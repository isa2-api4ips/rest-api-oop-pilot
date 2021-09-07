package eu.europa.ec.isa2.restapi.utils;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.io.File.createTempFile;
import static java.io.File.separator;
import static java.lang.String.format;

/**
 * Class handles local storage for binaries attached to mail. Local storage is
 * in ${laurentius.home}/storage folder. Binaries are stored under creation
 * date-named [yyyy/MM/dd_000001] folders. If in folder is more than
 * MAX_FILES_IN_FOLDER files new subfolder is created 001, subfolder has 3
 * digits with leading '0' for number lower than 1000. 001, 012, 898, 2215,
 * 3656, etc
 *
 *
 * @author Joze Rihtarsic <joze.rihtarsic@sodisce.si>
 */
public class StorageUtils {



    private static final AtomicInteger CURRENT_SUB_FOLDER_NUMBER = new AtomicInteger();
    private static final AtomicInteger CURRENT_FILE_COUNT_IN_FOLDER = new AtomicInteger();


    public static final int MAX_FILES_IN_FOLDER = 1024;
    private static final Logger LOG = LoggerFactory.getLogger(StorageUtils.class);
    private static final String S_IN_PREFIX = "IN_";
    private static final String S_OUT_PREFIX = "OUT_";
    private static final String S_ERR_PREFIX = "ERR_";

    private static Path sCurrentPath;
    final File storageLocationFile;


    public StorageUtils(String storageLocation) {
        Objects.requireNonNull(storageLocation, "Storage location must not be null");
        File storageLocationFile = new File(storageLocation);
        if (!storageLocationFile.exists() && !storageLocationFile.mkdirs()) {
            throw new  StorageException("Folder ["+storageLocation+"] does not exists and can not be created!");
        }
        if (!storageLocationFile.isDirectory() ) {
            throw new  StorageException("Folder ["+storageLocation+"] is not folder!");
        }
        this.storageLocationFile = storageLocationFile;
     //   this.storageLocation = storageLocation;
    }

    /**
     * Current storage folder.
     *
     * @return root folder as defined in system property:
     * ${laurentius.home}/storage/[CURRENT FOLDER - date]/
     * @throws StorageException - if current folder not exists and could not be
     * created
     */
    public File currentStorageFolder()
            throws StorageException {

        LocalDate cld = LocalDate.now();
        // current date

        Path pcDir = dateStorageFolder(cld);
        assert pcDir!= null: "Date storage folder must not be null!";


        // check if current path is 'todays' path
        if (sCurrentPath != null && !sCurrentPath.startsWith(pcDir)) {
            sCurrentPath = null;
        }
        // create new path
        if (sCurrentPath == null) {
            int i = getMaxSubFolderNumber(pcDir);
            sCurrentPath = pcDir.resolve(format("%03d", i)).toAbsolutePath();
            if (sCurrentPath.toFile().exists()) {

                try (Stream strPaths = Files.list(sCurrentPath)) {
                    CURRENT_FILE_COUNT_IN_FOLDER.set((int) strPaths.count());
                } catch (IOException ex) {
                    throw new StorageException(
                            format("Error occurred while counting files if storage folder: '%s'",
                                    sCurrentPath.toFile()), ex);
                }
            } else {
                CURRENT_FILE_COUNT_IN_FOLDER.set(0);
            }
        }

        // check max number
        if (Files.exists(sCurrentPath, LinkOption.NOFOLLOW_LINKS)
                && CURRENT_FILE_COUNT_IN_FOLDER.get() >= MAX_FILES_IN_FOLDER //Files.list(sCurrentPath).count() > MAX_FILES_IN_FOLDER
        ) {

            int i = getMaxSubFolderNumber(pcDir) + 1;
            sCurrentPath = pcDir.resolve(format("%03d", i)).normalize().
                    toAbsolutePath();
            CURRENT_FILE_COUNT_IN_FOLDER.set(0);

        }

        File f = sCurrentPath.toFile();
        if (!f.exists() && !f.mkdirs()) {
            throw new StorageException(
                    format("Error occurred while creating current storage folder: '%s'",
                            f.getAbsolutePath()));
        }
        return f;
    }

