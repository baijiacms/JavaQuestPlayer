package com.qsp.player;

import com.qsp.player.common.QspConstants;
import com.qsp.player.util.Uri;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameStatus {

    public volatile long gameStartTime;
    public volatile long lastMsCountCallTime;
    public boolean isOpenSaveWindow = false;
    public boolean isSobGame = false; //sob游戏时候特殊处理
    public boolean isBigKuyash = false; //BIG_KUYASH游戏时候特殊处理
    public boolean isProxy = false;

    public boolean isStart = false;
    public String gameResourcePath;
    public String gameFile;
    public String gameId = "default";
    public String gameTitle = "default";
    public String gameVersion = "1.0.0";

    private void setGameResource(String gameId) {
        this.gameId = gameId;
        this.gameResourcePath = Uri.getFolderPath(QspConstants.GAME_DATA_PATH) + this.gameId;

//        if(this.isProxy) {
//            URI.BASE_URL= "file:///"+ this.GAME_RESOURCE_PATH;
//            URI.REPLACE_URL=LOCAL_URL;
//        }

        gameFile = Uri.getFilePath(gameResourcePath, "game.qsp");
        ;
    }

    public String getSaveFolder() {
        String saveFolder = this.gameResourcePath + "/saves/";
        Uri.getFolderFile(saveFolder).mkdir();
        return saveFolder;
    }

    public void setGamePathById(String gameId) {
        setGameResource(gameId);
        String gameFolder = Uri.getFolderPath(this.gameResourcePath);
        File[] files = new File(gameFolder).listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
            } else {
                if (f.isFile() && f.getPath().endsWith(".qsp")) {
                    this.gameFile = f.getAbsolutePath();
                }
            }
        }
        File configFile = new File(this.gameResourcePath + "/game.ini");
        boolean isReadConfig = false;
        if (configFile.exists()) {
            isReadConfig = true;
            try {
                Properties properties = new Properties();
                properties.load(new FileReader(configFile));
                if (StringUtils.isEmpty(properties.getProperty("GAME_NAME")) == false) {
                    this.gameTitle = properties.getProperty("GAME_NAME");
                    this.gameVersion = properties.getProperty("GAME_VERSION");
                    String gameIsSob = properties.getProperty("GAME_IS_SOB");
                    if (StringUtils.isEmpty(gameIsSob) == false) {
                        this.isSobGame = Boolean.valueOf(gameIsSob);
                    }
                    String gameIsBigKuyash = properties.getProperty("GAME_IS_BIG_KUYASH");
                    if (StringUtils.isEmpty(gameIsBigKuyash) == false) {
                        this.isBigKuyash = Boolean.valueOf(gameIsBigKuyash);
                    }
                } else {
                    isReadConfig = false;
                }
            } catch (IOException e) {
                isReadConfig = false;
            }
        }

        if (isReadConfig == false) {
            this.gameTitle = gameId;
            this.gameVersion = "1.0.0";
        }
    }


    public AtomicBoolean maindescchanged = new AtomicBoolean(false);
    public AtomicBoolean actionschanged = new AtomicBoolean(false);
    public AtomicBoolean objectschanged = new AtomicBoolean(false);
    public AtomicBoolean varsdescchanged = new AtomicBoolean(false);

    public boolean isMaindescchanged(boolean defaultValue) {
        return maindescchanged.getAndSet(defaultValue);

    }


    public boolean isActionschanged() {
        return actionschanged.get();
    }

    public void setActionschanged(boolean value) {

        actionschanged.set(value);
    }

    public boolean isObjectschanged() {
        return objectschanged.get();
    }

    public void setObjectschanged(boolean value) {

        objectschanged.set(value);
    }

    public boolean isVarsdescchanged() {
        return varsdescchanged.get();
    }

    public void setVarsdescchanged(boolean value) {

        varsdescchanged.set(value);
    }

    public void refreshAll() {
        maindescchanged.getAndSet(true);
        actionschanged.set(true);
        objectschanged.set(true);
        varsdescchanged.set(true);
    }
}
