package com.cdl.rebranding.api;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FilesRebrandingManager {
    private static final String CONF_PROPERTIES_FILE = "conf.properties";
    private static final String XML_EXTENSION = ".xml";
    private static final String XSL_EXTENSION = ".xsl";
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
        if (files != null && files.length > 0) {
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future> futures = new ArrayList<Future>(files.length);
            for (File file : files) {
                futures.add(executorService.submit(new XMLFileRebrandingWorker(file, props)));
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
    }

    private File[] getFilesForRebranding() {
        return filesDirectory.listFiles(new FileFilter() {
            public boolean accept(File file) {
                String fileName = file.getName().toLowerCase();
                return fileName.endsWith(XML_EXTENSION) || fileName.endsWith(XSL_EXTENSION);
            }
        });
    }
}
