package com.qsp.player.entity;

import lombok.Data;

import java.io.File;

@Data
public class FileVo {
    File file;
    File folder;
    boolean isQproj;
}
