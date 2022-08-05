package com.qsp.player.libqsp;

/**
 * 窗口状态
 */
public class QspGameStatus {
    public static boolean maindescchanged;
    public static boolean actionschanged;
    public static boolean objectschanged;
    public static boolean varsdescchanged;
    public static void refreshAll()
    {
        QspGameStatus.maindescchanged=true;
        QspGameStatus.actionschanged=true;
        QspGameStatus.objectschanged=true;
        QspGameStatus.varsdescchanged=true;
    }
}
