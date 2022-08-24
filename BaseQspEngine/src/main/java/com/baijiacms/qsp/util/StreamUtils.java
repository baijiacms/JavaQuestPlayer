package com.baijiacms.qsp.util;

import com.baijiacms.qsp.common.QspConstants;
import com.baijiacms.qsp.player.PlayerEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;

/**
 * 流文件
 *
 * @author cxy
 */
public class StreamUtils {

    private static final Logger logger = LoggerFactory.getLogger(StreamUtils.class);

    public static final String BLANK_STR = "";
    public static final String SUCCESS_STR = "1";


    private static final int BUFFER_SIZE = 8192;
    private static InputStream blankInputStream = null;
    private static OutputStream blankOutputStream = null;

    public static InputStream openInputStream(Uri uri) {
        try {
            return new FileInputStream(uri.getmFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("null");
        }
    }

    public static OutputStream openOutputStream(Uri uri) {

        try {
            return new FileOutputStream(uri.getmFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("null");
        }
    }


    public static void copy(InputStream from, OutputStream to) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = from.read(buffer)) > 0) {
            to.write(buffer, 0, bytesRead);
        }
        to.flush();
    }

    public static InputStream blankInputStream() {
        if (blankInputStream == null) {
            blankInputStream = new ByteArrayInputStream(
                    "".getBytes());
        }
        return blankInputStream;
    }

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
        return blankInputStream();
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
                fileName = URLDecoder.decode(fileName, QspConstants.CHARSET_STR);
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
        return blankInputStream();
    }
}
