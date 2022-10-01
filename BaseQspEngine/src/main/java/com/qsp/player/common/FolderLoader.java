package com.qsp.player.common;

import com.qsp.player.entity.FileVo;
import com.qsp.player.entity.QspGame;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author baijiacms
 */
public class FolderLoader {

    private static Map<String, QspGame> GAME_FOLDER_MAP = new HashMap<>();

    public static Map<String, QspGame> getFolderMap() {
        if (GAME_FOLDER_MAP.size() == 0) {
            loadGameFolder(new ArrayList<>());
        }
        return GAME_FOLDER_MAP;
    }

    public static void loadGameFolder(List<QspGame> gameList) {
        List<FileVo> gameFileList = new ArrayList<>();
        File[] files = new File(QspConstants.getGameBaseFolder()).listFiles();
        for (File folder : files) {
            if (folder.isDirectory()) {
                File[] childFiles = folder.listFiles();
                boolean hasFile = false;
                for (File qspFile : childFiles) {
                    if (qspFile.isFile() && qspFile.getPath().endsWith(".qsp")) {
                        hasFile = true;
                        FileVo fileVo = new FileVo();
                        fileVo.setFile(qspFile);
                        fileVo.setFolder(folder);
                        fileVo.setQproj(false);
                        gameFileList.add(fileVo);
                        break;
                    }
                }
                if (hasFile == false) {
                    for (File qspFile : childFiles) {
                        if (qspFile.isFile() && qspFile.getPath().endsWith(".qproj")) {
                            FileVo fileVo = new FileVo();
                            fileVo.setFile(qspFile);
                            fileVo.setFolder(folder);
                            fileVo.setQproj(true);
                            gameFileList.add(fileVo);
                            break;
                        }
                    }
                }
            }
        }
        for (FileVo file : gameFileList) {
            dirFolder(gameList, file.getFile(), file.getFolder().getName());
        }
    }


    private static void dirFolder(List<QspGame> list, File file, String gameId) {
        if ("".equals(gameId) == false) {
            String gameFolder = QspConstants.getGameBaseFolder() + gameId + "/";
            boolean isDev = false;
            if (file.getPath().endsWith(".qproj")) {
                isDev = true;
            }
            File configFile = new File(gameFolder + "game.ini");
            boolean isReadConfig = false;
            if (configFile.exists()) {
                isReadConfig = true;
                try {
                    Properties properties = new Properties();
                    properties.load(new FileReader(configFile));
                    if (StringUtils.isEmpty(properties.getProperty("GAME_NAME")) == false) {
                        QspGame gameVo = new QspGame();
                        gameVo.setGameFile(file.getPath());
                        gameVo.setGameDesc(properties.getProperty("GAME_DESC"));
                        gameVo.setGameId(gameId);
                        gameVo.setGameName(properties.getProperty("GAME_NAME"));
                        gameVo.setGameVersion(properties.getProperty("GAME_VERSION"));
                        gameVo.setIsDevProject(isDev ? 1 : 0);
                        gameVo.setGameFolder(gameFolder);
                        gameVo.setGameDevFolder(gameFolder + "locations/");
                        gameVo.setGameQproj(file.getPath());
                        GAME_FOLDER_MAP.put(gameId, gameVo);

                        String gameIsSob = properties.getProperty("GAME_IS_SOB");
                        if (StringUtils.isEmpty(gameIsSob) == false) {
                            gameVo.setSob(Boolean.valueOf(gameIsSob));
                        }
                        String gameIsTowser = properties.getProperty("GAME_IS_TOWSER");
                        if (StringUtils.isEmpty(gameIsTowser) == false) {
                            gameVo.setTower(Boolean.valueOf(gameIsTowser));
                        }
                        String gameIsBigKuyash = properties.getProperty("GAME_IS_BIG_KUYASH");
                        if (StringUtils.isEmpty(gameIsBigKuyash) == false) {
                            gameVo.setBigKuyash(Boolean.valueOf(gameIsBigKuyash));
                        }
                        String gameIsDev = properties.getProperty("GAME_IS_DEV");
                        if (StringUtils.isEmpty(gameIsDev) == false) {
                            gameVo.setIsDev(Boolean.valueOf(gameIsDev) ? 1 : 0);
                        } else {
                            gameVo.setIsDev(0);
                        }
                        if (StringUtils.isEmpty(properties.getProperty("QSP_PASSWORD")) == false) {
                            gameVo.setQspPassword(properties.getProperty("QSP_PASSWORD"));
                        } else {
                            gameVo.setQspPassword("");
                        }
                        list.add(gameVo);
                    } else {
                        isReadConfig = false;
                    }
                } catch (IOException e) {
                    isReadConfig = false;
                }
            }
            if (isReadConfig == false) {
                QspGame gameVo = new QspGame();
                gameVo.setGameFile(file.getPath());
                gameVo.setGameDesc("blank");
                gameVo.setGameId(gameId);
                gameVo.setGameFolder(gameFolder);
                gameVo.setGameName(gameId);
                gameVo.setSob(false);
                gameVo.setTower(false);
                gameVo.setBigKuyash(false);
                gameVo.setGameVersion("1.0.0");
                gameVo.setIsDev(0);
                gameVo.setIsDevProject(isDev ? 1 : 0);
                gameVo.setQspPassword("");
                gameVo.setGameDevFolder(gameFolder + "locations/");
                gameVo.setGameQproj(file.getPath());
                list.add(gameVo);
                GAME_FOLDER_MAP.put(gameId, gameVo);
            }

        }


    }
}
