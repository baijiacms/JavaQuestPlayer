package com.qsp.player.util;

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
        String engineResourcePath = Uri.getFilePath(QspConstants.ENGINE_RESOURCE_PATH, fileName);
        if (new File(engineResourcePath).exists()) {
            try {
                return new FileInputStream(engineResourcePath);
            } catch (FileNotFoundException e) {
                logger.error("Engine resource not found:" + engineResourcePath);
                // e.printStackTrace();
            }
        }
        if (mPlayerEngine.getGameStatus().isStart) {
            fileName = fileName.replaceFirst("/engine/lib/bigKuyash/", "/");
            fileName = fileName.replaceFirst("/engine/lib/sob/", "/");
            return getGameResourceInputSteam(mPlayerEngine, fileName);
        }
        return IoUtil.blankInputStream();
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
        String gameDataPath = QspConstants.GAME_DATA_PATH + fileName;
        String gameResourcePath = null;
        if (mPlayerEngine.getGameStatus().isStart) {
            gameResourcePath = Uri.getFilePath(mPlayerEngine.getGameStatus().gameResourcePath, fileName);
        }
        if (new File(gameDataPath).exists() == false && (mPlayerEngine.getGameStatus().isStart && new File(gameResourcePath).exists() == false)) {
            try {
                fileName = URLDecoder.decode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            gameDataPath = QspConstants.GAME_DATA_PATH + fileName;
            gameResourcePath = Uri.getFilePath(mPlayerEngine.getGameStatus().gameResourcePath, fileName);
        }

        if (mPlayerEngine.getGameStatus().isStart) {
//        fileName=fileName.replaceAll("%20"," ");
            try {
                return new FileInputStream(gameResourcePath);
            } catch (Exception e) {
                logger.error("Game resource not found:" + gameResourcePath);
                // e.printStackTrace();
            }
        }
        //未开始情况下需要传游戏id才能找到资源
        try {
            return new FileInputStream(gameDataPath);
        } catch (Exception e) {
            logger.error("System resource not found:" + gameDataPath);
            // e.printStackTrace();
        }
        return IoUtil.blankInputStream();
//        return Gengine.class.getResourceAsStream(Constants.LUA_SCRIPT_FILE_PATH+luaFileMap.get(fileName));
    }
}
