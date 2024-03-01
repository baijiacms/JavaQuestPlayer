package com.qsp.player.libqsp.util;

import com.qsp.player.libqsp.QspConstants;
import com.qsp.player.libqsp.queue.QspCore;
import com.qsp.player.libqsp.queue.QspThread;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;

/**
 * @author baijiacms
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
        String engineResourcePath = QspUri.getFilePath(com.qsp.player.libqsp.common.QspConstants.getEngineResourcePath(), fileName);
        //  if (new File(engineResourcePath).exists()) {
        try {
            return new FileInputStream(engineResourcePath);
        } catch (FileNotFoundException e) {
            logger.error("Engine resource not found:" + engineResourcePath);
            // e.printStackTrace();
        }
        //   }
        return null;
//        return Gengine.class.getResourceAsStream(Constants.LUA_SCRIPT_FILE_PATH+luaFileMap.get(fileName));
    }

    /**
     * 游戏资源文件
     *
     * @param fileName
     * @return
     */
    public static InputStream getGameResourceInputSteam( String fileName) {

        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        String gameDataPath = com.qsp.player.libqsp.common.QspConstants.getGameBaseFolder() + fileName;
        String gameResourcePath = null;
        boolean gameIsStart = QspCore.concurrentBooleanMap.get(QspConstants.GAME_IS_RUNNING);
        String gameFolder= QspCore.concurrentStringMap.get(QspConstants.GAME_FOLDER);
        if (gameIsStart) {
            gameResourcePath = QspUri.getFilePath(gameFolder, fileName);
            if (new File(gameResourcePath).exists()) {
                try {
                    return new FileInputStream(gameResourcePath);
                } catch (Exception e) {
                    logger.error("Game resource not found:" + gameResourcePath);
                    // e.printStackTrace();
                }
            } else {
                String tFileName = fileName;
                try {
                    tFileName = URLDecoder.decode(fileName, com.qsp.player.libqsp.common.QspConstants.CHARSET_STR);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                gameResourcePath = QspUri.getFilePath(gameFolder, tFileName);
                if (new File(gameResourcePath).exists()) {
                    try {
                        return new FileInputStream(gameResourcePath);
                    } catch (Exception e) {
                        logger.error("Game resource not found:" + gameResourcePath);
                        // e.printStackTrace();
                    }
                }
            }
        }

        if (new File(gameDataPath).exists() == false) {
                try {
                    fileName = URLDecoder.decode(fileName, com.qsp.player.libqsp.common.QspConstants.CHARSET_STR);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(StringUtils.isNoneBlank(gameFolder)) {
                    gameDataPath = gameFolder + fileName;
                }
        }
        if (new File(gameDataPath).exists()) {
            //未开始情况下需要传游戏id才能找到资源
            try {
                return new FileInputStream(gameDataPath);
            } catch (Exception e) {
                logger.error("System resource not found:" + gameDataPath);
                // e.printStackTrace();
            }
        }

        return blankInputStream();
    }
}
