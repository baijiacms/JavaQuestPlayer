package com.qsp.view.template;

import com.qsp.player.LibEngine;
import com.qsp.player.common.QspConstants;
import com.qsp.view.common.UrlContants;
import com.qsp.view.util.ResponseUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;

/**
 * 游戏窗口-内容窗口 对应qsp的WindowType.VARIABLES
 *
 * @author cxy
 */
public class HtmlTemplate {
    private Template htmlTemplate;
    private GameSaveTemplate gameSaveTemplate;

    public HtmlTemplate(VelocityEngine ve, GameSaveTemplate gameSaveTemplate) {
        this.gameSaveTemplate = gameSaveTemplate;
        htmlTemplate = ve.getTemplate("baijiacms/html/core/html.vm", QspConstants.CHARSET_STR);
    }

    public boolean handle(LibEngine libEngine, String target, HttpServletResponse response, String actionScript) throws Exception {
        String htmlCode = null;
        switch (target) {
            case UrlContants.HTML_URL_ROOT + "engineHtmlPage":
                htmlCode = getHtmlHtml();
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.HTML_URL_ROOT + "html":
                htmlCode = getHtml(libEngine);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.HTML_URL_ROOT + "htmlCall":
                htmlCode = execHtml(libEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            default:;
        }
        return false;
    }

    public String getHtmlHtml() {

        VelocityContext context = new VelocityContext();

        StringWriter writer = new StringWriter();
        htmlTemplate.merge(context, writer);
        writer.flush();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public String getHtml(LibEngine libEngine) {
        String html = null;
        if (libEngine.getGameStatus().isOpenSaveWindow() == false) {

            html = libEngine.refreshMainDesc();
        } else {
            html = gameSaveTemplate.getHtml(libEngine);
        }
        return html;
    }

    public String execHtml(LibEngine libEngine, String command) {

        libEngine.shouldOverrideUrlLoading(command);
        libEngine.getGameStatus().refreshAll();

        return QspConstants.SUCCESS_STR;
    }

}
