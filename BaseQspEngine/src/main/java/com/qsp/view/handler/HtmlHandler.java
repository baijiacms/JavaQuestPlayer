package com.qsp.view.handler;

import com.qsp.player.LibEngine;
import com.qsp.player.util.StreamUtils;
import com.qsp.player.vi.audio.impl.util.mime.MyMediaTypeFactory;
import com.qsp.view.common.UrlContants;
import com.qsp.view.template.*;
import com.qsp.view.util.ResponseUtil;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author baijiacms
 */
public class HtmlHandler {
    private static final Logger logger = LoggerFactory.getLogger(HtmlHandler.class);

    private UserTemplate userTemplate;
    private ConsoleTemplate consoleTemplate;
    private ActionTemplate actionTemplate;
    private HtmlTemplate htmlTemplate;
    private IndexTemplate indexTemplate;
    private GameSelectTemplate gameSelectTemplate;
    private LoadingTemplate loadingTemplate;
    private GameSaveTemplate gameSaveTemplate;

    public HtmlHandler() {
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

    public boolean requestHandle(LibEngine libEngine, String actionScript, String target, HttpServletResponse response) throws Exception {
        if (target.startsWith(UrlContants.ENGINE_URL_ROOT)) {
            if (target.startsWith(UrlContants.GAME_SELECT_URL_ROOT)) {
                boolean result = gameSelectTemplate.handle(libEngine, target, response, actionScript);
                return result;
            }
            if (target.startsWith(UrlContants.LOADING_URL_ROOT)) {
                boolean result = loadingTemplate.handle(libEngine, actionScript, target, response);
                return result;
            }
        }
        if (target.startsWith(UrlContants.ENGINE_URL_ROOT + "lib/")) {///engine/lib/"
            InputStream byteResultStream = StreamUtils.getEngineResourceInputSteam(target);
            StreamUtils.copy(byteResultStream, response.getOutputStream());
            ResponseUtil.setContentType(response, MyMediaTypeFactory.getContentType(target));
            return true;
        }
        return false;
    }

    public boolean sessionHandle(LibEngine libEngine, String actionScript, String target, HttpServletResponse response) throws Exception {
        String loadGameUrl = UrlContants.INDEX_URL_ROOT + "loadGame";
        if (target.startsWith(UrlContants.ENGINE_URL_ROOT)) {
            if (loadGameUrl.equals(target) || libEngine.getGameStatus().isGameRunning()) {
                if (target.startsWith(UrlContants.GAME_SAVE_URL_ROOT)) {
                    boolean result = gameSaveTemplate.handle(libEngine, target, response, actionScript);
                    return result;
                }
                if (target.startsWith(UrlContants.INDEX_URL_ROOT)) {
                    boolean result = indexTemplate.handle(libEngine, target, response, actionScript);
                    return result;
                }
                if (target.startsWith(UrlContants.HTML_URL_ROOT)) {
                    boolean result = htmlTemplate.handle(libEngine, target, response, actionScript);
                    return result;
                }
                if (target.startsWith(UrlContants.CONSOLE_URL_ROOT)) {
                    boolean result = consoleTemplate.handle(libEngine, target, response, actionScript);
                    return result;
                }
                if (target.startsWith(UrlContants.ACTION_URL_ROOT)) {
                    boolean result = actionTemplate.handle(libEngine, target, response, actionScript);
                    return result;
                }
                if (target.startsWith(UrlContants.USER_URL_ROOT)) {
                    boolean result = userTemplate.handle(libEngine, target, response, actionScript);
                    return result;
                }
            }
        }
        return false;
    }


}