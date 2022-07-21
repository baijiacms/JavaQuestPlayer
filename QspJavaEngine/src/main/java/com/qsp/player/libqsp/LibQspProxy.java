package com.qsp.player.libqsp;

import com.qsp.webengine.vo.GameVo;
import com.qsp.player.core.util.Uri;

import com.qsp.player.core.model.GameState;

import java.io.File;

public interface LibQspProxy {

    void start();

    void stop();

    void runGame(String id, String title, File dir, File file);
    void restartGame();
    void loadGameState(Uri uri);
    void saveGameState(Uri uri);

    void onActionSelected(int index);
    void onActionClicked(int index);
    void onObjectSelected(int index);
    void onInputAreaClicked();


    void execute(String code);


    void executeCounter();

    GameState getGameState();

    void setGameInterface(GameInterface view);


    public void qspFileToText(GameVo gameVo, String toFile);
    public void toGemFile(GameVo gameVo, String toFile);
}
