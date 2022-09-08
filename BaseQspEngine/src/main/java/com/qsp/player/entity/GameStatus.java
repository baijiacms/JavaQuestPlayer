package com.qsp.player.entity;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author baijiacms
 */
public class GameStatus {


    private boolean isOpenSaveWindow = false;
    private boolean isSobGame = false; //sob游戏时候特殊处理
    private boolean isBigKuyash = false; //BIG_KUYASH游戏时候特殊处理


    private String gameTitle;
    private boolean gameRunning;

    private boolean showWindow;
    private boolean showMenu;
    private volatile long gameStartTime;
    private volatile long lastMsCountCallTime;
    private String mainDesc = "";
    private String varsDesc = "";
    private ArrayList<QspListItem> actions = new ArrayList<>();
    private ArrayList<QspListItem> objects = new ArrayList<>();
    private ArrayList<QspMenuItem> menuItems = new ArrayList<>();


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

    public boolean isOpenSaveWindow() {
        return isOpenSaveWindow;
    }

    public void setOpenSaveWindow(boolean openSaveWindow) {
        isOpenSaveWindow = openSaveWindow;
    }

    public boolean isSobGame() {
        return isSobGame;
    }

    public void setSobGame(boolean sobGame) {
        isSobGame = sobGame;
    }

    public boolean isBigKuyash() {
        return isBigKuyash;
    }

    public void setBigKuyash(boolean bigKuyash) {
        isBigKuyash = bigKuyash;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public boolean isShowWindow() {
        return showWindow;
    }

    public void setShowWindow(boolean showWindow) {
        this.showWindow = showWindow;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    public long getGameStartTime() {
        return gameStartTime;
    }

    public void setGameStartTime(long gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public long getLastMsCountCallTime() {
        return lastMsCountCallTime;
    }

    public void setLastMsCountCallTime(long lastMsCountCallTime) {
        this.lastMsCountCallTime = lastMsCountCallTime;
    }

    public String getMainDesc() {
        return mainDesc;
    }

    public void setMainDesc(String mainDesc) {
        this.mainDesc = mainDesc;
    }

    public String getVarsDesc() {
        return varsDesc;
    }

    public void setVarsDesc(String varsDesc) {
        this.varsDesc = varsDesc;
    }

    public ArrayList<QspListItem> getActions() {
        return actions;
    }

    public ArrayList<QspListItem> getObjects() {
        return objects;
    }

    public ArrayList<QspMenuItem> getMenuItems() {
        return menuItems;
    }

    public AtomicBoolean getMaindescchanged() {
        return maindescchanged;
    }

    public AtomicBoolean getActionschanged() {
        return actionschanged;
    }

    public AtomicBoolean getObjectschanged() {
        return objectschanged;
    }

    public AtomicBoolean getVarsdescchanged() {
        return varsdescchanged;
    }

    public void setActions(ArrayList<QspListItem> actions) {
        this.actions = actions;
    }

    public void setObjects(ArrayList<QspListItem> objects) {
        this.objects = objects;
    }

    public void setMenuItems(ArrayList<QspMenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public void setMaindescchanged(AtomicBoolean maindescchanged) {
        this.maindescchanged = maindescchanged;
    }

    public void setActionschanged(AtomicBoolean actionschanged) {
        this.actionschanged = actionschanged;
    }

    public void setObjectschanged(AtomicBoolean objectschanged) {
        this.objectschanged = objectschanged;
    }

    public void setVarsdescchanged(AtomicBoolean varsdescchanged) {
        this.varsdescchanged = varsdescchanged;
    }
}
