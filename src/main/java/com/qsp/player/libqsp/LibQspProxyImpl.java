package com.qsp.player.libqsp;

import com.baijiacms.qsp.controller.QspGameController;
import com.qsp.player.libqsp.dto.ActionData;
import com.qsp.player.libqsp.dto.ErrorData;
import com.qsp.player.libqsp.dto.ObjectData;
import com.qsp.player.libqsp.entity.QspGame;
import com.qsp.player.libqsp.entity.QspListItem;
import com.qsp.player.libqsp.queue.QspCore;
import com.qsp.player.libqsp.util.HtmlProcessor;
import com.qsp.player.libqsp.util.QspUri;
import com.qsp.player.libqsp.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author：ChenXingYu
 * @date 2024/2/29 10:17
 */
public class LibQspProxyImpl implements LibQspProxy {

    private static HtmlProcessor htmlProcessor = new HtmlProcessor();
    private static final Logger logger = LoggerFactory.getLogger(LibQspProxyImpl.class);
    private LibMethods libMethods;
    public LibQspProxyImpl(LibMethods libMethods)
    {
        this.libMethods=libMethods;
    }

    @Override
    public void start() {

        libMethods.QSPInit();
    }

    @Override
    public void stop() {
    }
    @Override
    public void initGame(QspGame qspGame) {
        QspCore.currentQspGame=qspGame;

        QspCore.concurrentBooleanMap.put(QspConstants.GAME_IS_RUNNING,true);
        QspCore.concurrentStringMap.put(QspConstants.GAME_FOLDER,qspGame.getGameFolder());
        QspCore.concurrentLongMap.put(QspConstants.GAME_START_TIME,new Date().getTime());

        byte[] gameData;
        if (qspGame.getIsDevProject() == 1) {
            gameData = QspCore.devMethodsHelper.getGemDate(qspGame.getGameDevFolder(), qspGame.getGameQproj());
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

        if (! libMethods.QSPLoadGameWorldFromData(gameData, gameData.length, fileName)) {

            showLastQspError();
            return;
        }
        if (!libMethods.QSPRestartGame(true)) {
            showLastQspError();
        }
        QspCore.refreshAll();

        return;
    }
    @Override
    public void loadGameState(QspUri uri) {
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

        if (!libMethods.QSPOpenSavedGameFromData(gameData, gameData.length, true)) {
            showLastQspError();
        }
        QspCore.refreshAll();
    }

    @Override
    public void saveGameState(QspUri uri) {
        if (uri != null) {

            logger.info("command:saveGameStateuri:" + uri.getmFile());
        }
        byte[] gameData =libMethods.QSPSaveGameAsData(false);
        if (gameData == null) {
            return;
        }

        try (OutputStream out = StreamUtils.openOutputStream(uri)) {
            out.write(gameData);
        } catch (IOException ex) {
            logger.error("Failed to save the game state", ex);
        }
        QspCore.refreshAll();
    }

    @Override
    public void onActionSelected(int index) {
        if (!libMethods.QSPSetSelActionIndex(index, false)) {
            showLastQspError();
        }
    }

    @Override
    public void onActionClicked(int index) {
        if (!libMethods.QSPSetSelActionIndex(index, false)) {
            showLastQspError();
        }
        if (!libMethods.QSPExecuteSelActionCode(true)) {
            showLastQspError();
        }
    }


    @Override
    public void onInputAreaClicked() {
        String input = "";
        String message = htmlProcessor.removeHtmlTags(input);
        if (message == null) {
            message = "";
        }
        logger.info("showInputBox:" + message);

        libMethods.QSPSetInputStrText(input);

        if (!libMethods.QSPExecUserInput(true)) {
            showLastQspError();
        }
    }

    @Override
    public void execute(String code) {
        if (!libMethods.QSPExecString(code, true)) {
            showLastQspError();
        }
        QspCore.refreshAll();
    }

    @Override
    public void executeCounter() {
        if (!libMethods.QSPExecCounter(true)) {
            showLastQspError();
        }
    }

    @Override
    public void getRefreshInterfaceRequest() {
        if (libMethods.QSPIsMainDescChanged()) {
            QspCore.maindescchanged.set(true);
            if(libMethods.QSPGetMainDesc()!=null) {
                QspCore.concurrentStringMap.put(QspConstants.MAIN_DESC, libMethods.QSPGetMainDesc());
            }
        }
        if (libMethods.QSPIsActionsChanged()) {
            QspCore.actionschanged.set(true);
            if(getActions()!=null) {
                QspCore.concurrentArrayListMap.put(QspConstants.ACTIONS, getActions());
            }
        }
        if (libMethods.QSPIsObjectsChanged()) {
            QspCore.objectschanged.set(true);
            if(getObjects()!=null) {
                QspCore.concurrentArrayListMap.put(QspConstants.OBJECTS, getObjects());
            }
        }
        if (libMethods.QSPIsVarsDescChanged()) {
            QspCore.varsdescchanged.set(true);
            if(libMethods.QSPGetVarsDesc()!=null) {
                QspCore.concurrentStringMap.put(QspConstants.VARS_DESC, libMethods.QSPGetVarsDesc());
            }
        }
    }

    private ArrayList<QspListItem> getActions() {
        ArrayList<QspListItem> actions = new ArrayList<>();

        int count = libMethods.QSPGetActionsCount();
        for (int i = 0; i < count; ++i) {
            ActionData actionData = (ActionData) libMethods.QSPGetActionData(i);
            QspListItem action = new QspListItem();
            action.index = i;
            action.text = true ? htmlProcessor.removeHtmlTags(actionData.name) : actionData.name;
            actions.add(action);
        }
        return actions;
    }

    private ArrayList<QspListItem> getObjects() {
        ArrayList<QspListItem> objects = new ArrayList<>();
        int count = libMethods.QSPGetObjectsCount();
        for (int i = 0; i < count; i++) {
            ObjectData objectResult = (ObjectData) libMethods.QSPGetObjectData(i);
            QspListItem object = new QspListItem();
            object.index = i;
            object.text = true ? htmlProcessor.removeHtmlTags(objectResult.getName()) : objectResult.getName();
            objects.add(object);
        }
        return objects;
    }
    public static String refreshMainDesc() {

        String mainDesc = getHtml(QspCore.concurrentStringMap.get(QspConstants.MAIN_DESC), true);

        return mainDesc;
    }

    public static String refreshVarsDesc() {
        String varsDesc = getHtml(QspCore.concurrentStringMap.get(QspConstants.VARS_DESC), false);
//        logger.info("varsDesc:"+varsDesc);
        return varsDesc;
    }

    public static ArrayList<QspListItem> refreshActions() {

        ArrayList<QspListItem> actions = QspCore.concurrentArrayListMap.get(QspConstants.ACTIONS);
        //logger.info("refreshActions:"+actions.size());
        return actions;
    }

    public static ArrayList<QspListItem> refreshObjects() {
        ArrayList<QspListItem> objects = QspCore.concurrentArrayListMap.get(QspConstants.OBJECTS);
        // logger.info("refreshObjects:"+objects.size());
        return objects;
    }
    private static String getHtml(String str, boolean isMainDesc) {

        return true ?
                htmlProcessor.convertQspHtmlToWebViewHtml(QspCore.concurrentStringMap.get(QspConstants.GAME_FOLDER), str, isMainDesc) :
                htmlProcessor.convertQspStringToWebViewHtml(str);
    }


    private void showLastQspError() {
        ErrorData errorData = (ErrorData) libMethods.QSPGetLastErrorData();

        String locName = errorData.locName != null ? errorData.locName : "";
        String desc = libMethods.QSPGetErrorDesc(errorData.errorNum);
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
    }
}
