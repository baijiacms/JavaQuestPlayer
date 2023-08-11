package com.qsp.player.entity;

/**
 * @author baijiacms
 */
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
    private boolean isTower = false;
    private boolean isBigKuyash = false;
    private String qspPassword;
    private String gameVersion;
    private int isDevProject;
    private int isDev;
    private String gameLogo;


    public String getGameLogo() {
        return gameLogo;
    }

    public void setGameLogo(String gameLogo) {
        this.gameLogo = gameLogo;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameFolder() {
        return gameFolder;
    }

    public void setGameFolder(String gameFolder) {
        this.gameFolder = gameFolder;
    }

    public String getGameSaveFolder() {
        return gameSaveFolder;
    }

    public void setGameSaveFolder(String gameSaveFolder) {
        this.gameSaveFolder = gameSaveFolder;
    }

    public String getGameQproj() {
        return gameQproj;
    }

    public void setGameQproj(String gameQproj) {
        this.gameQproj = gameQproj;
    }

    public String getGameDevFolder() {
        return gameDevFolder;
    }

    public void setGameDevFolder(String gameDevFolder) {
        this.gameDevFolder = gameDevFolder;
    }

    public String getGameFile() {
        return gameFile;
    }

    public void setGameFile(String gameFile) {
        this.gameFile = gameFile;
    }

    public String getGameDesc() {
        return gameDesc;
    }

    public void setGameDesc(String gameDesc) {
        this.gameDesc = gameDesc;
    }

    public boolean isSob() {
        return isSob;
    }

    public void setSob(boolean sob) {
        isSob = sob;
    }

    public boolean isBigKuyash() {
        return isBigKuyash;
    }

    public void setBigKuyash(boolean bigKuyash) {
        isBigKuyash = bigKuyash;
    }

    public String getQspPassword() {
        return qspPassword;
    }

    public void setQspPassword(String qspPassword) {
        this.qspPassword = qspPassword;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public int getIsDevProject() {
        return isDevProject;
    }

    public void setIsDevProject(int isDevProject) {
        this.isDevProject = isDevProject;
    }

    public int getIsDev() {
        return isDev;
    }

    public void setIsDev(int isDev) {
        this.isDev = isDev;
    }

    public boolean isTower() {
        return isTower;
    }

    public void setTower(boolean tower) {
        isTower = tower;
    }
}
