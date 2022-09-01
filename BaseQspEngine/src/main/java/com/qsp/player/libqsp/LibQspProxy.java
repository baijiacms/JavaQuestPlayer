package com.qsp.player.libqsp;

import com.qsp.player.entity.QspGame;
import com.qsp.player.entity.QspListItem;
import com.qsp.player.util.QspUri;

import java.util.ArrayList;

public interface LibQspProxy {

    void start();

    void stop();

    void restartGame(QspGame qspGame);

    void loadGameState(QspUri uri);

    void saveGameState(QspUri uri);

    void onActionSelected(int index);

    void onActionClicked(int index);

    void onObjectSelected(int index);

    void onInputAreaClicked();

    void execute(String code);

    void executeCounter();

    public void getRefreshInterfaceRequest();

    public void qspFileToText(QspGame qspGame, String toFile);

    public void toGemFile(QspGame qspGame, String toFile);

    public String refreshMainDesc();

    public String refreshVarsDesc();

    public ArrayList<QspListItem> refreshActions();

    public ArrayList<QspListItem> refreshObjects();
}
