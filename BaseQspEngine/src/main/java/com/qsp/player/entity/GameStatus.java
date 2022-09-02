package com.qsp.player.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * @author baijiacms
 */
@Data
public class GameStatus {
    private boolean isOpenSaveWindow = false;
    private boolean isSobGame = false; //sob游戏时候特殊处理
    private boolean isBigKuyash = false; //BIG_KUYASH游戏时候特殊处理

    private boolean gameRunning;
    private boolean showWindow;
    private boolean showMenu;
    public volatile long gameStartTime;
    public volatile long lastMsCountCallTime;
    private String gameTitle;
    public String mainDesc = "";
    public String varsDesc = "";
    public ArrayList<QspListItem> actions = new ArrayList<>();
    public ArrayList<QspListItem> objects = new ArrayList<>();
    public ArrayList<QspMenuItem> menuItems = new ArrayList<>();

    public boolean isGameRunning() {
        return gameRunning;
    }

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }


    public void reset() {
        isBigKuyash = false;
        isSobGame = false;
        isOpenSaveWindow = false;
        gameRunning = false;
        gameTitle = null;
        mainDesc = "";
        varsDesc = "";
        actions = new ArrayList<>();
        objects = new ArrayList<>();
        menuItems = new ArrayList<>();
    }


    private AtomicBoolean maindescchanged = new AtomicBoolean(false);
    private AtomicBoolean actionschanged = new AtomicBoolean(false);
    private AtomicBoolean objectschanged = new AtomicBoolean(false);
    private AtomicBoolean varsdescchanged = new AtomicBoolean(false);

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
