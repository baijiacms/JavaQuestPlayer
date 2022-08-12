package com.qsp.player.common;

import com.qsp.player.util.Uri;

import java.io.File;

/**
 * 常量
 */
public class QspConstants {

    public static final String DEFAULT_USER = "USER1";
    public static int HTTP_PORT = 19870;
    public static String HTTP_LOCAL_URL = "http://127.0.0.1:" + HTTP_PORT;
    public static final String ENGINE_TITLE = "JavaQuestPlayer";
    public static final String ENGINE_POWER_BY = "https://github.com/baijiacms/";
    public static final String ENGINE_VERSION = "20220812";
    public static final int MIN_WIDTH = 1280;
    public static final int MIN_HEIGHT = 960;

    public static String ENGINE_RESOURCE_PATH;
    public static String QSP_DLL_PATH;
    public static String QSP_DEV_DLL_PATH;
    public static String GAME_DATA_PATH;

    //设置基础路径
    public static void setBaseFoler(String jarPath) {
//        jarPath="D:/javaproject/";
        jarPath = Uri.getFolderPath(jarPath);
        ENGINE_RESOURCE_PATH = jarPath + "resources";

        QSP_DLL_PATH = ENGINE_RESOURCE_PATH + "/engine/engine.dll";
        QSP_DEV_DLL_PATH = ENGINE_RESOURCE_PATH + "/engine/devtools.dll";
        String path = jarPath;
        for (int i = 0; i < 3; i++) {
            path = new File(path).getParent();
            if (path != null) {
                if (path.endsWith("/") == false) {
                    path = path + "/";
                }
                if (new File(path + "QspGames/").exists()) {
                    break;
                }
            }

        }
        GAME_DATA_PATH = Uri.getFolderPath(path) + "QspGames/";
//        GAME_DATA_PATH="D:/javaproject/game/";
//        setGameResource(GAME_ID);
    }
}
