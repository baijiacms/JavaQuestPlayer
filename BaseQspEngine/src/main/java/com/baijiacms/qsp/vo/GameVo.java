package com.baijiacms.qsp.vo;

import lombok.Data;

/**
 * @author cxy
 */
@Data
public class GameVo {
    private String gameId;
    private String gameName;
    private String gameFolder;
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
