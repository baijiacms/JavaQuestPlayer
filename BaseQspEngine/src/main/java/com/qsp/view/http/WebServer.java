package com.qsp.view.http;

import com.qsp.player.LibEngine;
import com.qsp.player.common.FolderLoader;
import com.qsp.player.common.QspConstants;
import com.qsp.player.entity.QspGame;
import com.qsp.player.util.StreamUtils;
import com.qsp.player.vi.QspAudio;
import com.qsp.player.vi.QspUi;
import com.qsp.view.common.UrlContants;
import com.qsp.view.http.dto.QspHttpResponse;
import com.qsp.view.http.dto.QspHttpResponseImpl;
import com.qsp.view.http.handler.HtmlHandler;
import com.qsp.view.http.nanohttpd.NanoHTTPD;
import com.qsp.view.util.ResponseUtil;
import com.qsp.view.vi.audio.SwingAudio;
import com.qsp.view.vi.audio.WebAudio;
import com.qsp.view.vi.audio.mp3.mime.MyMediaTypeFactory;
import com.qsp.view.vi.box.SwingUi;
import com.qsp.view.vi.box.WebUi;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class WebServer extends NanoHTTPD {


    public WebServer(int port, boolean isGUI, String gameId) {
        super("127.0.0.1", port);
        htmlHandler = new HtmlHandler();

        if (isGUI) {
            qspUi = new SwingUi();
            qspAudio = new SwingAudio();
        } else {
            qspUi = new WebUi();
            qspAudio = new WebAudio();
        }
        singleLibEngine = new LibEngine(QspConstants.DEFAULT_USER, qspUi, qspAudio);
        if (StringUtils.isEmpty(gameId) == false) {
            QspGame qspGame = FolderLoader.getFolderMap().get(gameId);
            if (qspGame != null) {
                singleLibEngine.restartGame(qspGame);
            }
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        List<String> paramList = parameters.get("actionScript");
        String actionScript = null;
        if (paramList != null && paramList.size() > 0) {
            actionScript = paramList.get(0);
        }
        String uri = session.getUri();
        QspHttpResponse qspHttpResponse = new QspHttpResponseImpl();
        Response response = null;
        try {
            handle(uri, actionScript, qspHttpResponse);
            response = new NanoHTTPD.Response(Response.Status.OK, qspHttpResponse.getContentType(), qspHttpResponse.getInputStream(), qspHttpResponse.getTotalBytes());

        } catch (Exception e) {
            e.printStackTrace();
            response = new NanoHTTPD.Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, null, 0);
        }
//        Response response = new NanoHTTPD.Response(Response.Status.OK, MIME_PLAINTEXT, null, qspHttpResponse.getTotalBytes());
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,PUT,DELETE,PATCH,HEAD");
        response.addHeader("Access-Control-Allow-Max-Age", "3600");
        response.addHeader("Access-Control-Allow-Headers", "*");
        return response;
    }

    private HtmlHandler htmlHandler;

    private LibEngine singleLibEngine;

    private QspUi qspUi;
    private QspAudio qspAudio;

    public void handle(String target, String actionScript, QspHttpResponse qspHttpResponse) throws Exception {

        target = target.replace(QspConstants.HTTP_LOCAL_URL, "");
        target = target.replace(QspConstants.getEngineResourcePath(), "");
        if (target.startsWith("/") == false) {
            target = "/" + target;
        }
        if (target == null) {
            target = "";
        }
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
            default:
                ;
        }
        singleHandle(actionScript, target, qspHttpResponse);


    }

    public void singleHandle(String actionScript, String target, QspHttpResponse response) throws Exception {
        LibEngine libEngine = singleLibEngine;
        int isSession = UrlContants.isSessionPath(target);
        boolean hasResult = false;
        if (isSession == UrlContants.IS_SESSION) {
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
}
