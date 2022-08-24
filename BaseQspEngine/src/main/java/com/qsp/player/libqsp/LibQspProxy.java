package com.qsp.player.libqsp;

import com.baijiacms.qsp.dto.GameObject;
import com.baijiacms.qsp.util.Uri;
import com.baijiacms.qsp.vo.GameVo;

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

    GameObject getGameObject();


    public void qspFileToText(GameVo gameVo, String toFile);

    public void toGemFile(GameVo gameVo, String toFile);
}
