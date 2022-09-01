package com.qsp.player.util;

import com.qsp.player.LibEngine;
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
public class StreamUtils {

    private static final Logger logger = LoggerFactory.getLogger(StreamUtils.class);


    private static final int BUFFER_SIZE = 8192;
    private static InputStream blankInputStream = null;

    public static InputStream openInputStream(QspUri uri) {
        try {
            return new FileInputStream(uri.getmFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("null");
        }
    }

    public static OutputStream openOutputStream(QspUri uri) {

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

    public static InputStream getEngineResourceInputSteam(String fileName) {
        String engineResourcePath = QspUri.getFilePath(QspConstants.getEngineResourcePath(), fileName);
        if (new File(engineResourcePath).exists()) {
            try {
                return new FileInputStream(engineResourcePath);
            } catch (FileNotFoundException e) {
                logger.error("Engine resource not found:" + engineResourcePath);
                // e.printStackTrace();
            }
        }
        return blankInputStream();
//        return Gengine.class.getResourceAsStream(Constants.LUA_SCRIPT_FILE_PATH+luaFileMap.get(fileName));
    }

    /**
     * 引擎资源js文件
     *
     * @param fileName
     * @return
     */
    public static InputStream getSesionEngineResourceInputSteam(LibEngine libEngine, String fileName) {
        String engineResourcePath = QspUri.getFilePath(QspConstants.getEngineResourcePath(), fileName);
        if (new File(engineResourcePath).exists()) {
            try {
                return new FileInputStream(engineResourcePath);
            } catch (FileNotFoundException e) {
                logger.error("Engine resource not found:" + engineResourcePath);
                // e.printStackTrace();
            }
        }
        if (libEngine != null && libEngine.getGameStatus().isGameRunning()) {
            fileName = fileName.replaceFirst("/engine/lib/bigKuyash/", "/");
            fileName = fileName.replaceFirst("/engine/lib/sob/", "/");
            return getGameResourceInputSteam(libEngine, fileName);
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
    public static InputStream getGameResourceInputSteam(LibEngine libEngine, String fileName) {

        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        String gameDataPath = QspConstants.getGameBaseFolder() + fileName;
        String gameResourcePath = null;
        boolean gameIsStart = libEngine.getGameStatus().isGameRunning();
        if (gameIsStart) {
            gameResourcePath = QspUri.getFilePath(libEngine.getQspGame().getGameFolder(), fileName);
        }
        if (libEngine.getQspGame() != null && new File(gameDataPath).exists() == false && (gameIsStart && new File(gameResourcePath).exists() == false)) {
            try {
                fileName = URLDecoder.decode(fileName, QspConstants.CHARSET_STR);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            gameDataPath = libEngine.getQspGame().getGameFolder() + fileName;
            gameResourcePath = QspUri.getFilePath(libEngine.getQspGame().getGameFolder(), fileName);
        }

        if (gameIsStart) {
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
