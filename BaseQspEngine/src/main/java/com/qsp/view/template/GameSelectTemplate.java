package com.qsp.view.template;

import com.qsp.player.LibEngine;
import com.qsp.player.common.FolderLoader;
import com.qsp.player.common.QspConstants;
import com.qsp.player.entity.QspGame;
import com.qsp.player.vi.audio.impl.util.mime.MyMediaTypeFactory;
import com.qsp.view.common.UrlContants;
import com.qsp.view.util.ResponseUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        indexTemplate = ve.getTemplate("baijiacms/html/center/main.vm", QspConstants.CHARSET_STR);
    }

    public boolean handle(LibEngine libEngine, String target, HttpServletResponse response, String actionScript) throws Exception {
        switch (target) {
            case UrlContants.GAME_SELECT_URL_ROOT + "index.html"://游戏选择界面
                if (libEngine != null && libEngine.getGameStatus() != null) {
                    libEngine.getGameStatus().setGameRunning(false);
                }
                String htmlCode = getHtml();
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.GAME_SELECT_URL_ROOT + "blank.html":
                ResponseUtil.stringWriteToResponse(response, QspConstants.BLANK_STR);
                ResponseUtil.setContentType(response, MyMediaTypeFactory.getContentType(target));
                return true;
            default:
        }
        return false;
    }

    public String getHtml() {
        List<QspGame> gameList = new ArrayList<>();
        FolderLoader.loadGameFolder(gameList);
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


}
