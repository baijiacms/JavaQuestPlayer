package com.baijiacms.qsp.player.service;

import com.baijiacms.qsp.util.FileUtil;

import java.io.File;

public class GameContentResolver {
    private File gameDir;

    public File getFile(String relPath) {
        if (gameDir == null) {
            throw new IllegalStateException("gameDir must not be null");
        }
        return FileUtil.findFileRecursively(gameDir, normalizeContentPath(relPath));
    }

    private String normalizeContentPath(String path) {
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
