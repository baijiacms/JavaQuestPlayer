package com.baijiacms.qsp.handler;

import com.baijiacms.qsp.common.QspConstants;
import com.baijiacms.qsp.player.PlayerEngine;
import com.baijiacms.qsp.util.ResponseUtil;
import com.baijiacms.qsp.util.StreamUtils;
import com.baijiacms.qsp.util.UrlRequestUtil;
import com.baijiacms.qsp.util.mime.MyMediaTypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author cxy
 */
public class HtmlHandler {
    private static final Logger logger = LoggerFactory.getLogger(HtmlHandler.class);
    private PlayerEngine mPlayerEngine;
    private final String HTML_CONTENT_TYPE = "text/html;charset=" + QspConstants.CHARSET_STR;
    private final String JSON_CONTENT_TYPE = "application/json;charset=" + QspConstants.CHARSET_STR;
    private EngineHandler engineHandler;

    public HtmlHandler(EngineHandler engineHandler) {
        this.engineHandler = engineHandler;
        this.mPlayerEngine = new PlayerEngine(this.getClass());
    }


    public void handle(URL url, String target, HttpServletResponse response) throws IOException {

        target = target.replace(QspConstants.HTTP_LOCAL_URL, "");
        if (this.mPlayerEngine.getGameStatus().isStart) {
            target = target.replace(mPlayerEngine.getGameStatus().gameResourcePath, "");
        }
        target = target.replace(QspConstants.ENGINE_RESOURCE_PATH, "");
        if (target.startsWith("/") == false) {
            target = "/" + target;
        }
        UrlRequestUtil request = new UrlRequestUtil(url);
        String htmlCode = null;
        String actionScript = null;
        if (target == null) {
            target = "";
        }
        InputStream byteResultStream = null;
        String result = null;
        switch (target) {
            case "/engine/isNeedRefresh":
                htmlCode = "{\"actionschanged\":" + (mPlayerEngine.getGameStatus().isActionschanged() ? "true" : "false") + ",\"objectschanged\":" + (mPlayerEngine.getGameStatus().isObjectschanged() ? "true" : "false") + ",\"varsdescchanged\":" + (mPlayerEngine.getGameStatus().isVarsdescchanged() ? "true" : "false") + ",\"maindescchanged\":" + (mPlayerEngine.getGameStatus().isMaindescchanged(false) ? "true" : "false") + "}";
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, JSON_CONTENT_TYPE);
                return;
            case "/engine/isNeedRefreshHtml":
                result = mPlayerEngine.getGameStatus().isMaindescchanged(false) ? "1" : "0";
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, JSON_CONTENT_TYPE);
                return;
            case "/engine/isNeedRefreshAction":
                result = mPlayerEngine.getGameStatus().isActionschanged() ? "1" : "0";
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, JSON_CONTENT_TYPE);
                return;
            case "/engine/isNeedRefreshUser":
                result = mPlayerEngine.getGameStatus().isVarsdescchanged() ? "1" : "0";
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, JSON_CONTENT_TYPE);
                return;
            case "/engine/isNeedRefreshConsole":
                result = mPlayerEngine.getGameStatus().isObjectschanged() ? "1" : "0";
                ResponseUtil.stringWriteToResponse(response, result);
                ResponseUtil.setContentType(response, JSON_CONTENT_TYPE);
                return;
            case "/favicon.ico":
                ResponseUtil.stringWriteToResponse(response, StreamUtils.BLANK_STR);
                ResponseUtil.setContentType(response, MyMediaTypeFactory.getContentType(target));
                return;
            case "/engineHtmlPage":
                htmlCode = this.engineHandler.getHtmlTemplate().getHtmlHtml();
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engineUserPage":
                htmlCode = this.engineHandler.getUserTemplate().getUserHtml();
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engineConsolePage":
                htmlCode = this.engineHandler.getConsoleTemplate().getConsoleHtml();
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engineActionPage":
                htmlCode = this.engineHandler.getActionTemplate().getActionHtml();
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/html":
                htmlCode = this.engineHandler.getHtmlTemplate().getHtml(mPlayerEngine, this.engineHandler.getGameSaveTemplate());
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/user":
                htmlCode = this.engineHandler.getUserTemplate().getUser(mPlayerEngine);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/console":
                htmlCode = this.engineHandler.getConsoleTemplate().getConsole(mPlayerEngine);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/action":
                htmlCode = this.engineHandler.getActionTemplate().getAction(mPlayerEngine);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/htmlCall":
                actionScript = request.getParameter("actionScript").trim();
                htmlCode = this.engineHandler.getHtmlTemplate().execHtml(mPlayerEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/userCall":
                actionScript = request.getParameter("actionScript").trim();
                htmlCode = this.engineHandler.getUserTemplate().execUser(mPlayerEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/actionCall":
                actionScript = request.getParameter("actionScript").trim();
                htmlCode = this.engineHandler.getActionTemplate().execAction(mPlayerEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/consoleCall":
                actionScript = request.getParameter("actionScript").trim();
                htmlCode = this.engineHandler.getConsoleTemplate().execConsole(mPlayerEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/openSaveWindow":
                mPlayerEngine.getGameStatus().isOpenSaveWindow = true;
                mPlayerEngine.getGameStatus().refreshAll();
                ResponseUtil.stringWriteToResponse(response, StreamUtils.SUCCESS_STR);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/closeSaveWindow":
                mPlayerEngine.getGameStatus().isOpenSaveWindow = false;
                mPlayerEngine.getGameStatus().refreshAll();
                ResponseUtil.stringWriteToResponse(response, StreamUtils.SUCCESS_STR);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/deleteGameSave":
                actionScript = request.getParameter("actionScript").trim();
                if (StringUtils.isEmpty(actionScript)) {
                    ResponseUtil.stringWriteToResponse(response, StreamUtils.BLANK_STR);
                    ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                    return;
                }
                htmlCode = this.engineHandler.getGameSaveTemplate().deleteGameSave(mPlayerEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/GameSave":
                actionScript = request.getParameter("actionScript").trim();
                if (StringUtils.isEmpty(actionScript)) {
                    ResponseUtil.stringWriteToResponse(response, StreamUtils.BLANK_STR);
                    ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                    return;
                }

                htmlCode = this.engineHandler.getGameSaveTemplate().saveGame(mPlayerEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/LoadGameSave":
                actionScript = request.getParameter("actionScript").trim();
                if (StringUtils.isEmpty(actionScript)) {
                    ResponseUtil.stringWriteToResponse(response, StreamUtils.BLANK_STR);
                    ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                    return;
                }
                htmlCode = this.engineHandler.getGameSaveTemplate().loadSaveGame(mPlayerEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/QuickSave":
                htmlCode = this.engineHandler.getGameSaveTemplate().saveGame(mPlayerEngine, null);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/loadQuickSave":
                htmlCode = this.engineHandler.getGameSaveTemplate().loadSaveGame(mPlayerEngine, null);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/gameIndex"://游戏主界面
                htmlCode = this.engineHandler.getIndexTemplate().getIndexHtml(mPlayerEngine.getGameStatus());
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/":
            case "/engine.html"://游戏选择界面
                mPlayerEngine.getGameStatus().isStart = false;
                htmlCode = this.engineHandler.getGameSelectTemplate().getHtml();
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/loadingPage":
                htmlCode = this.engineHandler.getLoadingTemplate().getHtml();
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/loadGame":
                actionScript = request.getParameter("actionScript").trim();
                htmlCode = this.engineHandler.getGameSelectTemplate().loadGame(mPlayerEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/exportGameToText":
                actionScript = request.getParameter("actionScript").trim();
                htmlCode = this.engineHandler.getGameSelectTemplate().exportGameToText(mPlayerEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            case "/engine/exportGameToQsp":
                actionScript = request.getParameter("actionScript").trim();
                htmlCode = this.engineHandler.getGameSelectTemplate().exportGameToQsp(mPlayerEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, HTML_CONTENT_TYPE);
                return;
            default:
                if (target.startsWith("/engine/lib/")) {
                    byteResultStream = StreamUtils.getEngineResourceInputSteam(this.mPlayerEngine, target);
                    StreamUtils.copy(byteResultStream, response.getOutputStream());
                    ResponseUtil.setContentType(response, MyMediaTypeFactory.getContentType(target));
                    return;
                }
        }
        byteResultStream = StreamUtils.getGameResourceInputSteam(this.mPlayerEngine, target);
        StreamUtils.copy(byteResultStream, response.getOutputStream());
        ResponseUtil.setContentType(response, MyMediaTypeFactory.getContentType(target));
        return;
    }


}
