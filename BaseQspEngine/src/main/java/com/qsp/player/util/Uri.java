package com.qsp.player.util;

import java.io.File;

public class Uri {

    private File mFile;

    public Uri(File file) {
        this.mFile = file;
    }

    public File getmFile() {
        return mFile;
    }

    public static Uri fromFile(File file) {

        return new Uri(file);
    }
}
