package eu.europa.ec.isa2.oop.restapi.dao.utils;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

/**
 * Class generates DDL script for SMP. Purpose of script is to manually run SQL script to create database. And to
 * give more Database Administrators opportunity to enhance script before executing on the database.
 *
 * @author Joze Rihtarsic
 * @since 1.0
 */
public class DSDSchemaGenerator {
    private static String filenameTemplate = "%s.ddl";
    private static String filenameDropTemplate = "%s-drop.ddl";
    private static final Logger LOG = LoggerFactory.getLogger(DSDSchemaGenerator.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException {


        String strDialects = args[0]; // comma separated dialects
        String entityPackageNames = args.length > 1 ? args[1] : ""; // comma separated entity packages
        String strVersion = args.length > 2 ? args[2] : "";  // version
        String exportFolder = args.length > 3 ? args[3] : ""; // export folder

        if (StringUtils.isBlank(strDialects)) {
            LOG.error("Second parameter [comma separated dialects] must not be null!");
            System.exit(1);
        }

        if (StringUtils.isBlank(entityPackageNames)) {
            LOG.error("Second parameter [comma separated entity packages] must not be null!");
            System.exit(2);
        }

        // execute
        DSDSchemaGenerator sg = new DSDSchemaGenerator();
        String[] dialects = strDialects.split("\\s*,\\s*");
        List<String> entityPackages = Arrays.asList(entityPackageNames.split("\\s*,\\s*"));
        for (String dialect : dialects) {
            sg.createDDLScript(exportFolder, dialect.trim(), entityPackages, strVersion);
        }

        System.exit(0);
    }

    /**
     * Create and export DDL script for hibernate dialects.
     *
     * @param exportFolder
     * @param hibernateDialect
     * @param packageNames
     * @param version
     */
    public void createDDLScript(String exportFolder, String hibernateDialect, List<String> packageNames, String version) throws ClassNotFoundException, IOException {
        // create export file
        System.setProperty("spring.jpa.hibernate.ddl-auto", "create");
        System.setProperty("hibernate.hbm2ddl.auto", "create");

        String filename = createFileName(hibernateDialect);
        String filenameDrop = createDropFileName(hibernateDialect);

        String dialect = getDialect(hibernateDialect);

        // metadata source
        MetadataSources metadata = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .applySetting("hibernate.dialect", dialect)
                        .applySetting("hibernate.hbm2ddl.auto", "create")
                        .build());

        // add annonated classes
        for (String pckName : packageNames) {
            // metadata.addPackage did not work...
            List<Class> clsList = getAllEntityClasses(pckName);
            for (Class clazz : clsList) {
                metadata.addAnnotatedClass(clazz);
            }
        }
        // build metadata implementor
        MetadataImplementor metadataImplementor = (MetadataImplementor) metadata.buildMetadata();

        // add column description
        metadata.getAnnotatedClasses().forEach(clzz -> {
            for (Field fld : clzz.getDeclaredFields()) {

                updateColumnComment(clzz.getName(), fld, metadataImplementor);
            }
        });

        // create schema exporter
        SchemaExport export = new SchemaExport();


        File file = new File(exportFolder, filename);
        if (file.delete()) { // delete if exists
            LOG.debug("File {} deleted - new will be generated!", file.getAbsolutePath());
        }

        File fileDrop = new File(exportFolder, filenameDrop);
        if (fileDrop.delete()) { // delete if exists
            LOG.debug("File {} deleted - new will be generated!", fileDrop.getAbsolutePath());
        }

        LOG.info("Export DDL script with dialect: {} to file: {}", dialect, file.getAbsolutePath());
        export.setOutputFile(file.getAbsolutePath());
        export.setFormat(true);
        export.setDelimiter(";");

        //chan change the output here
        EnumSet<TargetType> enumSet = EnumSet.of(TargetType.SCRIPT);
        export.execute(enumSet, SchemaExport.Action.CREATE, metadataImplementor);
        // prepend comment to file with wersion
        prependComment(file, String.format("-- This file was generated by hibernate for: [%s].\n", version));

        // create drop script
        export.setOutputFile(fileDrop.getAbsolutePath());
        export.execute(enumSet, SchemaExport.Action.DROP, metadataImplementor);
        prependComment(fileDrop, String.format("-- This file was generated by hibernate for: [%s].\n", version));



    }


    private void updateColumnComment(String entityName, Field field, MetadataImplementor metadataImplementor) {
        javax.persistence.Column column = field.getAnnotation(javax.persistence.Column.class);
        ColumnDescription columnDesc = field.getAnnotation(ColumnDescription.class);
        if (column != null && columnDesc != null && !StringUtils.isBlank(columnDesc.comment())) {
            LOG.info("Get table for entity " + entityName + " column name: " + column.name());
            PersistentClass persistentClass = metadataImplementor.getEntityBinding(entityName);
            if (persistentClass != null) {
                Column c = persistentClass.getTable().getColumn(Identifier.toIdentifier(column.name(), false));
                // remove quotations from description
                c.setComment(columnDesc.comment().replaceAll("'", ""));
            }
        }
    }


    /**
     * Method creates filename based on dialect and version
     *
     * @param dialect
     * @return file name.
     */
    public String createFileName(String dialect) {
        String dbName = dialect.substring(dialect.lastIndexOf('.') + 1, dialect.lastIndexOf("Dialect")).toLowerCase();
        return String.format(filenameTemplate, dbName);
    }

    public String createDropFileName(String dialect) {
        String dbName = dialect.substring(dialect.lastIndexOf('.') + 1, dialect.lastIndexOf("Dialect")).toLowerCase();
        return String.format(filenameDropTemplate, dbName);
    }

    /**
     * Some dialect are customized in order to generate better SQL DDL script. Method check the dialect and returns
     * the upgraded dialect
     *
     * @param dialect - original hibernate dialect
     * @return return the customized dialect or the dialects itself if not costumization
     */
    public String getDialect(String dialect) {
        if (!StringUtils.isBlank(dialect) && dialect.equalsIgnoreCase("org.hibernate.dialect.MySQL5InnoDBDialect")) {
            return "eu.europa.ec.bdmsl.dao.utils.SMLMySQL5InnoDBDialect";
        } else {
            return dialect;
        }
    }

    /***
     * Returns list of classes with entity anotation in package and subpackages.
     * @param packageName
     * @return
     *
     * Method source
     * https://dzone.com/articles/get-all-classes-within-package
     */


    public List<Class> getAllEntityClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        if (dirs.isEmpty()) {
            throw new ClassNotFoundException("Package: " + packageName + " not exist.");
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                Class clazz = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                if (clazz.isAnnotationPresent(Entity.class)) {
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }


    public static void prependComment(File input, String prefix) throws IOException {
        LineIterator li = FileUtils.lineIterator(input);
        File tempFile = File.createTempFile("prependPrefix", ".tmp");
        BufferedWriter w = new BufferedWriter(new FileWriter(tempFile));
        try {
            w.write(prefix);
            while (li.hasNext()) {
                w.write(li.next());
                w.write("\n");
            }
        } finally {
            IOUtils.closeQuietly(w);
            LineIterator.closeQuietly(li);
        }
        FileUtils.deleteQuietly(input);
        FileUtils.moveFile(tempFile, input);
    }
}
