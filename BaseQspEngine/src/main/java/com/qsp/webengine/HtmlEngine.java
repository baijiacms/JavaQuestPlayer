package com.qsp.webengine;

import com.qsp.QspEngineCore;
import com.qsp.player.PlayerEngine;
import com.qsp.player.common.QspConstants;
import com.qsp.player.vi.AudioInterface;
import com.qsp.player.vi.ViewInterface;
import com.qsp.webengine.handler.MyQspHandler;
import com.qsp.webengine.template.*;
import com.qsp.webengine.util.MyUrlRequest;
import com.qsp.webengine.util.SteamUtils;
import com.qsp.webengine.util.Utils;
import com.qsp.webengine.util.mime.MyMediaTypeFactory;
import com.qsp.webengine.vo.ResponseVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author cxy
 */
public class HtmlEngine {
    private static final Logger logger = LoggerFactory.getLogger(HtmlEngine.class);
    private PlayerEngine mPlayerEngine;
    private UserTemplate userTemplate;
    private ConsoleTemplate consoleTemplate;
    private ActionTemplate actionTemplate;
    private HtmlTemplate htmlTemplate;
    private IndexTemplate indexTemplate;
    private GameSelectTemplate gameSelectTemplate;
    private LoadingTemplate loadingTemplate;
    private GameSaveTemplate gameSaveTemplate;
    private final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private final String JSON_CONTENT_TYPE = "application/json;charset=utf-8";

    public HtmlEngine(String userId, QspEngineCore qspEngineCore) {
        this(userId, qspEngineCore.getJavaFxViewImpl(), qspEngineCore.getJavaFxMediaPlayer());
    }

    private HtmlEngine(String userId, ViewInterface qspViewImpl, AudioInterface audioInterfaceImp) {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(Velocity.RESOURCE_LOADER, Velocity.RESOURCE_LOADER_CLASS);
        ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init();
        this.mPlayerEngine = new PlayerEngine(userId, this.getClass(), qspViewImpl, audioInterfaceImp);
        this.userTemplate = new UserTemplate(ve);
        this.actionTemplate = new ActionTemplate(ve);
        this.consoleTemplate = new ConsoleTemplate(ve);
        this.htmlTemplate = new HtmlTemplate(ve);
        this.indexTemplate = new IndexTemplate(mPlayerEngine, ve);
        this.gameSelectTemplate = new GameSelectTemplate(ve);
        this.loadingTemplate = new LoadingTemplate(ve);
        this.gameSaveTemplate = new GameSaveTemplate(mPlayerEngine, ve);

    }

    @Deprecated
    public void startProxy() {

        mPlayerEngine.getGameStatus().isProxy = true;
        URL.setURLStreamHandlerFactory(protocol -> {
            return new MyQspHandler(this);
        });
    }

