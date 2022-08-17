package com.qsp.player.vi.impl;

import com.qsp.player.vi.ViewInterface;

public class SwingViewImpl implements ViewInterface {
    /**
     * 消息输入
     *
     * @param prompt
     * @return
     */
    @Override
    public String getInputStr(String prompt) {
        return SwingUtils.showTextInputDialog(prompt);
    }

    /**
     * 消息提示框
     *
     * @param prompt
     */
    @Override
    public void showMessageBox(String prompt) {
        SwingUtils.showAlertDialog(prompt);
    }

    @Override
    public void showErrorBox(String prompt) {
        SwingUtils.showErrorialog(prompt);
    }

    @Override
    public void showPicture(String imagePath) {
        SwingUtils.showPicture(imagePath);
    }
}

