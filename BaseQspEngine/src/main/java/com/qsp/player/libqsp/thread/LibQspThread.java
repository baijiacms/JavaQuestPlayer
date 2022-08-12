package com.qsp.player.libqsp.thread;

import com.qsp.player.common.GameFolderLoader;
import com.qsp.player.libqsp.GameInterface;
import com.qsp.player.libqsp.LibQspProxyImpl;
import com.qsp.player.libqsp.dto.*;
import com.qsp.player.libqsp.service.HtmlProcessor;
import com.qsp.player.libqsp.util.DevUtils;
import com.qsp.player.util.IoUtil;
import com.qsp.player.util.Uri;
import com.qsp.webengine.vo.GameVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Locale;

import static com.qsp.player.util.StringUtil.getStringOrEmpty;

public class LibQspThread extends Thread {
    private ThreadObject libQspThreadObject = new ThreadObject();

    private static final Logger logger = LoggerFactory.getLogger(LibQspThread.class);

    private String threadName;
    private final DevUtils devUtils;

    private LibQspProxyImpl libQspProxyImpl;

    public LibQspThread(String userId, LibQspProxyImpl libQspProxyImpl) {
        super("ThreadName@" + userId);
        this.threadName = "ThreadName@" + userId;
        this.libQspProxyImpl = libQspProxyImpl;
        devUtils = new DevUtils();
        libQspThreadObject.userId = userId;
    }

