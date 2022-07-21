package com.qsp.player.core;

import com.qsp.webengine.vo.GameVo;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 常量
 */
public class QspConstants {
    public static Map<String, GameVo> GAME_FOLDER_MAP=new HashMap<>();

    public static  String LOCAL_URL="http://127.0.0.1:10024";
    public static boolean IS_SOB_GAME=false; //sob游戏时候特殊处理
    public static boolean IS_BIG_KUYASH=false; //BIG_KUYASH游戏时候特殊处理
    public static final String ENGINE_VERSION="20220717";
    public static final int width=640;
    public static boolean isProxy=false;

    public static  String ENGINE_RESOURCE_PATH;
    public static  String QSP_DLL_PATH;
    public static  String QSP_DEV_DLL_PATH;
    public static  String GAME_DATA_PATH;

    public static boolean isStart=false;
    public static String GAME_RESOURCE_PATH;
    public static String GAME_FILE;
    public static String GAME_ID="default";
    public static String GAME_TITLE="default";
    public static String GAME_VERSION="1.0.0";
    public static String URL_BASE_URL;
    public static String URL_REPLACE_URL;

    //设置基础路径
    public static void setBaseFoler(String jarPath)
    {
//        jarPath="D:/javaproject/";
        if(jarPath.endsWith("/")==false)
        {
            jarPath=jarPath+"/";
        }
        ENGINE_RESOURCE_PATH=jarPath+"resources";

        QSP_DLL_PATH=ENGINE_RESOURCE_PATH+"/engine/engine.dll";
       QSP_DEV_DLL_PATH=ENGINE_RESOURCE_PATH+"/engine/devtools.dll";
        GAME_DATA_PATH=ENGINE_RESOURCE_PATH+"/game/";

//        GAME_DATA_PATH="D:/javaproject/game/";
         setGameResource(GAME_ID);
    }
    private static void setGameResource(String gameId)
    {
        QspConstants.GAME_ID = gameId;
        QspConstants.GAME_RESOURCE_PATH = QspConstants.GAME_DATA_PATH + QspConstants.GAME_ID ;
        URL_BASE_URL="file:///"+ QspConstants.GAME_RESOURCE_PATH;
        URL_REPLACE_URL=LOCAL_URL;
        if(QspConstants.isProxy) {
            //JAVAFX需要通过替换java.net.URI类才能正常播放视频，需要替换的类在QspJavaFxPlayer的docs文件夹下，以下是替换后需要设置的参数，用来修改URL
            URI.BASE_URL= "file:///"+ QspConstants.GAME_RESOURCE_PATH;
            URI.REPLACE_URL=LOCAL_URL;
        }
        GAME_FILE=GAME_RESOURCE_PATH+"/game.qsp";
    }
    public static String getSaveFolder()
    {
        String saveFolder=QspConstants.GAME_RESOURCE_PATH+"/save/";
        new File(saveFolder).mkdir();
        return saveFolder;
    }
    public static void setGamePathById(String gameId) {
        setGameResource(gameId);
        String gameFolder = QspConstants.GAME_RESOURCE_PATH + "/";
        File[] files = new File(gameFolder).listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
            } else {
                if (f.isFile() && f.getPath().endsWith(".qsp")) {
                    QspConstants.GAME_FILE = f.getAbsolutePath();
                }
            }
        }
        File configFile = new File(QspConstants.GAME_RESOURCE_PATH+ "/game.ini");
        boolean isReadConfig = false;
        if (configFile.exists()) {
            isReadConfig = true;
            try {
                Properties properties = new Properties();
                properties.load(new FileReader(configFile));
                if (StringUtils.isEmpty(properties.getProperty("GAME_NAME")) == false) {
                    QspConstants.GAME_TITLE = properties.getProperty("GAME_NAME");
                    QspConstants.GAME_VERSION = properties.getProperty("GAME_VERSION");
                    String gameIsSob= properties.getProperty("GAME_IS_SOB");
                    if(StringUtils.isEmpty(gameIsSob)==false)
                    {
                        QspConstants.IS_SOB_GAME=Boolean.valueOf(gameIsSob);
                    }
                    String gameIsBigKuyash= properties.getProperty("GAME_IS_BIG_KUYASH");
                    if(StringUtils.isEmpty(gameIsBigKuyash)==false)
                    {
                        QspConstants.IS_BIG_KUYASH=Boolean.valueOf(gameIsBigKuyash);
                    }
                } else {
                    isReadConfig = false;
                }
            } catch (IOException e) {
                isReadConfig = false;
            }
        }

        if (isReadConfig == false) {
            QspConstants.GAME_TITLE = gameId;
            QspConstants.GAME_VERSION = "1.0.0";
        }
    }

}
