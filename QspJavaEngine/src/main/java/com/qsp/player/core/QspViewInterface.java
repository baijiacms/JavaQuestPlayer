package com.qsp.player.core;

/**
 * 交换窗接口类
 */
public interface QspViewInterface {
    /**
     * 显示输入框
     * @param prompt
     * @return
     */
    public String getInputStr(String prompt);

    /**
     * 显示确认框
     * @param prompt
     */
    public void showMessageBox(String prompt);

    /**
     * 显示错误框
     * @param prompt
     */
    public void showErrorBox(String prompt);

    /**
     * 单独显示图片框
     * @param imagePath
     */
    public void showPicture(String imagePath);
}
