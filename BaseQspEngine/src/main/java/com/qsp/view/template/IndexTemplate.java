package com.qsp.view.template;

import com.qsp.player.LibEngine;
import com.qsp.player.common.FolderLoader;
import com.qsp.player.common.QspConstants;
import com.qsp.player.entity.QspGame;
import com.qsp.view.common.UrlContants;
import com.qsp.view.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

/**
 * 游戏主窗口，iframe关联其他窗口
 *
 * @author cxy
 */
public class IndexTemplate {
    private Template indexTemplate;
    private Template indexSobTemplate;
    private Template indexBigKuyashTemplate;

    public IndexTemplate(VelocityEngine ve) {
        indexTemplate = ve.getTemplate("baijiacms/html/core/index.vm", QspConstants.CHARSET_STR);
        indexSobTemplate = ve.getTemplate("baijiacms/html/diy/sobIndex.vm", QspConstants.CHARSET_STR);
        indexBigKuyashTemplate = ve.getTemplate("baijiacms/html/diy/bigKuyashIndex.vm", QspConstants.CHARSET_STR);
    }

    public boolean handle(LibEngine libEngine, String target, HttpServletResponse response, String actionScript) throws Exception {
        String result;
        switch (target) {
            case UrlContants.INDEX_URL_ROOT + "isNeedRefresh":
                result = "{\"actionschanged\":" + (libEngine.isActionschanged() ? "true" : "false") + ",\"objectschanged\":" + (libEngine.isObjectschanged() ? "true" : "false") + ",\"varsdescchanged\":" + (libEngine.isVarsdescchanged() ? "true" : "false") + ",\"maindescchanged\":" + (libEngine.isMaindescchanged(false) ? "true" : "false") + "}";
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, QspConstants.JSON_CONTENT_TYPE);
                return true;
            case UrlContants.INDEX_URL_ROOT + "isNeedRefreshHtml":
                result = libEngine.isMaindescchanged(false) ? "1" : "0";
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, QspConstants.JSON_CONTENT_TYPE);
                return true;
            case UrlContants.INDEX_URL_ROOT + "isNeedRefreshAction":
                result = libEngine.isActionschanged() ? "1" : "0";
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, QspConstants.JSON_CONTENT_TYPE);
                return true;
            case UrlContants.INDEX_URL_ROOT + "isNeedRefreshUser":
                result = libEngine.isVarsdescchanged() ? "1" : "0";
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, QspConstants.JSON_CONTENT_TYPE);
                return true;
            case UrlContants.INDEX_URL_ROOT + "isNeedRefreshConsole":
                result = libEngine.isObjectschanged() ? "1" : "0";
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, QspConstants.JSON_CONTENT_TYPE);
                return true;
            case UrlContants.INDEX_URL_ROOT + "gameIndex"://游戏主界面
                result = getIndexHtml(libEngine.getQspGame());
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.INDEX_URL_ROOT + "loadGame":
                result = loadGame(libEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.INDEX_URL_ROOT + "exportGameToText":
                result = exportGameToText(libEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.INDEX_URL_ROOT + "exportGameToQsp":
                result = exportGameToQsp(libEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            default:;
        }
        return false;
    }

    public String loadGame(LibEngine libEngine, String gameId) {

        if (StringUtils.isEmpty(gameId)) {
            return QspConstants.BLANK_STR;
        }
        QspGame qspGame= FolderLoader.getFolderMap().get(gameId);
        if(qspGame!=null) {
            libEngine.restartGame(FolderLoader.getFolderMap().get(gameId));
            return QspConstants.SUCCESS_STR;
        }
        return QspConstants.BLANK_STR;
    }

    public String exportGameToText(LibEngine libEngine, String gameId) {
        String actionScript = gameId;
        if (StringUtils.isEmpty(actionScript)) {
            return QspConstants.BLANK_STR;
        }
        QspGame gameVo = FolderLoader.getFolderMap().get(gameId);

        new File(gameVo.getGameFolder() + "/exportText/").mkdir();

        libEngine.getLibQspProxy().qspFileToText(gameVo, gameVo.getGameFolder() + "/exportText/source.txt");
        return QspConstants.SUCCESS_STR;
    }

    public String exportGameToQsp(LibEngine libEngine, String gameId) {
        String actionScript = gameId;
        if (StringUtils.isEmpty(actionScript)) {
            return QspConstants.BLANK_STR;
        }

        QspGame gameVo = FolderLoader.getFolderMap().get(gameId);
        new File(gameVo.getGameFolder() + "/exportQsp/").mkdir();
        libEngine.getLibQspProxy().toGemFile(gameVo, gameVo.getGameFolder() + "/exportQsp/game.qsp");
        return QspConstants.SUCCESS_STR;
    }

    private String getIndexHtml(QspGame qspGame) {
        VelocityContext context = new VelocityContext();
        if (qspGame == null) {
            qspGame = new QspGame();
        }
//        context.put("screen_width", screen_width);
//        context.put("screen_height", screen_height);
//        context.put("user_width", user_width);
//        context.put("user_height", user_height);
//        context.put("action_width", action_width);
//        context.put("action_height", action_height);
//        context.put("root_width", root_width);
//        context.put("root_height", root_height);

        context.put("gameTitle", qspGame.getGameName());
        context.put("gameVersion", qspGame.getGameVersion());
        context.put("enginePowerBy", QspConstants.ENGINE_POWER_BY);
        context.put("engineTitle", QspConstants.ENGINE_TITLE);
        context.put("engineVersion", QspConstants.ENGINE_VERSION);


        StringWriter writer = new StringWriter();
        if (qspGame.isSob()) {
            indexSobTemplate.merge(context, writer);
        } else {
            if (qspGame.isBigKuyash()) {
                indexBigKuyashTemplate.merge(context, writer);
            } else {
                indexTemplate.merge(context, writer);
            }
        }
        writer.flush();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }
}
