package com.qsp.player.entity;

import lombok.Data;
/**
 * @author baijiacms
 */
@Data
public class QspGame {
    private String gameId;
    private String gameName;
    private String gameFolder;
    private String gameSaveFolder;
    private String gameQproj;
    private String gameDevFolder;
    private String gameFile;
    private String gameDesc;
    private boolean isSob = false;
    private boolean isBigKuyash = false;
    private String qspPassword;
    private String gameVersion;
    private int isDevProject;
    private int isDev;

}
