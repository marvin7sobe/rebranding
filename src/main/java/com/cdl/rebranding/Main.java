package com.cdl.rebranding;

import com.cdl.rebranding.api.FilesRebrandingManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        FilesRebrandingManager rebrandingManager = new FilesRebrandingManager(args[0]);
        rebrandingManager.startRebranding();
    }
}
