package com.qsp.player.libqsp;

import com.qsp.player.LibEngine;
import com.qsp.player.entity.QspGame;
import com.qsp.player.entity.QspListItem;
import com.qsp.player.libqsp.dto.ActionData;
import com.qsp.player.libqsp.dto.ObjectData;
import com.qsp.player.thread.LibQspThread;
import com.qsp.player.util.HtmlProcessor;
import com.qsp.player.util.QspUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class LibQspProxyImpl implements LibQspProxy {
    private static final Logger logger = LoggerFactory.getLogger(LibQspProxyImpl.class);
    private LibEngine libEngine;
    private LibQspThread libQspThread;
    private HtmlProcessor htmlProcessor = new HtmlProcessor();
    private LibMethods libMethods;
    private int userId;

    public LibQspProxyImpl(int userId, LibEngine libEngine) {
        this.userId = userId;
        this.libEngine = libEngine;
        libQspThread = new LibQspThread(userId, libEngine, this);

        this.libMethods = libQspThread.getLibMethods();
    }

    @Override
    public void start() {
        logger.info("command:start");
        this.libQspThread.start();
        this.libQspThread.qspStart();
    }

    @Override
    public void stop() {

    }

    private QspGame qspGame;

    @Override
    public void restartGame(QspGame qspGame) {
        logger.info("doRunGame Thread:" + Thread.currentThread().getName());
        this.qspGame = qspGame;


        this.libQspThread.loadGameWorld(qspGame);

        refreshMainDesc();
        refreshVarsDesc();
        refreshActions();
        refreshObjects();
    }

    @Override
    public void loadGameState(QspUri uri) {

        this.libQspThread.loadGameState(uri);
    }

    @Override
    public void saveGameState(QspUri uri) {

        logger.info("command:saveGameState" + uri);
        if (uri != null) {

            logger.info("command:saveGameStateuri:" + uri.getmFile());
        }
        this.libQspThread.qspSaveGameAsData(uri);
    }

    @Override
    public void onActionSelected(int index) {

        logger.info("command:onActionSelected");

        this.libQspThread.qspSetSelActionIndex(index);
    }

    @Override
    public void onActionClicked(int index) {

        logger.info("command:onActionClicked userId:" + userId);
        this.libQspThread.qspExecuteSelActionCode(index);
    }

    @Override
    public void onObjectSelected(int index) {

        logger.info("command:onObjectSelected");
        this.libQspThread.qspSetSelObjectIndex(index);
    }

    @Override
    public void onInputAreaClicked() {

        logger.info("command:onInputAreaClicked");

        String input = libEngine.getQspUI().showTextInputDialog("userInput");
        String message = htmlProcessor.removeHtmlTags(input);
        if (message == null) {
            message = "";
        }
        logger.info("showInputBox:" + message);

        this.libQspThread.qspSetInputStrText(message);
    }

    @Override
    public void execute(String code) {
        code = code.trim();
        logger.info("command:execute:" + code);
        if ("OPENGAME".equals(code)) {
            if (qspGame.isBigKuyash()) {

                libEngine.getGameStatus().setOpenSaveWindow(true);
                return;
            }
        }
        this.libQspThread.qspExecString(code);
    }

    @Override
    public void executeCounter() {
        logger.info("command:executeCounter");
        this.libQspThread.qspExecCounter();
    }

    @Override
    public void qspFileToText(QspGame qspGame, String toFile) {
        this.libQspThread.qspFileToText(qspGame, toFile);
    }

    @Override
    public void toGemFile(QspGame qspGame, String toFile) {
        this.libQspThread.toGemFile(qspGame, toFile);
    }


    public void getRefreshInterfaceRequest() {
        if (this.libMethods.QSPIsMainDescChanged()) {
            libEngine.getGameStatus().mainDesc = this.libMethods.QSPGetMainDesc();
            libEngine.getGameStatus().isMaindescchanged(true);
        }
        if (this.libMethods.QSPIsActionsChanged()) {
            libEngine.getGameStatus().actions = getActions();
            libEngine.getGameStatus().setActionschanged(true);
        }
        if (this.libMethods.QSPIsObjectsChanged()) {
            libEngine.getGameStatus().objects = getObjects();
            libEngine.getGameStatus().setObjectschanged(true);
        }
        if (this.libMethods.QSPIsVarsDescChanged()) {
            libEngine.getGameStatus().varsDesc = this.libMethods.QSPGetVarsDesc();
            libEngine.getGameStatus().setVarsdescchanged(true);
        }

    }

    public String refreshMainDesc() {
        String mainDesc = getHtml(libEngine.getGameStatus().mainDesc, true);

        return mainDesc;
    }


    public String refreshVarsDesc() {
        String varsDesc = getHtml(libEngine.getGameStatus().varsDesc, false);
//        logger.info("varsDesc:"+varsDesc);
        return varsDesc;
    }

    public ArrayList<QspListItem> refreshActions() {
        ArrayList<QspListItem> actions = libEngine.getGameStatus().actions;
        //logger.info("refreshActions:"+actions.size());
        return actions;
    }

    public ArrayList<QspListItem> refreshObjects() {
        ArrayList<QspListItem> objects = libEngine.getGameStatus().objects;
        // logger.info("refreshObjects:"+objects.size());
        return objects;
    }

    private String getHtml(String str, boolean isMainDesc) {

        return true ?
                htmlProcessor.convertQspHtmlToWebViewHtml(libEngine.getQspGame().getGameFolder(), str, isMainDesc) :
                htmlProcessor.convertQspStringToWebViewHtml(str);
    }


    private ArrayList<QspListItem> getActions() {
        ArrayList<QspListItem> actions = new ArrayList<>();

        int count = this.libMethods.QSPGetActionsCount();
        for (int i = 0; i < count; ++i) {
            ActionData actionData = (ActionData) this.libMethods.QSPGetActionData(i);
            QspListItem action = new QspListItem();
            action.index = i;
            action.text = true ? htmlProcessor.removeHtmlTags(actionData.name) : actionData.name;
            actions.add(action);
        }
        return actions;
    }

    private ArrayList<QspListItem> getObjects() {
        ArrayList<QspListItem> objects = new ArrayList<>();
        int count = this.libMethods.QSPGetObjectsCount();
        for (int i = 0; i < count; i++) {
            ObjectData objectResult = (ObjectData) this.libMethods.QSPGetObjectData(i);
            QspListItem object = new QspListItem();
            object.index = i;
            object.text = true ? htmlProcessor.removeHtmlTags(objectResult.name) : objectResult.name;
            objects.add(object);
        }
        return objects;
    }
}
