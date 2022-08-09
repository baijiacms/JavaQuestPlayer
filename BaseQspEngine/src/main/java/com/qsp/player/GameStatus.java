package com.qsp.player;

import com.qsp.player.common.QspConstants;
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
    public boolean IS_SOB_GAME = false; //sob游戏时候特殊处理
    public boolean IS_BIG_KUYASH = false; //BIG_KUYASH游戏时候特殊处理
    public boolean isProxy = false;

    public boolean isStart = false;
    public String GAME_RESOURCE_PATH;
    public String GAME_FILE;
    public String GAME_ID = "default";
    public String GAME_TITLE = "default";
    public String GAME_VERSION = "1.0.0";
    public String URL_BASE_URL;
    public String URL_REPLACE_URL;

    private void setGameResource(String gameId) {
        this.GAME_ID = gameId;
        this.GAME_RESOURCE_PATH = QspConstants.GAME_DATA_PATH + this.GAME_ID;
        URL_BASE_URL = "file:///" + this.GAME_RESOURCE_PATH;
        URL_REPLACE_URL = QspConstants.HTTP_LOCAL_URL;
//        if(this.isProxy) {
//            URI.BASE_URL= "file:///"+ this.GAME_RESOURCE_PATH;
//            URI.REPLACE_URL=LOCAL_URL;
//        }
        GAME_FILE = GAME_RESOURCE_PATH + "/game.qsp";
    }

    public String getSaveFolder() {
        String saveFolder = this.GAME_RESOURCE_PATH + "/saves/";
        new File(saveFolder).mkdir();
        return saveFolder;
    }

    public void setGamePathById(String gameId) {
        setGameResource(gameId);
        String gameFolder = this.GAME_RESOURCE_PATH + "/";
        File[] files = new File(gameFolder).listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
            } else {
                if (f.isFile() && f.getPath().endsWith(".qsp")) {
                    this.GAME_FILE = f.getAbsolutePath();
                }
            }
        }
        File configFile = new File(this.GAME_RESOURCE_PATH + "/game.ini");
        boolean isReadConfig = false;
        if (configFile.exists()) {
            isReadConfig = true;
            try {
                Properties properties = new Properties();
                properties.load(new FileReader(configFile));
                if (StringUtils.isEmpty(properties.getProperty("GAME_NAME")) == false) {
                    this.GAME_TITLE = properties.getProperty("GAME_NAME");
                    this.GAME_VERSION = properties.getProperty("GAME_VERSION");
                    String gameIsSob = properties.getProperty("GAME_IS_SOB");
                    if (StringUtils.isEmpty(gameIsSob) == false) {
                        this.IS_SOB_GAME = Boolean.valueOf(gameIsSob);
                    }
                    String gameIsBigKuyash = properties.getProperty("GAME_IS_BIG_KUYASH");
                    if (StringUtils.isEmpty(gameIsBigKuyash) == false) {
                        this.IS_BIG_KUYASH = Boolean.valueOf(gameIsBigKuyash);
                    }
                } else {
                    isReadConfig = false;
                }
            } catch (IOException e) {
                isReadConfig = false;
            }
        }

        if (isReadConfig == false) {
            this.GAME_TITLE = gameId;
            this.GAME_VERSION = "1.0.0";
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
