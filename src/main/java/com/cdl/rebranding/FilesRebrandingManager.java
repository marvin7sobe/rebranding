package com.cdl.rebranding;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FilesRebrandingManager {

    private File filesDirectory;

    public FilesRebrandingManager(String filesDirectory) {
        //todo check directory
        this.filesDirectory =  new File(filesDirectory);
        //todo read rebranding config
    }

    public void startRebranding(){
        File[] files = getFiles();

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future> futures = new ArrayList<Future>(files.length);
        for(File file: files){
            futures.add(executorService.submit(new FileRebrandingWorker(file)));
        }
        for(Future future:futures){
            try {
                future.get();
            } catch (InterruptedException e) {
                System.out.println(e);
            } catch (ExecutionException e) {
                System.out.println(e);
            }
        }

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
