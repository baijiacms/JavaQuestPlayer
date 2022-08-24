package com.baijiacms.qsp.player;

import com.baijiacms.qsp.common.WindowType;
import com.baijiacms.qsp.dto.RefreshRequest;

public interface GameInterface {

    void refresh(RefreshRequest request);

    void showError(String message);

    void showPicture(String path);

    void showMessage(String message);

    String showInputBox(String prompt);

    int showMenu();

    void saveGame(String filename);

    String loadSaveGame(String filename);

    void deleteSaveGame(String filename);

    void showWindow(WindowType type, boolean show);

    void setCounterInterval(int millis);

}
