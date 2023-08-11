package com.qsp.player;

import com.qsp.player.common.QspConstants;
import com.qsp.player.entity.AudioObject;
import com.qsp.player.entity.GameStatus;
import com.qsp.player.entity.QspGame;
import com.qsp.player.entity.QspListItem;
import com.qsp.player.libqsp.LibQspProxy;
import com.qsp.player.libqsp.LibQspProxyImpl;
import com.qsp.player.util.Base64Util;
import com.qsp.player.util.FileUtil;
import com.qsp.player.util.QspUri;
import com.qsp.player.vi.QspAudio;
import com.qsp.player.vi.QspUi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class LibEngine {
    private static final Logger logger = LoggerFactory.getLogger(LibEngine.class);
    private LibQspProxy libQspProxy;
    private GameStatus gameStatus;
    private AudioObject audioObject;
    private QspGame qspGame;
    private QspUi qspUI;

    public LibEngine(int userId, QspUi qspUi, QspAudio qspAudio) {
        this.qspUI = qspUi;
        this.audioObject = new AudioObject(qspAudio);
        this.gameStatus = new GameStatus();
        libQspProxy = new LibQspProxyImpl(userId, this);
        libQspProxy.start();

    }

    public boolean isMaindescchanged(boolean defaultValue) {
        return gameStatus.isMaindescchanged(defaultValue);
    }

    public boolean isActionschanged() {
        return gameStatus.isActionschanged();
    }

    public boolean isObjectschanged() {
        return gameStatus.isObjectschanged();
    }

    public boolean isVarsdescchanged() {
        return gameStatus.isVarsdescchanged();
    }


    public void onItemClick(int position) {
        libQspProxy.onActionClicked(position);
    }

    public void onItemSelected(int position) {
        libQspProxy.onActionSelected(position);
    }

    public void onObjectSelected(int position) {
        libQspProxy.onObjectSelected(position);
    }

    public void refreshGame() {
        refreshMainDesc();
        refreshVarsDesc();
        refreshActions();
        refreshObjects();
    }

    public void restartGame(QspGame qspGame) {
        qspGame.setGameSaveFolder(qspGame.getGameFolder() + "saves/");
        this.qspGame = qspGame;
        gameStatus.reset();
        gameStatus.setGameRunning(true);
        gameStatus.setGameStartTime(System.currentTimeMillis());
        gameStatus.setLastMsCountCallTime(0);
        libQspProxy.restartGame(qspGame);
    }

    public String refreshMainDesc() {
        return libQspProxy.refreshMainDesc();
    }


    public String refreshVarsDesc() {
        return libQspProxy.refreshVarsDesc();
    }

    public ArrayList<QspListItem> refreshActions() {
        return libQspProxy.refreshActions();
    }

    public ArrayList<QspListItem> refreshObjects() {
        return libQspProxy.refreshObjects();
    }

    public LibQspProxy getLibQspProxy() {
        return libQspProxy;
    }

    public boolean shouldOverrideUrlLoading(final String href) {
        if (href.toLowerCase().startsWith("exec:")) {
            String code = Base64Util.decodeBase64(href.substring(5));
            libQspProxy.execute(code);
        }
        return true;
    }

    public String loadSaveGame(String filename) {
        if (StringUtils.isEmpty(filename)) {
            filename = QspConstants.QUICK_SAVE_NAME;
        }
        if (filename.endsWith(".sav") == false) {
            filename = filename + ".sav";
        }
        String saveFolder = FileUtil.getFolderPath(qspGame.getGameSaveFolder());
        File saveFile = QspUri.getFile(saveFolder, filename);
        if (saveFile.exists() == false) {
            return "0";
        }
        libQspProxy.loadGameState(QspUri.toUri(saveFile));
        gameStatus.refreshAll();
        return "1";
    }

    public void deleteSaveGame(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return;
        }

        if (filename.endsWith(".sav") == false) {
            filename = filename + ".sav";
        }
        String saveFolder = FileUtil.getFolderPath(qspGame.getGameSaveFolder());
        logger.info(saveFolder + filename);

        File file = QspUri.getFile(saveFolder, filename);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        gameStatus.refreshAll();
    }

    public void saveGame(String filename) {
        if (StringUtils.isEmpty(filename)) {
            filename = QspConstants.QUICK_SAVE_NAME;
        }     if ("NONAME".equals(filename)) {
            filename = UUID.randomUUID().toString().toUpperCase();
        }

        if (filename.endsWith(".sav") == false) {
            filename = filename + ".sav";
        }
        String saveFolder = FileUtil.getFolderPath(qspGame.getGameSaveFolder());
        new File(saveFolder).mkdir();
        libQspProxy.saveGameState(QspUri.toUri(QspUri.getFile(saveFolder, filename)));
        gameStatus.refreshAll();
    }

    public QspGame getQspGame() {
        return qspGame;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public QspUi getQspUI() {
        return qspUI;
    }

    public AudioObject getAudioObject() {
        return audioObject;
    }
}
