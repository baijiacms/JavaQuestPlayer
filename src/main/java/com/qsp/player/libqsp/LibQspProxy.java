package com.qsp.player.libqsp;

import com.qsp.player.libqsp.entity.QspGame;
import com.qsp.player.libqsp.entity.QspListItem;
import com.qsp.player.libqsp.util.QspUri;

import java.util.ArrayList;

/**
 * @author baijiacms
 */
public interface LibQspProxy {

    void start();

    void stop();

    void initGame(QspGame qspGame);

    void loadGameState(QspUri uri);

    void saveGameState(QspUri uri);

    void onActionSelected(int index);

    void onActionClicked(int index);


    void onInputAreaClicked();

    void execute(String code);

    void executeCounter();

    public void getRefreshInterfaceRequest();

}