    @Override
    public void run() {
        while (true) {
            if (libQspThreadObject.seekCount < 0) {
                libQspThreadObject.seekCount = 0;
            }
            if (libQspThreadObject.seekCount == 0) {
                libQspThreadObject.threadRun = false;

                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                    try {
//                        libQspThreadObject.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
            } else {
                libQspThreadObject.threadRun = true;
            }

            if (libQspThreadObject.threadRun) {
                libQspThreadObject.seekCount = libQspThreadObject.seekCount - 1;
                try {
                    toDo();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void threadRun() {
        libQspThreadObject.seekCount = libQspThreadObject.seekCount + 1;
        libQspThreadObject.threadRun = true;

    }

    private void toDo() {
//        synchronized (libQspThreadObject) {
        switch (libQspThreadObject.method) {
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

    private GameVo gameVo;
    private String toFile;
    private int result;
    private Uri uri;
    private GameInterface gameInterface;
    private String code;
    private String input;
    private int index;
    private GameObject gameObject;

    public void qspSaveGameAsData(GameInterface gameInterface, Uri uri) {
        this.uri = uri;
        this.gameInterface = gameInterface;

        if (threadName.equals(Thread.currentThread().getName())) {

            qspSaveGameAsData();
        } else {
            synchronized (libQspThreadObject) {
                libQspThreadObject.method = ThreadConstants.QSP_SAVE_GAME_AS_DATA;
                threadRun();
            }
        }
    }

    private void qspSaveGameAsData() {

        byte[] gameData = this.libQspProxyImpl.getNativeMethods().QSPSaveGameAsData(false);
        if (gameData == null) {
            return;
        }

        try (OutputStream out = IoUtil.openOutputStream(uri)) {
            out.write(gameData);
        } catch (IOException ex) {
            logger.error("Failed to save the game state", ex);
        }
    }

    public void loadGameWorld(GameObject gameObject, GameInterface gameInterface) {

        this.gameObject = gameObject;
        this.gameInterface = gameInterface;

        if (threadName.equals(Thread.currentThread().getName())) {

            loadGameWorld();
        } else {
            synchronized (libQspThreadObject) {
                libQspThreadObject.method = ThreadConstants.LOAD_GAME_WORLD;
                threadRun();
            }
        }
    }

    private void loadGameWorld() {
        byte[] gameData;
        GameVo gameVo = GameFolderLoader.getFolderMap().get(gameObject.gameId);
        if (gameVo.getIsDevProject() == 1) {
//            devUtils.toGemFile(gameVo.getGameDevFolder(),gameVo.getGameQproj(),"D:/1.qsp");
            gameData = this.devUtils.getGemDate(gameVo.getGameDevFolder(), gameVo.getGameQproj());
        } else {
            try (FileInputStream in = new FileInputStream(gameObject.gameFile)) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    IoUtil.copy(in, out);
                    gameData = out.toByteArray();
                }
            } catch (IOException ex) {
                logger.error("Failed to load the game world", ex);
                return;
            }
        }
        String fileName = gameObject.gameFile.getAbsolutePath();

        if (!this.libQspProxyImpl.getNativeMethods().QSPLoadGameWorldFromData(gameData, gameData.length, fileName)) {
            showLastQspError(gameInterface);
            return;
        }
        this.libQspProxyImpl.getGameStatus().gameStartTime = System.currentTimeMillis();
        this.libQspProxyImpl.getGameStatus().lastMsCountCallTime = 0;
//        QSPRestartGame(gameInterface);
        qspRestartGame();

        return;
    }

    public void qspStart(GameInterface gameInterface) {

        this.gameInterface = gameInterface;

        if (threadName.equals(Thread.currentThread().getName())) {

            qspStart();
        } else {
            synchronized (libQspThreadObject) {
                libQspThreadObject.method = ThreadConstants.QSP_START;
                threadRun();
            }
        }
    }

    private void qspStart() {

        this.libQspProxyImpl.getNativeMethods().QSPInit();
//        this.libQspProxyImpl.getNativeMethods().QSPDeInit();
    }

    public void qspRestartGame(GameInterface gameInterface) {

        this.gameInterface = gameInterface;

        if (threadName.equals(Thread.currentThread().getName())) {

            qspRestartGame();
        } else {
            synchronized (libQspThreadObject) {
                libQspThreadObject.method = ThreadConstants.QSP_RESTART_GAME;
                threadRun();
            }
        }
    }

    private void qspRestartGame() {
        if (!this.libQspProxyImpl.getNativeMethods().QSPRestartGame(true)) {
            showLastQspError(gameInterface);
        }
    }

    public void qspExecuteSelActionCode(int index, GameInterface gameInterface) {

        this.gameInterface = gameInterface;
        this.index = index;
        if (threadName.equals(Thread.currentThread().getName())) {

            qspExecuteSelActionCode();
        } else {
            synchronized (libQspThreadObject) {
                libQspThreadObject.method = ThreadConstants.QSP_EXECUTE_SEL_ACTION_CODE;
                threadRun();
            }
        }
    }

    private void qspExecuteSelActionCode() {
        if (!this.libQspProxyImpl.getNativeMethods().QSPSetSelActionIndex(index, false)) {
            showLastQspError(gameInterface);
        }
        if (!this.libQspProxyImpl.getNativeMethods().QSPExecuteSelActionCode(true)) {
            showLastQspError(gameInterface);
        }
    }

    public void qspSetSelActionIndex(int index, GameInterface gameInterface) {

        this.gameInterface = gameInterface;
        this.index = index;

        if (threadName.equals(Thread.currentThread().getName())) {

            qspSetSelActionIndex();
        } else {
            synchronized (libQspThreadObject) {
                libQspThreadObject.method = ThreadConstants.QSP_SET_SEL_ACTION_INDEX;
                threadRun();
            }
        }
    }

    private void qspSetSelActionIndex() {
        if (!this.libQspProxyImpl.getNativeMethods().QSPSetSelActionIndex(index, false)) {
            showLastQspError(gameInterface);
        }
    }

    public void qspSetSelObjectIndex(int index, GameInterface gameInterface) {

        this.gameInterface = gameInterface;
        this.index = index;
        if (threadName.equals(Thread.currentThread().getName())) {

            qspSetSelObjectIndex();
        } else {
            synchronized (libQspThreadObject) {
                libQspThreadObject.method = ThreadConstants.QSP_SET_SEL_OBJECT_INDEX;
                threadRun();
            }
        }
    }

    private void qspSetSelObjectIndex() {
        if (!this.libQspProxyImpl.getNativeMethods().QSPSetSelObjectIndex(index, true)) {
            showLastQspError(gameInterface);
        }
    }

    public void qspSetInputStrText(String input, GameInterface gameInterface) {

        this.gameInterface = gameInterface;
        this.input = input;

        if (threadName.equals(Thread.currentThread().getName())) {

            qspSetInputStrText();
        } else {
            synchronized (libQspThreadObject) {
                libQspThreadObject.method = ThreadConstants.QSP_SET_INPUT_STR_TEXT;
                threadRun();
            }
        }
    }

    private void qspSetInputStrText() {
        this.libQspProxyImpl.getNativeMethods().QSPSetInputStrText(input);

        if (!this.libQspProxyImpl.getNativeMethods().QSPExecUserInput(true)) {
            showLastQspError(gameInterface);
        }
    }

    public void qspExecString(String code, GameInterface gameInterface) {
        this.gameInterface = gameInterface;
        this.code = code;
        if (threadName.equals(Thread.currentThread().getName())) {

            qspExecString();
        } else {
            synchronized (libQspThreadObject) {
                libQspThreadObject.method = ThreadConstants.QSP_EXEC_STRING;
                threadRun();
            }
        }
    }

    private void qspExecString() {
        logger.debug("exec:" + code);
        if (!this.libQspProxyImpl.getNativeMethods().QSPExecString(code, true)) {
            showLastQspError(gameInterface);
        }
    }

    public void qspExecCounter(GameInterface gameInterface) {
        this.gameInterface = gameInterface;

        if (threadName.equals(Thread.currentThread().getName())) {

            qspExecCounter();
        } else {
            synchronized (libQspThreadObject) {
                libQspThreadObject.method = ThreadConstants.QSP_EXEC_COUNTER;
                threadRun();
            }
        }
    }

    private void qspExecCounter() {
        if (!this.libQspProxyImpl.getNativeMethods().QSPExecCounter(true)) {
            showLastQspError(this.gameInterface);
        }
    }

    public void loadGameState(final Uri uri, GameInterface gameInterface) {

        this.uri = uri;
        this.gameInterface = gameInterface;

        if (threadName.equals(Thread.currentThread().getName())) {

            loadGameState();
        } else {
            synchronized (libQspThreadObject) {
                libQspThreadObject.method = ThreadConstants.LOAD_GAME_STATE;
                threadRun();
            }
        }
    }

    private void loadGameState() {

        logger.info("command:loadGameState");
        final byte[] gameData;

        try (InputStream in = IoUtil.openInputStream(uri)) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                IoUtil.copy(in, out);
                gameData = out.toByteArray();
            }
        } catch (IOException ex) {
            logger.error("Failed to load game state", ex);
            return;
        }

        if (!this.libQspProxyImpl.getNativeMethods().QSPOpenSavedGameFromData(gameData, gameData.length, true)) {
            showLastQspError(gameInterface);
        }
    }

    public void qspSelectMenuItem(int result) {
        this.result = result;
        if (threadName.equals(Thread.currentThread().getName())) {

            qspSelectMenuItem();
        } else {
            synchronized (libQspThreadObject) {
                libQspThreadObject.method = ThreadConstants.QSP_SELECT_MENU_ITEM;
                threadRun();
            }
        }
    }

    private void qspSelectMenuItem() {
        this.libQspProxyImpl.getNativeMethods().QSPSelectMenuItem(result);
    }

    public void qspFileToText(GameVo gameVo, String toFile) {
        this.gameVo = gameVo;
        this.toFile = toFile;
//        if(threadName.equals(Thread.currentThread().getName()))
//        {

        qspFileToText();
//        }else {
//            synchronized (libQspThreadObject) {
//                libQspThreadObject.method = ThreadConstants.qspFileToText;
//                libQspThreadObject.seekCount = libQspThreadObject.seekCount + 1;
//                threadGoto();
//            }
//        }
    }

    private void qspFileToText() {
        if (gameVo.getIsDevProject() == 0) {
            this.devUtils.qspFileToText(gameVo.getGameFile(), toFile, gameVo.getQspPassword());
        }
    }

    public void toGemFile(GameVo gameVo, String toFile) {
        this.gameVo = gameVo;
        this.toFile = toFile;
//        if(threadName.equals(Thread.currentThread().getName()))
//        {

        toGemFile();
//        }else {
//            synchronized (libQspThreadObject) {
//                libQspThreadObject.method = ThreadConstants.toGemFile;
//                libQspThreadObject.seekCount = libQspThreadObject.seekCount + 1;
//                threadGoto();
//            }
//        }
    }

    private void toGemFile() {
        if (gameVo.getIsDevProject() == 1) {
            devUtils.toGemFile(gameVo.getGameDevFolder(), gameVo.getGameQproj(), toFile);
        }
    }

    private void showLastQspError(GameInterface gameInterface) {
        ErrorData errorData = (ErrorData) this.libQspProxyImpl.getNativeMethods().QSPGetLastErrorData();
        String locName = getStringOrEmpty(errorData.locName);
        String desc = getStringOrEmpty(this.libQspProxyImpl.getNativeMethods().QSPGetErrorDesc(errorData.errorNum));

        final String message = String.format(
                Locale.getDefault(),
                "Location: %s\nAction: %d\nLine: %d\nError number: %d\nDescription: %s",
                locName,
                errorData.index,
                errorData.line,
                errorData.errorNum,
                desc);

        logger.error(message);

        GameInterface inter = gameInterface;
        if (inter != null) {
            gameInterface.showError(message);
        }
    }

    public RefreshRequest getRefreshInterfaceRequest(HtmlProcessor htmlProcessor) {
//        synchronized (libQspThreadObject) {
        RefreshRequest request = new RefreshRequest();

        if (this.libQspProxyImpl.getNativeMethods().QSPIsMainDescChanged()) {
            gameObject.mainDesc = this.libQspProxyImpl.getNativeMethods().QSPGetMainDesc();
            request.mainDescChanged = true;
        }
        if (this.libQspProxyImpl.getNativeMethods().QSPIsActionsChanged()) {
            gameObject.actions = getActions(htmlProcessor);
            request.actionsChanged = true;
        }
        if (this.libQspProxyImpl.getNativeMethods().QSPIsObjectsChanged()) {
            gameObject.objects = getObjects(htmlProcessor);
            request.objectsChanged = true;
        }
        if (this.libQspProxyImpl.getNativeMethods().QSPIsVarsDescChanged()) {
            gameObject.varsDesc = this.libQspProxyImpl.getNativeMethods().QSPGetVarsDesc();
            request.varsDescChanged = true;
        }
        return request;
//        }
    }


    private ArrayList<QspListItem> getActions(HtmlProcessor htmlProcessor) {
        ArrayList<QspListItem> actions = new ArrayList<>();

        int count = this.libQspProxyImpl.getNativeMethods().QSPGetActionsCount();
        for (int i = 0; i < count; ++i) {
            ActionData actionData = (ActionData) this.libQspProxyImpl.getNativeMethods().QSPGetActionData(i);
            QspListItem action = new QspListItem();
            action.index = i;
            action.text = true ? htmlProcessor.removeHtmlTags(actionData.name) : actionData.name;
            actions.add(action);
        }
        return actions;
    }

    private ArrayList<QspListItem> getObjects(HtmlProcessor htmlProcessor) {
        ArrayList<QspListItem> objects = new ArrayList<>();
        int count = this.libQspProxyImpl.getNativeMethods().QSPGetObjectsCount();
        for (int i = 0; i < count; i++) {
            ObjectData objectResult = (ObjectData) this.libQspProxyImpl.getNativeMethods().QSPGetObjectData(i);
            QspListItem object = new QspListItem();
            object.index = i;
            object.text = true ? htmlProcessor.removeHtmlTags(objectResult.name) : objectResult.name;
            objects.add(object);
        }
        return objects;
    }

}
