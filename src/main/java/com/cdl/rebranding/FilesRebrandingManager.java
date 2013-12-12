package com.cdl.rebranding;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FilesRebrandingManager {

    private File filesDirectory;

    public FilesRebrandingManager(String filesDirectory) {
        //todo check directory
        this.filesDirectory = new File(filesDirectory);
        //todo read rebranding config
    }

    public void startRebranding() {
        File[] files = getFiles();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future> futures = new ArrayList<Future>(files.length);
        for (File file : files) {
            Thread worker = null;
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".xml")) {
                worker = new XMLFileRebrandingWorker(file);
            } else if (fileName.endsWith(".xsl")) {
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

    private File[] getFiles() {
        //todo get files from subdirectories
        return filesDirectory.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getName().toLowerCase().endsWith(".xml") || file.getName().toLowerCase().endsWith(".xsl");
            }
        });
    }
}
