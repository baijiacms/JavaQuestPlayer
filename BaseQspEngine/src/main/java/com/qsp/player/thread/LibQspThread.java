package com.qsp.player.thread;

import com.qsp.player.LibEngine;
import com.qsp.player.common.QspConstants;
import com.qsp.player.common.ThreadConstants;
import com.qsp.player.entity.QspGame;
import com.qsp.player.libqsp.*;
import com.qsp.player.libqsp.dto.ErrorData;
import com.qsp.player.util.QspUri;
import com.qsp.player.util.StreamUtils;
import com.qsp.player.vi.QspUi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Locale;
/**
 * @author baijiacms
 */
public class LibQspThread extends Thread {
    private ThreadObject threadObject = new ThreadObject();
    private static final Logger logger = LoggerFactory.getLogger(LibQspThread.class);
    private String threadName;
    private final DevMethodsHelper devMethodsHelper = QspConstants.DEV_UTILS;
    private QspUi qspUI;

    private LibMethods libMethods;


    private LibQspProxy libQspProxy;

    public LibQspThread(int userId, LibEngine libEngine, LibQspProxy libQspProxy) {
        super("ThreadName@" + userId);
        this.threadName = "ThreadName@" + userId;
        threadObject.userId = userId;
        this.libQspProxy = libQspProxy;
        this.qspUI = libEngine.getQspUI();
        LibQspCallbacks callbacks = new LibQspCallBacksImpl(userId, libEngine, this);
        libMethods = new NativeMethods(callbacks);
    }

    public LibQspProxy getLibQspProxy() {
        return libQspProxy;
    }

    public LibMethods getLibMethods() {
        return libMethods;
    }

