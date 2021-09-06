package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.services;


import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.restapi.utils.StorageException;
import eu.europa.ec.isa2.restapi.utils.StorageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Class handles local storage for binaries attached to mail. Local storage is
 * in ${laurentius.home}/storage folder. Binaries are stored under creation
 * date-named [yyyy/MM/dd_000001] folders. If in folder is more than
 * MAX_FILES_IN_FOLDER files new subfolder is created 001, subfolder has 3
 * digits with leading '0' for number lower than 1000. 001, 012, 898, 2215,
 * 3656, etc
 *
 * @author Joze Rihtarsic <joze.rihtarsic@sodisce.si>
 */
@Service
public class StoragesService {
    private static final Logger LOG = LoggerFactory.getLogger(StoragesService.class);
    final StorageUtils storageUtils;

    @Autowired
    public StoragesService(NationalBrokerProperties properties) {
        storageUtils = new StorageUtils(properties.getStorageLocation());
    }

    public File getNewStorageFile(String suffix, String prefix)
            throws StorageException {
        return storageUtils.getNewStorageFile(suffix, prefix);
    }

    public String getRelativePath(File file)
            throws StorageException {
        return storageUtils.getRelativePath(file);
    }

    public File getFile(String storagePath) {
        return storageUtils.getFile(storagePath);
    }
}
