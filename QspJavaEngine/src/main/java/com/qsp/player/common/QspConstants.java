package com.qsp.player.common;

import com.qsp.webengine.vo.FileVo;
import com.qsp.webengine.vo.GameVo;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * 常量
 */
public class QspConstants {

    public static final String DEFAULT_USERID="USER1";
    public static int HTTP_PORT =19870;
    public static String HTTP_LOCAL_URL ="http://127.0.0.1:"+ HTTP_PORT;
    public static final String ENGINE_TITLE="JavaQuestPlayer";
    public static final String ENGINE_POWER_BY ="https://github.com/baijiacms/";
    public static final String ENGINE_VERSION="20220807";
    public static final int minWidth=1280;
    public static final int minHeight=960;

    public static  String ENGINE_RESOURCE_PATH;
    public static  String QSP_DLL_PATH;
    public static  String QSP_DEV_DLL_PATH;
    public static  String GAME_DATA_PATH;
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
        String parentJarPath=new File(jarPath).getParent();
        if(parentJarPath.endsWith("/")==false)
        {
            parentJarPath=parentJarPath+"/";
        }
        GAME_DATA_PATH=parentJarPath+"QspGames/";

//        GAME_DATA_PATH="D:/javaproject/game/";
//        setGameResource(GAME_ID);
    }
}