    @Override
    public void run() {
        while (true) {
            if (threadObject.seekCount < 0) {
                threadObject.seekCount = 0;
            }
            if (threadObject.seekCount == 0) {
                threadObject.threadRun = false;

                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                threadObject.threadRun = true;
            }

            if (threadObject.threadRun) {
                threadObject.seekCount = threadObject.seekCount - 1;
                try {
                    toDo();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void threadRun() {
        threadObject.seekCount = threadObject.seekCount + 1;
        threadObject.threadRun = true;

    }


    private void toDo() {
//        synchronized (threadObject) {
        switch (threadObject.method) {
            case ThreadConstants.QSP_FILE_TO_TEXT:
                qspFileToText();
                break;
            case ThreadConstants.TO_GEM_FILE:
                toGemFile();
                break;
            case ThreadConstants.QSP_SELECT_MENU_ITEM:
                qspSelectMenuItem();
                break;
            case ThreadConstants.LOAD_GAME_STATE:
                loadGameState();
                break;
            case ThreadConstants.QSP_EXEC_COUNTER:
                qspExecCounter();
                break;
            case ThreadConstants.QSP_EXEC_STRING:
                qspExecString();
                break;
            case ThreadConstants.QSP_SET_INPUT_STR_TEXT:
                qspSetInputStrText();
                break;
            case ThreadConstants.QSP_SET_SEL_OBJECT_INDEX:
                qspSetSelObjectIndex();
                break;
            case ThreadConstants.QSP_SET_SEL_ACTION_INDEX:
                qspSetSelActionIndex();
                break;
            case ThreadConstants.QSP_EXECUTE_SEL_ACTION_CODE:
                qspExecuteSelActionCode();
                break;
            case ThreadConstants.QSP_RESTART_GAME:
                qspRestartGame();
                break;
            case ThreadConstants.QSP_START:
                qspStart();
                break;
            case ThreadConstants.LOAD_GAME_WORLD:
                loadGameWorld();
                break;
            case ThreadConstants.QSP_SAVE_GAME_AS_DATA:
                qspSaveGameAsData();
                break;
            default:
                break;

        }
//        }
    }


    private QspGame qspGame;
    private String toFile;
    private int result;
    private QspUri uri;
    private String code;
    private String input;
    private int index;

    public void qspSaveGameAsData(QspUri uri) {
        this.uri = uri;

        if (threadName.equals(Thread.currentThread().getName())) {

            qspSaveGameAsData();
        } else {
            synchronized (threadObject) {
                threadObject.method = ThreadConstants.QSP_SAVE_GAME_AS_DATA;
                threadRun();
            }
        }
    }

    private void qspSaveGameAsData() {

        byte[] gameData = this.libMethods.QSPSaveGameAsData(false);
        if (gameData == null) {
            return;
        }

        try (OutputStream out = StreamUtils.openOutputStream(uri)) {
            out.write(gameData);
        } catch (IOException ex) {
            logger.error("Failed to save the game state", ex);
        }
    }

    public void loadGameWorld(QspGame qspGame) {
        this.qspGame = qspGame;

        if (threadName.equals(Thread.currentThread().getName())) {

            loadGameWorld();
        } else {
            synchronized (threadObject) {
                threadObject.method = ThreadConstants.LOAD_GAME_WORLD;
                threadRun();
            }
        }
    }

    private void loadGameWorld() {
        byte[] gameData;
        if (qspGame.getIsDevProject() == 1) {
//            devUtils.toGemFile(gameVo.getGameDevFolder(),gameVo.getGameQproj(),"D:/1.qsp");
            gameData = this.devMethodsHelper.getGemDate(qspGame.getGameDevFolder(), qspGame.getGameQproj());
        } else {
            try (FileInputStream in = new FileInputStream(qspGame.getGameFile())) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    StreamUtils.copy(in, out);
                    gameData = out.toByteArray();
                }
            } catch (IOException ex) {
                logger.error("Failed to load the game world", ex);
                return;
            }
        }
        String fileName = qspGame.getGameFile();

        if (!this.libMethods.QSPLoadGameWorldFromData(gameData, gameData.length, fileName)) {

            showLastQspError();
            return;
        }
        qspRestartGame();


        return;
    }

    public void qspStart() {


        if (threadName.equals(Thread.currentThread().getName())) {

            localQspStart();
        } else {
            synchronized (threadObject) {
                threadObject.method = ThreadConstants.QSP_START;
                threadRun();
            }
        }
    }

    private void localQspStart() {

        this.libMethods.QSPInit();
//        this.libMethods.QSPDeInit();
    }

    public void qspRestartGame() {


        if (threadName.equals(Thread.currentThread().getName())) {

            localQspRestartGame();
        } else {
            synchronized (threadObject) {
                threadObject.method = ThreadConstants.QSP_RESTART_GAME;
                threadRun();
            }
        }
    }

    private void localQspRestartGame() {
        if (!this.libMethods.QSPRestartGame(true)) {
            showLastQspError();
        }
    }

    public void qspExecuteSelActionCode(int index) {

        this.index = index;
        if (threadName.equals(Thread.currentThread().getName())) {

            qspExecuteSelActionCode();
        } else {
            synchronized (threadObject) {
                threadObject.method = ThreadConstants.QSP_EXECUTE_SEL_ACTION_CODE;
                threadRun();
            }
        }
    }

    private void qspExecuteSelActionCode() {
        if (!this.libMethods.QSPSetSelActionIndex(index, false)) {
            showLastQspError();
        }
        if (!this.libMethods.QSPExecuteSelActionCode(true)) {
            showLastQspError();
        }
    }

    public void qspSetSelActionIndex(int index) {

        this.index = index;

        if (threadName.equals(Thread.currentThread().getName())) {

            qspSetSelActionIndex();
        } else {
            synchronized (threadObject) {
                threadObject.method = ThreadConstants.QSP_SET_SEL_ACTION_INDEX;
                threadRun();
            }
        }
    }

    private void qspSetSelActionIndex() {
        if (!this.libMethods.QSPSetSelActionIndex(index, false)) {
            showLastQspError();
        }
    }

    public void qspSetSelObjectIndex(int index) {

        this.index = index;
        if (threadName.equals(Thread.currentThread().getName())) {

            qspSetSelObjectIndex();
        } else {
            synchronized (threadObject) {
                threadObject.method = ThreadConstants.QSP_SET_SEL_OBJECT_INDEX;
                threadRun();
            }
        }
    }

    private void qspSetSelObjectIndex() {
        if (!this.libMethods.QSPSetSelObjectIndex(index, true)) {
            showLastQspError();
        }
    }

    public void qspSetInputStrText(String input) {

        this.input = input;

        if (threadName.equals(Thread.currentThread().getName())) {

            qspSetInputStrText();
        } else {
            synchronized (threadObject) {
                threadObject.method = ThreadConstants.QSP_SET_INPUT_STR_TEXT;
                threadRun();
            }
        }
    }

    private void qspSetInputStrText() {
        this.libMethods.QSPSetInputStrText(input);

        if (!this.libMethods.QSPExecUserInput(true)) {
            showLastQspError();
        }
    }

    public void qspExecString(String code) {
        this.code = code;
        if (threadName.equals(Thread.currentThread().getName())) {

            qspExecString();
        } else {
            synchronized (threadObject) {
                threadObject.method = ThreadConstants.QSP_EXEC_STRING;
                threadRun();
            }
        }
    }

    private void qspExecString() {
        logger.debug("exec:" + code);
        if (!this.libMethods.QSPExecString(code, true)) {
            showLastQspError();
        }
    }

    public void qspExecCounter() {
        if (threadName.equals(Thread.currentThread().getName())) {

            localQspExecCounter();
        } else {
            synchronized (threadObject) {
                threadObject.method = ThreadConstants.QSP_EXEC_COUNTER;
                threadRun();
            }
        }
    }

    private void localQspExecCounter() {
        if (!this.libMethods.QSPExecCounter(true)) {
            showLastQspError();
        }
    }

    public void loadGameState(final QspUri uri) {

        this.uri = uri;

        if (threadName.equals(Thread.currentThread().getName())) {

            loadGameState();
        } else {
            synchronized (threadObject) {
                threadObject.method = ThreadConstants.LOAD_GAME_STATE;
                threadRun();
            }
        }
    }

    private void loadGameState() {

        logger.info("command:loadGameState");
        final byte[] gameData;

        try (InputStream in = StreamUtils.openInputStream(uri)) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                StreamUtils.copy(in, out);
                gameData = out.toByteArray();
            }
        } catch (IOException ex) {
            logger.error("Failed to load game state", ex);
            return;
        }

        if (!this.libMethods.QSPOpenSavedGameFromData(gameData, gameData.length, true)) {
            showLastQspError();
        }
    }

