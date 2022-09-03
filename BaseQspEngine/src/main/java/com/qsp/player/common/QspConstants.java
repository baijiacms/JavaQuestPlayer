package com.qsp.player.common;


import com.qsp.player.libqsp.DevMethodsHelper;
import com.qsp.player.util.FileUtil;
import com.qsp.player.util.QspUri;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author baijiacms
 */
public class QspConstants {
    public static final int DEFAULT_USER = 99;
    public static final String ENGINE_VERSION = "20220902";
    public static int HTTP_PORT = 19870;
    public static String HTTP_LOCAL_URL = "http://127.0.0.1:" + HTTP_PORT;
    public static final String ENGINE_TITLE = "JavaQuestPlayer";
    public static final String ENGINE_POWER_BY = "https://github.com/baijiacms/";

    public static final DevMethodsHelper DEV_UTILS = new DevMethodsHelper();
    public static final String QUICK_SAVE_NAME = "quickSave.sav";
    public static final String CHARSET_STR = "utf-8";
    public static final String HTML_CONTENT_TYPE = "text/html;charset=" + CHARSET_STR;
    public static final String JSON_CONTENT_TYPE = "application/json;charset=" + CHARSET_STR;
    public static final String BLANK_STR = "";
    public static final String SUCCESS_STR = "1";

    public static final Charset CHARSET = Charset.forName(CHARSET_STR);
    public static final int MIN_WIDTH = 1280;
    public static final int MIN_HEIGHT = 960;

    private static String jarPath = null;
    private static String GAME_DATA_PATH = null;
    private static String ENGINE_RESOURCE_PATH = null;

    private static String getJarPath() {
        if (jarPath == null) {

            jarPath = FileUtil.getJarPath(QspConstants.class);
        }
        return jarPath;
    }

    private static String getResourcePath() {
        return getJarPath() + "resources/";
    }

    public static String getLibQspPath() {
        if (isLinux()) {
            return getResourcePath() + "engine/libqsp.so";
        } else {
            return getResourcePath() + "engine/libqsp.dll";
        }
    }

    public static String getLibDevPath() {
        if (isLinux()) {
            return getResourcePath() + "engine/libdev.so";
        } else {
            return getResourcePath() + "engine/libdev.dll";
        }

    }

    public static String getEngineResourcePath() {
        if (ENGINE_RESOURCE_PATH == null) {
            String jarPath = FileUtil.getJarPath(QspConstants.class);
            ENGINE_RESOURCE_PATH = jarPath + "resources/";

        }
        return ENGINE_RESOURCE_PATH;
    }


    private static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public static String getSaveFolder(String gameId) {
        String saveFolder = getGameResource(gameId) + "/saves/";
        QspUri.getFolderFile(saveFolder).mkdir();
        return saveFolder;
    }

    private static String getGameResource(String gameId) {
        return QspUri.getFolderPath(getGameBaseFolder()) + gameId;

    }

    public static String getGameBaseFolder() {
        if (GAME_DATA_PATH == null) {
            jarPath = QspUri.getFolderPath(FileUtil.getJarPath(QspConstants.class));
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
            GAME_DATA_PATH = QspUri.getFolderPath(path) + "QspGames/";
        }
        return GAME_DATA_PATH;
    }
}
