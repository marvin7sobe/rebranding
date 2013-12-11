package com.cdl.rebranding;

public class Main {
    public static void main(String[] args){
        FilesRebrandingManager rebrandingManager = new FilesRebrandingManager(args[0]);
        rebrandingManager.startRebranding();

    }
}