    public void qspSelectMenuItem(int result) {
        this.result = result;
        if (threadName.equals(Thread.currentThread().getName())) {

            qspSelectMenuItem();
        } else {
            synchronized (threadObject) {
                threadObject.method = ThreadConstants.QSP_SELECT_MENU_ITEM;
                threadRun();
            }
        }
    }

    private void qspSelectMenuItem() {
        this.libMethods.QSPSelectMenuItem(result);
    }

    public void qspFileToText(QspGame qspGame, String toFile) {
        this.qspGame = qspGame;
        this.toFile = toFile;
        qspFileToText();
    }

    private void qspFileToText() {
        if (qspGame.getIsDevProject() == 0) {
            this.devMethodsHelper.qspFileToText(qspGame.getGameFile(), toFile, qspGame.getQspPassword());
        }
    }

    public void toGemFile(QspGame qspGame, String toFile) {
        this.qspGame = qspGame;
        this.toFile = toFile;

        toGemFile();
    }

    private void toGemFile() {
        if (qspGame.getIsDevProject() == 1) {
            devMethodsHelper.toGemFile(qspGame.getGameDevFolder(), qspGame.getGameQproj(), toFile);
        }
    }

    private void showLastQspError() {
        ErrorData errorData = (ErrorData) this.libMethods.QSPGetLastErrorData();

        String locName = errorData.locName != null ? errorData.locName : "";
        String desc = this.libMethods.QSPGetErrorDesc(errorData.errorNum);
        desc = desc != null ? desc : "";
        final String message = String.format(
                Locale.getDefault(),
                "Location: %s\nAction: %d\nLine: %d\nError number: %d\nDescription: %s",
                locName,
                errorData.index,
                errorData.line,
                errorData.errorNum,
                desc);

        logger.error(message);
        qspUI.showErrorialog(message);
    }


}
