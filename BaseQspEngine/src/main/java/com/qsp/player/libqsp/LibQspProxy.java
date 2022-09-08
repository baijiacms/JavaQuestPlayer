package com.qsp.player.libqsp;

import com.qsp.player.entity.QspGame;
import com.qsp.player.entity.QspListItem;
import com.qsp.player.util.QspUri;

import java.util.ArrayList;

/**
 * @author baijiacms
 */
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

    public String refreshMainDesc();

    public String refreshVarsDesc();

    public ArrayList<QspListItem> refreshActions();

    public ArrayList<QspListItem> refreshObjects();
}
