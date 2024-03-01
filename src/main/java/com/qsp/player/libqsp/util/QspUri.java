package com.qsp.player.libqsp.util;

import java.io.File;

/**
 * @author baijiacms
 */
public class QspUri {

    private File mFile;

    public QspUri(File file) {
        this.mFile = file;
    }

    public File getmFile() {
        return mFile;
    }

    public static String getFolderPath(String gameResourcePath) {
        gameResourcePath = gameResourcePath.replaceAll("\\\\", "/");
        if (gameResourcePath.endsWith("/")) {
            return gameResourcePath;
        } else {
            return gameResourcePath + "/";

        }
    }

    public static File getFolderFile(String gameResourcePath) {
        return new File(getFolderPath(gameResourcePath));
    }

    public static File getFile(String gameResourcePath, String path) {
        return new File(getFilePath(gameResourcePath, path));
    }

    public static String getFilePath(String gameResourcePath, String path) {
        if (path.length() > 1 && path.startsWith("/")) {
            path = path.substring(1);
        }
        return getFolderPath(gameResourcePath) + path;
    }

    public static QspUri toUri(File file) {

        return new QspUri(file);
    }
}
