package com.qsp.webengine.template;

import com.qsp.player.PlayerEngine;
import com.qsp.player.common.GameFolderLoader;
import com.qsp.player.common.QspConstants;
import com.qsp.player.util.IoUtil;
import com.qsp.webengine.vo.GameVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏选择界面
 *
 * @author cxy
 */
public class GameSelectTemplate {
    private Template indexTemplate;

    public GameSelectTemplate(VelocityEngine ve) {

        indexTemplate = ve.getTemplate("baijiacms/html/center/main.vm", "utf-8");
    }

    public String getHtml() {
        List<GameVo> gameList = new ArrayList<>();
        GameFolderLoader.loadGameFolder(gameList);
        VelocityContext context = new VelocityContext();

        context.put("engineTitle", QspConstants.ENGINE_TITLE);
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


    public InputStream loadGame(PlayerEngine mPlayerEngine, String gameId) {

        if (StringUtils.isEmpty(gameId)) {
            return IoUtil.blankInputStream();
        }
        mPlayerEngine.getGameStatus().isOpenSaveWindow = false;
        mPlayerEngine.restartGame(gameId);
        return IoUtil.stringToInputStream("1");
    }

    public InputStream exportGameToText(PlayerEngine mPlayerEngine, String gameId) {
        String actionScript = gameId;
        if (StringUtils.isEmpty(actionScript)) {
            return IoUtil.blankInputStream();
        }
        mPlayerEngine.getGameStatus().setGamePathById(actionScript);
        GameVo gameVo = GameFolderLoader.getFolderMap().get(gameId);

        new File(gameVo.getGameFolder() + "/exportText/").mkdir();

        mPlayerEngine.getLibQspProxy().qspFileToText(gameVo, gameVo.getGameFolder() + "/exportText/source.txt");
        return IoUtil.stringToInputStream("1");
    }

    public InputStream exportGameToQsp(PlayerEngine mPlayerEngine, String gameId) {
        String actionScript = gameId;
        if (StringUtils.isEmpty(actionScript)) {
            return IoUtil.blankInputStream();
        }
        mPlayerEngine.getGameStatus().setGamePathById(actionScript);

        GameVo gameVo = GameFolderLoader.getFolderMap().get(gameId);
        new File(gameVo.getGameFolder() + "/exportQsp/").mkdir();
        mPlayerEngine.getLibQspProxy().toGemFile(gameVo, gameVo.getGameFolder() + "/exportQsp/game.qsp");
        return IoUtil.stringToInputStream("1");
    }
}
