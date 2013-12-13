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
    public static final String CONF_PROPERTIES_FILE = "conf.properties";
    private static final String XML_EXTENSION = ".xml";
    private static final String XSL_EXTENSION = ".xsl";
    private File baseFilesDirectory;
    private Properties props;

    public FilesRebrandingManager(String baseFilesDirectory) throws IOException {
        this.baseFilesDirectory = readDirectory(baseFilesDirectory);
        this.props = readPropertiesFromFile();
    }

    private File readDirectory(String baseFilesDirectory) throws FileNotFoundException {
        File directory = null;
        boolean isValidDirectory = baseFilesDirectory != null
                && baseFilesDirectory.length() > 0
                && ((directory = new File(baseFilesDirectory)).exists())
                && directory.isDirectory();

        if (isValidDirectory) {
            return directory;
        }
        throw new FileNotFoundException("Incorrect directory: " + baseFilesDirectory);
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
        List<File> files = getFilesForRebranding(baseFilesDirectory);
        if (files.size() > 0) {
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future> futures = new ArrayList<Future>(files.size());
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

    private List<File> getFilesForRebranding(File filesDirectory) {
        List<File> result = new ArrayList<File>();
        for (File file : filesDirectory.listFiles()) {
            if (file.isDirectory()) {
                result.addAll(getFilesForRebranding(file));
            } else {
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(XML_EXTENSION) || fileName.endsWith(XSL_EXTENSION)) {
                    result.add(file);
                }
            }
        }
        return result;
    }
}
