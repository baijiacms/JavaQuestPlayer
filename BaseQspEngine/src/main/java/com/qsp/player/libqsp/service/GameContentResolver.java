package com.qsp.player.libqsp.service;

import com.qsp.player.util.FileUtil;

import java.io.File;

public class GameContentResolver {
    private File gameDir;

    public File getFile(String relPath) {
        System.out.println(relPath);
        if (gameDir == null) {
            throw new IllegalStateException("gameDir must not be null");
        }
        return FileUtil.findFileRecursively(gameDir, normalizeContentPath(relPath));
    }

    public String getAbsolutePath(String relPath) {
        File file = getFile(relPath);
        return file != null ? file.getAbsolutePath() : null;
    }

    public static String normalizeContentPath(String path) {
        if (path == null) {
            return null;
        }

        String result = path;
        if (result.startsWith("./")) {
            result = result.substring(2);
        }

        return result.replace("\\", "/");
    }

    public void setGameDir(File gameDir) {
        this.gameDir = gameDir;
    }
}
