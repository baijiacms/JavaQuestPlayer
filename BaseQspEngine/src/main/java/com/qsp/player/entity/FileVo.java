package com.qsp.player.entity;

import lombok.Data;

import java.io.File;
/**
 * @author baijiacms
 */
@Data
public class FileVo {
    File file;
    File folder;
    boolean isQproj;
}
