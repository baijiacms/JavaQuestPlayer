package com.qsp.view.handler;

import com.qsp.player.LibEngine;
import com.qsp.player.common.FolderLoader;
import com.qsp.player.common.QspConstants;
import com.qsp.player.entity.QspGame;
import com.qsp.player.util.StreamUtils;
import com.qsp.player.vi.QspAudio;
import com.qsp.view.vi.audio.SwingAudio;
import com.qsp.view.vi.audio.WebAudio;
import com.qsp.view.vi.audio.mp3.mime.MyMediaTypeFactory;
import com.qsp.player.vi.QspUi;
import com.qsp.view.vi.box.SwingUi;
import com.qsp.view.vi.box.WebUi;
import com.qsp.view.common.UrlContants;
import com.qsp.view.multiple.UserManager;
import com.qsp.view.multiple.dto.User;
import com.qsp.view.multiple.dto.UserId;
import com.qsp.view.template.*;
import com.qsp.view.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author baijiacms
 */
public class ServerHandler extends AbstractHandler {
    private HtmlHandler htmlHandler;

    private UserTemplate userTemplate;
    private ConsoleTemplate consoleTemplate;
    private ActionTemplate actionTemplate;
    private HtmlTemplate htmlTemplate;
    private IndexTemplate indexTemplate;
    private GameSelectTemplate gameSelectTemplate;
    private LoadingTemplate loadingTemplate;
    private GameSaveTemplate gameSaveTemplate;

    private LibEngine singleLibEngine;

    private boolean isMultiple;
    QspUi qspUi;
    QspAudio qspAudio;

    public ServerHandler(boolean isGUI, boolean isMultiple,String gameId) {
        initVelocityEngine();
        this.isMultiple = isMultiple;
        htmlHandler = new HtmlHandler();

        if (isGUI) {
            qspUi = new SwingUi();
            qspAudio = new SwingAudio();
        } else {
            qspUi = new WebUi();
            qspAudio = new WebAudio();
        }


        if (isMultiple == false) {
            singleLibEngine = new LibEngine(QspConstants.DEFAULT_USER, qspUi, qspAudio);
            if(StringUtils.isEmpty(gameId)==false)
            {
                QspGame qspGame= FolderLoader.getFolderMap().get(gameId);
                if(qspGame!=null) {
                    singleLibEngine.restartGame(qspGame);
                }
            }
        }
    }

    private void initVelocityEngine() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(Velocity.RESOURCE_LOADER, Velocity.RESOURCE_LOADER_CLASS);
        ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init();
        this.userTemplate = new UserTemplate(ve);
        this.actionTemplate = new ActionTemplate(ve);
        this.consoleTemplate = new ConsoleTemplate(ve);
        this.gameSaveTemplate = new GameSaveTemplate(ve);
        this.htmlTemplate = new HtmlTemplate(ve, gameSaveTemplate);
        this.indexTemplate = new IndexTemplate(ve);
        this.gameSelectTemplate = new GameSelectTemplate(ve);
        this.loadingTemplate = new LoadingTemplate(ve);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        target = target.replace(QspConstants.HTTP_LOCAL_URL, "");
        target = target.replace(QspConstants.getEngineResourcePath(), "");
        if (target.startsWith("/") == false) {
            target = "/" + target;
        }
        if (target == null) {
            target = "";
        }
        String actionScript = request.getParameter("actionScript");
        if (actionScript != null) {
            actionScript.trim();
        } else {
            actionScript = QspConstants.BLANK_STR;
        }
        switch (target) {
            case "/"://游戏选择界面
            case "/index.html"://游戏选择界面
            case "/index.htm"://游戏选择界面
                target = UrlContants.GAME_SELECT_URL_ROOT + "index.html";
                break;
            case "/favicon.ico":
                target = UrlContants.GAME_SELECT_URL_ROOT + "blank.html";
                break;
            default:;
        }
        if (isMultiple) {
            sessionHandle(request.getSession().getId(), actionScript, target, response);

        } else {
            singleHandle(actionScript, target, response);

        }

    }

    public void singleHandle(String actionScript, String target, HttpServletResponse response) throws IOException, ServletException {
        LibEngine libEngine = singleLibEngine;
        int isSession = UrlContants.isSessionPath(target);
        boolean hasResult = false;
        if (isSession == UrlContants.IS_SESSION || isSession == UrlContants.IS_NEW_SESSION) {
            try {
                hasResult = htmlHandler.sessionHandle(libEngine, actionScript, target, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (isSession == UrlContants.IS_NOT_SESSION) {
                try {
                    hasResult = htmlHandler.requestHandle(libEngine, actionScript, target, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (hasResult == false) {
            InputStream byteResultStream = null;
            byteResultStream = StreamUtils.getGameResourceInputSteam(libEngine, target);
            StreamUtils.copy(byteResultStream, response.getOutputStream());
            ResponseUtil.setContentType(response, MyMediaTypeFactory.getContentType(target));
        }
        return;
    }

    public void sessionHandle(String sessionId, String actionScript, String target, HttpServletResponse response) throws IOException, ServletException {
        int isSession = UrlContants.isSessionPath(target);
        boolean hasResult = false;
        UserId userId = UserManager.INSTANCE.getUserIdBySessionId(sessionId);
        User user = null;
        if (userId != null) {
            user = UserManager.INSTANCE.getUser(userId.getUserId());
        }
        if (isSession == UrlContants.IS_SESSION || isSession == UrlContants.IS_NEW_SESSION) {

            if (user == null && isSession == UrlContants.IS_NEW_SESSION) {
                user = UserManager.INSTANCE.getOrNewUserBySessionId(sessionId, qspUi, qspAudio);
            }
            if (user != null) {
                try {
                    hasResult = htmlHandler.sessionHandle(user.getLibEngine(), actionScript, target, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ResponseUtil.stringWriteToResponse(response, QspConstants.BLANK_STR);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return;
            }

        } else {
            if (isSession == UrlContants.IS_NOT_SESSION) {
                try {
                    hasResult = htmlHandler.requestHandle(user != null ? user.getLibEngine() : null, actionScript, target, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (hasResult == false) {
            if (user != null && user.getLibEngine() != null) {
                InputStream byteResultStream = StreamUtils.getGameResourceInputSteam(user.getLibEngine(), target);
                StreamUtils.copy(byteResultStream, response.getOutputStream());
                ResponseUtil.setContentType(response, MyMediaTypeFactory.getContentType(target));
                return;
            } else {

                ResponseUtil.stringWriteToResponse(response, QspConstants.BLANK_STR);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return;
            }
        }

    }
}