    /**
     * Method returs path for LocalDate
     * ${laurentius.home}/storage/[year]/[month]/[day]
     *
     * @param ld - Local date
     * @return returs path
     */
    protected Path dateStorageFolder(LocalDate ld) {
        return Paths.get(this.storageLocationFile.getAbsolutePath(),
                ld.getYear() + "",
                format("%02d", ld.getMonthValue()),
                format("%02d", ld.getDayOfMonth()));
    }

    /**
     * File to relative storage path ${laurentius.home}/[storagePath]
     *
     * @param storagePath
     * @return File to to relative storage path
     */
    public File getFile(String storagePath) {
        return new File(this.storageLocationFile, storagePath);
    }

    /**
     * Method returs max subfolder number. If storage folder has more than
     * MAX_FILES_IN_FOLDER new subfolder is created. Method resturms maxnumber if
     * subfolder number in current date folder
     * ${laurentius.home}/storage/[year]/[month]/[day]/[number]
     *
     * @param dateDir - Current date dir
     * ${laurentius.home}/storage/[year]/[month]/[day]
     * @return returs max subfolder number. If there is no subfolder method returs
     * 1
     */
    protected int getMaxSubFolderNumber(Path dateDir) {
        
        int iMaxFolder = 1;

        if (dateDir != null && Files.exists(dateDir, LinkOption.NOFOLLOW_LINKS) && Files.
                isDirectory(
                        dateDir, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dateDir,
                    (Path file)
                            -> (Files.isDirectory(file)))) {
                for (Path path : stream) {
                    String name = path.getFileName().toString();
                    try {
                        int i = Integer.parseInt(name);
                        iMaxFolder = i > iMaxFolder ? i : iMaxFolder;
                    } catch (NumberFormatException ne) {
                        LOG.warn(format("Subfolder '%s' is not a number! Path: %s",
                                name,
                                dateDir.toFile().getAbsolutePath()), ne);
                    }
                }
            } catch (IOException e) {
                LOG.warn(format("Error reading subfolders for path '%s'",
                        dateDir.toFile().getAbsolutePath()), e);
            }
        }
        return iMaxFolder;
    }

    /**
     * create new empty file in current storage folder
     *
     * @param suffix - file suffix (usually mime suffix as .pdf, .xml, etc. )
     * @param prefix - file prefix (in_/out_)
     * @return new storage file
     * @throws StorageException
     */
    public  File getNewStorageFile(String suffix, String prefix)
            throws StorageException {
        File fStore;
        try {
            fStore = createTempFile(prefix, "." + suffix, currentStorageFolder());
            CURRENT_FILE_COUNT_IN_FOLDER.incrementAndGet();
        } catch (IOException ex) {
            throw new StorageException("Error occurred while creating storage file",ex);
        }
        return fStore;
    }

    /**
     * Returns relative string path for file
     *
     * @param path - input file in storage
     * @return String - relative path
     * @throws StorageException
     */
    public String getRelativePath(File path)
            throws StorageException {

        if (path.getAbsolutePath().startsWith(storageLocationFile.getAbsolutePath())) {
            String rp = path.getAbsolutePath().substring(storageLocationFile.getAbsolutePath().
                    length());
            rp = rp.startsWith(separator) ? rp.substring(1) : rp;
            return rp;
        } else {
            throw new StorageException(format("File: '%s' is not in storage '%s'",
                    path.getAbsolutePath(), storageLocationFile.getAbsolutePath()));
        }
    }

    /**
     * Mehotd return ${laurentius.home}/storage folder as File object.
     *
     * @return return storage folder
     */
    public File getStorageFolder() {
        return storageLocationFile;
    }

    /**
     * delete file in storage.
     *
     * @param strInFileName - relative file path
     * @throws StorageException
     */
    public void removeFile(String strInFileName)
            throws StorageException {
        File f = getFile(strInFileName);
        if (!f.isFile()) {
            throw new StorageException(format("Path %s is not a file", f.
                    getAbsoluteFile()));
        }
        if (!f.delete()) {
            throw new StorageException(format("File %s was not deleted", f.
                    getAbsoluteFile()));
        }
    }

    /**
     * Copy file to target file
     *
     * @param sourceFile - source - the file to copy
     * @param destFile - target file (may be associated with a different provider
     * to the source path)
     * @param replaceExisting - replace if target file exists
     * @throws StorageException, file already
     * exists, error reading file
     */
    public static void copyFile(File sourceFile, File destFile,
                                boolean replaceExisting)
            throws StorageException {

        try {
            if (replaceExisting) {
                Files.copy(sourceFile.toPath(), destFile.toPath(),
                        StandardCopyOption.COPY_ATTRIBUTES,
                        StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(sourceFile.toPath(), destFile.toPath(),
                        StandardCopyOption.COPY_ATTRIBUTES);
            }
        } catch (FileAlreadyExistsException fe) {
            throw new StorageException(format(
                    "Could not copy! Dest file already exists: '%s'",
                    destFile.getAbsolutePath()), fe);
        } catch (IOException ex) {
            throw new StorageException(format("Error copying file!: '%s' to file: %s",
                    sourceFile.getAbsolutePath(), destFile.getAbsolutePath()), ex);
        }

    }

    /**
     * Copy relative storage file to dest folder.
     *
     * @param storageFilePath - relative storage file path
     * @param folder - deset folder
     * @return dest file
     * @throws StorageException - if dest folder could not be created of file
     * could not be copied to dest folder
     */
    public File copyFileToFolder(String storageFilePath, File folder)
            throws StorageException {

        return copyFileToFolder(storageFilePath, folder, false);
    }

    /**
     * Copy relative storage file to dest folder.
     *
     * @param storageFilePath - relative storage file path
     * @param folder - deset folder
     * @param bOverWrite . overwrite dest file if exists
     * @return dest file
     * @throws StorageException - if dest folder could not be created of file
     * could not be copied to dest folder
     */
    public File copyFileToFolder(String storageFilePath, File folder,
                                 boolean bOverWrite)
            throws StorageException {

        return copyFileToFolder(storageFilePath, folder, bOverWrite, null);
    }

    public File copyFileToFolder(String storageFilePath, File folder,
                                 boolean bOverWrite, String targetFilename)
            throws StorageException {

        if (!folder.exists() && !folder.mkdirs()) {
            throw new StorageException(format(
                    "Could not create dest folder: '%s' to copy file: '%s'.",
                    folder.getAbsolutePath(), storageFilePath));
        }
        File srcFile = getFile(storageFilePath);
        File destFile = new File(folder,
                StringUtils.isEmpty(targetFilename) ? srcFile.getName() : targetFilename);
        File pf = destFile.getParentFile();
        if (!pf.exists() && !pf.mkdirs()) {
            throw new StorageException(format("Could not create folder '%s'", pf.
                    getAbsolutePath()));
        }

        copyFile(srcFile, destFile, bOverWrite);
        return destFile;
    }

    /**
     * Reads file content to byteArray. Avoid this method if possible especially
     * for large files! Handle files as streams instead
     *
     * @param storagePath - relative storage paths
     * @return file content
     * @throws StorageException error reading file
     */
    public byte[] getByteArray(String storagePath)
            throws StorageException {

        File f = getFile(storagePath);
        byte[] bin;
        try {
            bin = Files.readAllBytes(f.toPath());
        } catch (IOException ex) {
            throw new StorageException("Error occurred while creating storage file",
                    ex);
        }
        return bin;
    }

    /**
     * Store byteArray to storage. Avoid using this method. Handle file contetn as
     * streams!
     *
     * @param suffix - file suffix (usually mime suffix as .pdf, .xml, etc. )
     * @param prefix - file prefix (in_/out_)
     * @param buffer
     * @return Storage File
     * @throws StorageException error storage files
     */
    public File storeFile(String prefix, String suffix, byte[] buffer)
            throws StorageException {
        return storeFile(prefix, suffix, new ByteArrayInputStream(buffer));
    }

    /**
     * Store stream to storage.
     *
     * @param suffix - file suffix (usually mime suffix as .pdf, .xml, etc. )
     * @param prefix - file prefix (in_/out_)
     * @param inStream content stream
     * @return Storage File
     * @throws StorageException error storage files
     */
    public File storeFile(String prefix, String suffix, InputStream inStream)
            throws StorageException {
        File fStore = getNewStorageFile(suffix, prefix);

        try (FileOutputStream fos = new FileOutputStream(fStore)) {

            byte[] buffer = new byte[1024];
            int len = inStream.read(buffer);
            while (len != -1) {
                fos.write(buffer, 0, len);
                len = inStream.read(buffer);
            }

        } catch (IOException ex) {
            throw new StorageException(format(
                    "Error occurred while writing to file: '%s'",
                    fStore.getAbsolutePath()));
        }
        return fStore;
    }

    /**
     * Copy file as input file to storage!
     *
     * @param mimeType - mimetype of input file
     * @param fIn - input file
     * @return storage file
     * @throws StorageException error occured copying file!
     */
    public File storeInFile(String mimeType, File fIn)
            throws StorageException {
        if (!fIn.exists()) {
            throw new StorageException(format("Source 'IN' file: '%s' not exists ",
                    fIn.getAbsolutePath()));
        }
        File fStore = getNewStorageFile(getSuffixBYMimeType(mimeType), S_IN_PREFIX);
        copyFile(fIn, fStore, true);

        return fStore;
    }

    /**
     * Store input stream as input file to storage.
     *
     * @param mimeType - input mimetype
     * @param is - input stream
     * @return stored file
     * @throws StorageException error storing data from input stream to storage
     */
    public File storeInFile(String mimeType, InputStream is)
            throws StorageException {
        return storeFile(S_IN_PREFIX, getSuffixBYMimeType(mimeType), is);
    }

    /**
     * Store input stream as input file to storage.
     *
     * @param th
     * @return stored file
     * @throws StorageException error storing data from input stream to storage
     */
    public String storeThrowableAndGetRelativePath(Throwable th)
            throws StorageException {
        if (th == null) {
            return null;
        }

        File f = getNewStorageFile(MimeTypeUtils.TEXT_PLAIN_VALUE, S_ERR_PREFIX);

        try (PrintWriter fw = new PrintWriter(f)) {
            Throwable cs = th;
            String ident = " ";
            do {
                fw.append(ident);
                fw.append("Caused by:");
                fw.append(cs.getMessage());
                fw.append("\n");
                ident += "  ";
                StackTraceElement[] lst = cs.getStackTrace();
                for (int i = 0; i < lst.length && i < 50; i++) {
                    fw.append(ident);
                    fw.append(lst[i].toString());
                    fw.append("\n");
                }

            } while ((cs = cs.getCause()) != null);

        } catch (FileNotFoundException ex) {
            throw new StorageException(format("Error opening file:  '%s'!", f.
                    getAbsolutePath()));
        }
        return getRelativePath(f);

    }

    public File getCreateEmptyInFile(String mimeType)
            throws StorageException {
        return getNewStorageFile(getSuffixBYMimeType(mimeType), S_IN_PREFIX);
    }

    /**
     * Store bytearray to storage.
     *
     * @param mimeType - input mimetype
     * @param buff - bytes
     * @return stored file
     * @throws StorageException error storing data from input stream to storage
     */
    public File storeInFile(String mimeType, byte[] buff)
            throws StorageException {
        return storeFile(S_IN_PREFIX, getSuffixBYMimeType(mimeType),
                new ByteArrayInputStream(buff));
    }

    /**
     * Store output bytearray to storage.
     *
     * @param mimeType - file mimetype
     * @param buffer - file content
     * @return storage file
     * @throws StorageException -error occured storing content to storage
     */
    public File storeOutFile(String mimeType, byte[] buffer)
            throws StorageException {
        return storeFile(S_OUT_PREFIX, getSuffixBYMimeType(mimeType), buffer);
    }

    /**
     * Store/copy output file to storage.
     *
     * @param mimeType - output file mimetype
     * @param fOut - file to copy
     * @return - storage file
     * @throws StorageException - error occured while copying file to storage
     */
    public File storeOutFile(String mimeType, File fOut)
            throws StorageException {
        if (!fOut.exists()) {
            throw new StorageException(format("File in message: '%s' not exists ",
                    fOut.getAbsolutePath()));
        }
        File fStore = getNewStorageFile(S_OUT_PREFIX, getSuffixBYMimeType(mimeType));
        copyFile(fOut, fStore, true);
        return fStore;
    }
    
    protected String getSuffixBYMimeType(String mimeType){
        return MimeType.valueOf(mimeType).getSubtypeSuffix();
    }

}
