package com.qsp.webengine.util;

import com.qsp.player.PlayerEngine;
import com.qsp.player.common.QspConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;

/**
 * 流文件
 *
 * @author cxy
 */
public class SteamUtils {

    private static final Logger logger = LoggerFactory.getLogger(SteamUtils.class);

    /**
     * 引擎资源js文件
     *
     * @param fileName
     * @return
     */
    public static InputStream getEngineResourceInputSteam(PlayerEngine mPlayerEngine, String fileName) {
        try {
            return new FileInputStream(QspConstants.ENGINE_RESOURCE_PATH + fileName);
        } catch (FileNotFoundException e) {
            logger.error("引擎资源未找到" + QspConstants.ENGINE_RESOURCE_PATH + fileName);
            // e.printStackTrace();
        }
        if (mPlayerEngine.getGameStatus().isStart) {
            fileName = fileName.replaceFirst("/engine/lib/bigKuyash/", "/");
            fileName = fileName.replaceFirst("/engine/lib/sob/", "/");
            return getGameResourceInputSteam(mPlayerEngine, fileName);
        }
        return Utils.blankInputStream();
//        return Gengine.class.getResourceAsStream(Constants.LUA_SCRIPT_FILE_PATH+luaFileMap.get(fileName));
    }

    /**
     * 游戏资源文件
     *
     * @param fileName
     * @return
     */
    public static InputStream getGameResourceInputSteam(PlayerEngine mPlayerEngine, String fileName) {

        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        if (new File(QspConstants.GAME_DATA_PATH + fileName).exists() == false && (mPlayerEngine.getGameStatus().isStart && new File(mPlayerEngine.getGameStatus() + "/" + fileName).exists() == false)) {
            try {
                fileName = URLDecoder.decode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (mPlayerEngine.getGameStatus().isStart) {
//        fileName=fileName.replaceAll("%20"," ");
            try {
                return new FileInputStream(mPlayerEngine.getGameStatus().gameResourcePath + "/" + fileName);
            } catch (Exception e) {
                logger.error("游戏资源未找到:" + mPlayerEngine.getGameStatus().gameResourcePath + "/" + fileName);
                // e.printStackTrace();
            }
        }
        //未开始情况下需要传游戏id才能找到资源
        try {
            return new FileInputStream(QspConstants.GAME_DATA_PATH + fileName);
        } catch (Exception e) {
            logger.error("系统资源未找到:" + QspConstants.GAME_DATA_PATH + fileName);
            // e.printStackTrace();
        }
        return Utils.blankInputStream();
//        return Gengine.class.getResourceAsStream(Constants.LUA_SCRIPT_FILE_PATH+luaFileMap.get(fileName));
    }
}
