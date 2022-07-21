package com.qsp.webengine.template;

import com.qsp.webengine.HtmlEngine;
import com.qsp.webengine.util.Utils;
import com.qsp.webengine.vo.FileVo;
import com.qsp.webengine.vo.GameVo;
import com.qsp.player.core.QspConstants;
import com.qsp.player.core.game.GameShower;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.*;

/**
 * 游戏选择界面
 * @author cxy
 */
public class GameSelectTemplate {
    private Template indexTemplate;

    public GameSelectTemplate(VelocityEngine ve) {

        indexTemplate = ve.getTemplate("baijiacms/html/center/main.vm", "utf-8");
    }

    public String getHtml() {
        List<GameVo> gameList = new ArrayList<>();
        List<FileVo> gameFileList=new ArrayList<>();
        File[] files = new File(QspConstants.GAME_DATA_PATH).listFiles();
        Map<String,String> gameFolerMap=new HashMap<>();
        for (File folder : files) {
            if (folder.isDirectory()) {
                File[] childFiles = folder.listFiles();
                boolean hasFile=false;
                for(File qspFile:childFiles)
                {
                    if(qspFile.isFile()&&qspFile.getPath().endsWith(".qsp")) {
                        hasFile=true;
                        FileVo fileVo=new FileVo();
                        fileVo.setFile(qspFile);
                        fileVo.setFolder(folder);
                        fileVo.setQproj(false);
                        gameFileList.add(fileVo);
                        break;
                    }
                }
                if(hasFile==false) {
                    for (File qspFile : childFiles) {
                        if (qspFile.isFile() && qspFile.getPath().endsWith(".qproj")) {
                            FileVo fileVo=new FileVo();
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
        for(FileVo file:gameFileList)
        {
            dirFolder1(gameList, file.getFile(), file.getFolder().getName());
        }
        VelocityContext context = new VelocityContext();

        context.put("engineVersion", QspConstants.ENGINE_VERSION);
        context.put("gameList", gameList);


        StringWriter writer = new StringWriter();
        indexTemplate.merge(context, writer);
        writer.flush();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }


    private void dirFolder1(List<GameVo> list, File file, String gameId) {
                if ("".equals(gameId) == false) {
                    String gameFolder=QspConstants.GAME_DATA_PATH + "" + gameId + "/";
                        boolean isDev=false;
                        if(file.getPath().endsWith(".qproj"))
                        {
                            isDev=true;
                        }
                        File configFile = new File(gameFolder+ "game.ini");
                        boolean isReadConfig = false;
                        if (configFile.exists()) {
                            isReadConfig = true;
                            try {
                                Properties properties = new Properties();
                                properties.load(new FileReader(configFile));
                                if (StringUtils.isEmpty(properties.getProperty("GAME_NAME")) == false) {
                                    GameVo gameVo = new GameVo();
                                    gameVo.setGameFile(file.getPath());
                                    gameVo.setGameDesc(properties.getProperty("GAME_DESC"));
                                    gameVo.setGameId(gameId);
                                    gameVo.setGameName(properties.getProperty("GAME_NAME"));
                                    gameVo.setGameVersion(properties.getProperty("GAME_VERSION"));
                                    gameVo.setIsDevProject(isDev?1:0);
                                    gameVo.setGameFolder(gameFolder);
                                    gameVo.setGameDevFolder(gameFolder+"locations/");
                                    gameVo.setGameQproj(file.getPath());
                                    QspConstants.GAME_FOLDER_MAP.put(gameId,gameVo);

                                    String gameIsSob= properties.getProperty("GAME_IS_SOB");
                                    if(StringUtils.isEmpty(gameIsSob)==false)
                                    {
                                        gameVo.setSob(Boolean.valueOf(gameIsSob));
                                    }
                                    String gameIsBigKuyash= properties.getProperty("GAME_IS_BIG_KUYASH");
                                    if(StringUtils.isEmpty(gameIsBigKuyash)==false)
                                    {
                                        gameVo.setBigKuyash(Boolean.valueOf(gameIsBigKuyash));
                                    }
                                    String gameIsDev= properties.getProperty("GAME_IS_DEV");
                                    if(StringUtils.isEmpty(gameIsDev)==false)
                                    {
                                        gameVo.setIsDev(Boolean.valueOf(gameIsDev)?1:0);
                                    }else
                                    {
                                        gameVo.setIsDev(0);
                                    }
                                    if(StringUtils.isEmpty(properties.getProperty("QSP_PASSWORD"))==false)
                                    {
                                        gameVo.setQspPassword(properties.getProperty("QSP_PASSWORD"));
                                    }else
                                    {
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
                            GameVo gameVo = new GameVo();
                            gameVo.setGameFile(file.getPath());
                            gameVo.setGameDesc("无描述");
                            gameVo.setGameId(gameId);
                            gameVo.setGameFolder(gameFolder);
                            gameVo.setGameName(gameId);
                            gameVo.setSob(false);
                            gameVo.setBigKuyash(false);
                            gameVo.setGameVersion("1.0.0");
                            gameVo.setIsDev(0);
                            gameVo.setIsDevProject(isDev?1:0);
                            gameVo.setQspPassword("");
                            gameVo.setGameDevFolder(gameFolder+"locations/");
                            gameVo.setGameQproj(file.getPath());
                            list.add(gameVo);
                            QspConstants.GAME_FOLDER_MAP.put(gameId,gameVo);
                            // System.out.println(f.getPath());
                        }

                }


    }


    public InputStream loadGame(GameShower mGameShower, String gameId) {
        String actionScript = gameId;
        if (StringUtils.isEmpty(actionScript)) {
            return Utils.BlankInputStream();
        }
        QspConstants.isStart = true;
        HtmlEngine.isOpenSaveWindow = false;
        QspConstants.setGamePathById(actionScript);
        mGameShower.restartGame();
        return Utils.StringToInputStream("1");
    }

    public InputStream exportGameToText(GameShower mGameShower, String gameId) {
        String actionScript = gameId;
        if (StringUtils.isEmpty(actionScript)) {
            return Utils.BlankInputStream();
        }
        QspConstants.setGamePathById(actionScript);
        GameVo gameVo= QspConstants.GAME_FOLDER_MAP.get(gameId);

        new File(gameVo.getGameFolder()+"/exportText/").mkdir();

        mGameShower.getLibQspProxy().qspFileToText(gameVo,gameVo.getGameFolder()+"/exportText/source.txt");
        return Utils.StringToInputStream("1");
    }

    public InputStream exportGameToQsp(GameShower mGameShower, String gameId) {
        String actionScript = gameId;
        if (StringUtils.isEmpty(actionScript)) {
            return Utils.BlankInputStream();
        }
        QspConstants.setGamePathById(actionScript);

        GameVo gameVo= QspConstants.GAME_FOLDER_MAP.get(gameId);
        new File(gameVo.getGameFolder()+"/exportQsp/").mkdir();
        mGameShower.getLibQspProxy().toGemFile(gameVo, gameVo.getGameFolder()+"/exportQsp/game.qsp");
        return Utils.StringToInputStream("1");
    }
}
