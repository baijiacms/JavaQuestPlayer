package com.qsp.player.javafx;

import com.qsp.player.vi.ViewInterface;

public class JavaFxViewImpl implements ViewInterface {
    /**
     * 消息输入
     *
     * @param prompt
     * @return
     */
    @Override
    public String getInputStr(String prompt) {
        return JavaFxUtils.showTextInputDialog(prompt);
    }

    /**
     * 消息提示框
     *
     * @param prompt
     */
    @Override
    public void showMessageBox(String prompt) {
        JavaFxUtils.showAlertDialog(prompt);
    }

    @Override
    public void showErrorBox(String prompt) {
        JavaFxUtils.showErrorialog(prompt);
    }

    @Override
    public void showPicture(String imagePath) {
        JavaFxUtils.showPicture(imagePath);
    }
}