    public ResponseVo getInputStream(URL url, String target) throws IOException {
        ResponseVo responseVo = new ResponseVo();
        target = target.replace(QspConstants.HTTP_LOCAL_URL, "");
        if (this.mPlayerEngine.getGameStatus().isStart) {
            target = target.replace(mPlayerEngine.getGameStatus().GAME_RESOURCE_PATH, "");
        }
        target = target.replace(QspConstants.ENGINE_RESOURCE_PATH, "");
        if (target.startsWith("/") == false) {
            target = "/" + target;
        }
        MyUrlRequest request = new MyUrlRequest(url);
        String htmlCode = null;
        String actionScript = null;
        if (target == null) {
            target = "";
        }
        InputStream responseStream = null;
        switch (target) {
            case "/engine/isNeedRefresh":
                htmlCode = "{\"actionschanged\":" + (mPlayerEngine.getGameStatus().isActionschanged() ? "true" : "false") + ",\"objectschanged\":" + (mPlayerEngine.getGameStatus().isObjectschanged() ? "true" : "false") + ",\"varsdescchanged\":" + (mPlayerEngine.getGameStatus().isVarsdescchanged() ? "true" : "false") + ",\"maindescchanged\":" + (mPlayerEngine.getGameStatus().isMaindescchanged(false) ? "true" : "false") + "}";
                responseStream = Utils.StringToInputStream(htmlCode);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(JSON_CONTENT_TYPE);
                return responseVo;
            case "/engine/isNeedRefreshHtml":
                responseStream = Utils.StringToInputStream(mPlayerEngine.getGameStatus().isMaindescchanged(false) ? "1" : "0");
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(JSON_CONTENT_TYPE);
                return responseVo;
            case "/engine/isNeedRefreshAction":
                responseStream = Utils.StringToInputStream(mPlayerEngine.getGameStatus().isActionschanged() ? "1" : "0");
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(JSON_CONTENT_TYPE);
                return responseVo;
            case "/engine/isNeedRefreshUser":
                responseStream = Utils.StringToInputStream(mPlayerEngine.getGameStatus().isVarsdescchanged() ? "1" : "0");
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(JSON_CONTENT_TYPE);
                return responseVo;
            case "/engine/isNeedRefreshConsole":
                responseStream = Utils.StringToInputStream(mPlayerEngine.getGameStatus().isObjectschanged() ? "1" : "0");
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(JSON_CONTENT_TYPE);
                return responseVo;
            case "/favicon.ico":
                responseStream = Utils.BlankInputStream();
                responseVo.setResponseStream(responseStream);

                responseVo.setContentType(MyMediaTypeFactory.getContentType(target));
                return responseVo;
            case "/engineHtmlPage":
                htmlCode = htmlTemplate.getHtmlHtml();
                responseStream = Utils.StringToInputStream(htmlCode);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engineUserPage":
                htmlCode = userTemplate.getUserHtml();
                responseStream = Utils.StringToInputStream(htmlCode);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engineConsolePage":
                htmlCode = consoleTemplate.getConsoleHtml();
                responseStream = Utils.StringToInputStream(htmlCode);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engineActionPage":
                htmlCode = actionTemplate.getActionHtml();
                responseStream = Utils.StringToInputStream(htmlCode);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/html":
                responseStream = this.htmlTemplate.getHtml(mPlayerEngine, gameSaveTemplate);

                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/user":
                responseStream = this.userTemplate.getUser(mPlayerEngine);

                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/console":
                responseStream = this.consoleTemplate.getConsole(mPlayerEngine);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/action":
                responseStream = actionTemplate.getAction(mPlayerEngine);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/htmlCall":
                actionScript = request.getParameter("actionScript").trim();
                responseStream = this.htmlTemplate.execHtml(mPlayerEngine, actionScript);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/userCall":
                actionScript = request.getParameter("actionScript").trim();
                responseStream = this.userTemplate.execUser(mPlayerEngine, actionScript);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/actionCall":
                actionScript = request.getParameter("actionScript").trim();
                responseStream = this.actionTemplate.execAction(mPlayerEngine, actionScript);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/consoleCall":
                actionScript = request.getParameter("actionScript").trim();
                // actionScript=new String( Base64.decode(actionScript.getBytes()),"utf-8");
                responseStream = this.consoleTemplate.execConsole(mPlayerEngine, actionScript);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/openSaveWindow":
                mPlayerEngine.getGameStatus().isOpenSaveWindow = true;
                mPlayerEngine.getGameStatus().refreshAll();
                responseStream = Utils.BlankInputStream();
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/closeSaveWindow":
                mPlayerEngine.getGameStatus().isOpenSaveWindow = false;
                mPlayerEngine.getGameStatus().refreshAll();
                responseStream = Utils.BlankInputStream();
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/deleteGameSave":
                actionScript = request.getParameter("actionScript").trim();
                if (StringUtils.isEmpty(actionScript)) {
                    responseStream = Utils.BlankInputStream();
                    responseVo.setResponseStream(responseStream);
                    responseVo.setContentType(HTML_CONTENT_TYPE);
                    return responseVo;
                }
                responseStream = this.gameSaveTemplate.deleteGameSave(mPlayerEngine, actionScript);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/GameSave":
                actionScript = request.getParameter("actionScript").trim();
                if (StringUtils.isEmpty(actionScript)) {
                    responseStream = Utils.BlankInputStream();
                    responseVo.setResponseStream(responseStream);
                    responseVo.setContentType(HTML_CONTENT_TYPE);
                    return responseVo;
                }

                responseStream = this.gameSaveTemplate.saveGame(mPlayerEngine, actionScript);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/LoadGameSave":
                actionScript = request.getParameter("actionScript").trim();
                if (StringUtils.isEmpty(actionScript)) {
                    responseStream = Utils.BlankInputStream();
                    responseVo.setResponseStream(responseStream);
                    responseVo.setContentType(HTML_CONTENT_TYPE);
                    return responseVo;
                }
                responseStream = this.gameSaveTemplate.loadSaveGame(mPlayerEngine, actionScript);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/QuickSave":
                responseStream = this.gameSaveTemplate.saveGame(mPlayerEngine, null);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/loadQuickSave":
                responseStream = this.gameSaveTemplate.loadSaveGame(mPlayerEngine, null);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/gameIndex"://游戏主界面
                htmlCode = indexTemplate.getIndexHtml();
                responseStream = Utils.StringToInputStream(htmlCode);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/":
            case "/engine.html"://游戏选择界面
                mPlayerEngine.getGameStatus().isStart = false;
                htmlCode = gameSelectTemplate.getHtml();
                responseStream = Utils.StringToInputStream(htmlCode);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/loadingPage":
                htmlCode = loadingTemplate.getHtml();
                responseStream = Utils.StringToInputStream(htmlCode);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/loadGame":
                actionScript = request.getParameter("actionScript").trim();
                responseStream = this.gameSelectTemplate.loadGame(mPlayerEngine, actionScript);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/exportGameToText":
                actionScript = request.getParameter("actionScript").trim();
                responseStream = this.gameSelectTemplate.exportGameToText(mPlayerEngine, actionScript);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            case "/engine/exportGameToQsp":
                actionScript = request.getParameter("actionScript").trim();
                responseStream = this.gameSelectTemplate.exportGameToQsp(mPlayerEngine, actionScript);
                responseVo.setResponseStream(responseStream);
                responseVo.setContentType(HTML_CONTENT_TYPE);
                return responseVo;
            default:
                if (target.startsWith("/engine/lib/")) {
                    responseStream = SteamUtils.getEngineResourceInputSteam(this.mPlayerEngine, target);
                    responseVo.setResponseStream(responseStream);
                    responseVo.setContentType(MyMediaTypeFactory.getContentType(target));
                    return responseVo;
                }
        }
        responseStream = SteamUtils.getGameResourceInputSteam(this.mPlayerEngine, target);
        responseVo.setResponseStream(responseStream);
        responseVo.setContentType(MyMediaTypeFactory.getContentType(target));
        return responseVo;
    }


}
