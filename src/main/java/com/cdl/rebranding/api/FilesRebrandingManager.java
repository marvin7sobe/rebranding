package com.cdl.rebranding.api;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.cdl.rebranding.api.Utils.*;

public class FilesRebrandingManager {
    private File filesDirectory;
    private Properties props;

    public FilesRebrandingManager(String filesDirectory) throws IOException {
        this.filesDirectory = readDirectory(filesDirectory);
        this.props = readPropertiesFromFile();
    }

    private File readDirectory(String filesDirectory) throws FileNotFoundException {
        if (filesDirectory == null || filesDirectory.length() == 0 || !(new File(filesDirectory).exists())) {
            throw new FileNotFoundException("Incorrect directory path: " + filesDirectory);
        }
        return new File(filesDirectory);
    }

    private Properties readPropertiesFromFile() throws IOException {
        Properties result = new Properties();
        try {
            result.load(new FileInputStream(CONF_PROPERTIES_FILE));
        } catch (Exception e) {
            throw new IOException("Can not read properties file. File must be placed next to jar and has name '" + CONF_PROPERTIES_FILE + "' ");
        }
        return result;
    }

    public void startRebranding() {
        File[] files = getFilesForRebranding();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future> futures = new ArrayList<Future>(files.length);
        for (File file : files) {
            Runnable worker = null;
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(XML_EXTENSION)) {
                worker = new XMLFileRebrandingWorker(file, props);
            } else if (fileName.endsWith(XSL_EXTENSION)) {
                //todo xsl rebranding
            }
            if (worker != null) {
                futures.add(executorService.submit(worker));
            }
        }

        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                System.out.println(e);
            } catch (ExecutionException e) {
                System.out.println(e);
            }
        }
        executorService.shutdown();
    }

    private File[] getFilesForRebranding() {
        return filesDirectory.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getName().toLowerCase().endsWith(XML_EXTENSION) || file.getName().toLowerCase().endsWith(XSL_EXTENSION);
            }
        });
    }
}
