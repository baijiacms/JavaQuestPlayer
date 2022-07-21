package com.qsp.webengine.util;

import com.qsp.player.core.QspConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 流文件
 * @author cxy
 */
public class SteamUtils {

    private static final Logger logger = LoggerFactory.getLogger(SteamUtils.class);

    /**
     * 引擎资源js文件
     * @param fileName
     * @return
     */
    public static InputStream getEngineResourceInputSteam(String fileName) {
        try {
            return new FileInputStream(QspConstants.ENGINE_RESOURCE_PATH + fileName);
        } catch (FileNotFoundException e) {
            logger.error("引擎资源未找到"+  QspConstants.ENGINE_RESOURCE_PATH +  fileName);
            // e.printStackTrace();
        }
        if(QspConstants.isStart)
        {
            fileName=fileName.replaceFirst("/engine/lib/bigKuyash/","/");
            fileName=fileName.replaceFirst("/engine/lib/sob/","/");
           return getGameResourceInputSteam( fileName);
        }
        return Utils.BlankInputStream();
//        return Gengine.class.getResourceAsStream(Constants.LUA_SCRIPT_FILE_PATH+luaFileMap.get(fileName));
    }

    /**
     * 游戏资源文件
     * @param fileName
     * @return
     */
    public static InputStream getGameResourceInputSteam(String fileName) {
        try {
            fileName= URLDecoder.decode(fileName,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(fileName.startsWith("/"))
        {
            fileName= fileName.substring(1);
        }
        if(QspConstants.isStart==false)
        {//未开始情况下需要传游戏id才能找到资源
            try {
                return new FileInputStream(QspConstants.GAME_DATA_PATH + fileName);
            } catch (Exception e) {
                logger.error("系统资源未找到:"+  QspConstants.GAME_DATA_PATH +  fileName);
                // e.printStackTrace();
            }
        }
//        fileName=fileName.replaceAll("%20"," ");
        try {
            return new FileInputStream(QspConstants.GAME_RESOURCE_PATH +"/"+ fileName);
        } catch (Exception e) {
            logger.error("游戏资源未找到:"+  QspConstants.GAME_RESOURCE_PATH+"/" +  fileName);
            // e.printStackTrace();
        }
        return Utils.BlankInputStream();
//        return Gengine.class.getResourceAsStream(Constants.LUA_SCRIPT_FILE_PATH+luaFileMap.get(fileName));
    }
}
