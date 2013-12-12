package com.cdl.rebranding;

import com.cdl.rebranding.api.FilesRebrandingManager;

public class Main {
    public static void main(String[] args){
        FilesRebrandingManager rebrandingManager = new FilesRebrandingManager(args[0]);
        rebrandingManager.startRebranding();

    }
}
