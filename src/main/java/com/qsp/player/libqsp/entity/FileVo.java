package com.qsp.player.libqsp.entity;


import java.io.File;

/**
 * @author baijiacms
 */
public class FileVo {
    File file;
    File folder;
    boolean isQproj;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }

    public boolean isQproj() {
        return isQproj;
    }

    public void setQproj(boolean qproj) {
        isQproj = qproj;
    }
}
