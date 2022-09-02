package com.qsp.view.template;

import com.qsp.player.LibEngine;
import com.qsp.player.common.QspConstants;
import com.qsp.player.entity.QspListItem;
import com.qsp.view.common.UrlContants;
import com.qsp.view.util.ResponseUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * 游戏窗口-控制台 对应qsp的WindowType.OBJECTS
 *
 * @author cxy
 */
public class ConsoleTemplate {

    private Template actionTemplate;

    public ConsoleTemplate(VelocityEngine ve) {
        actionTemplate = ve.getTemplate("baijiacms/html/core/console.vm", QspConstants.CHARSET_STR);
    }

    public boolean handle(LibEngine libEngine, String target, HttpServletResponse response, String actionScript) throws Exception {
        String htmlCode = null;
        switch (target) {
            case UrlContants.CONSOLE_URL_ROOT + "engineConsolePage":
                htmlCode = getConsoleHtml();
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.CONSOLE_URL_ROOT + "html":
                htmlCode = getConsole(libEngine);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.CONSOLE_URL_ROOT + "consoleCall":
                htmlCode = execConsole(libEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            default:;
        }
        return false;
    }

    public String getConsoleHtml() {
        VelocityContext context = new VelocityContext();


        StringWriter writer = new StringWriter();
        actionTemplate.merge(context, writer);
        writer.flush();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public String getConsole(LibEngine libEngine) {
        StringBuffer response = new StringBuffer();
        libEngine.getGameStatus().setObjectschanged(false);
        if (libEngine.getGameStatus().isOpenSaveWindow()) {
            return QspConstants.BLANK_STR;
        }
        ArrayList<QspListItem> list = libEngine.refreshObjects();
        response.append(getConsoleActionHtml(list));
        return response.toString();
    }

    private String getConsoleActionHtml(ArrayList<QspListItem> list) {
        StringBuffer sb = new StringBuffer();
        for (QspListItem aspListItem : list) {

            sb.append("<button role=\"menuitem\" tabindex=\"0\" class=\"css-ic6cb5 egqqxmy0\">");
            sb.append("<div class=\"css-1e9tg5g eoej6yh0\" onclick=\"consoleTo('" + aspListItem.index + "')\"><b>" + aspListItem.text + "</b></div>\n");
            sb.append("</button>");
        }
        return sb.toString();
    }

    public String execConsole(LibEngine libEngine, String command) {
        // Looper.prepare();
        libEngine.onObjectSelected(Integer.parseInt(command));
        // Looper.loop();

        libEngine.getGameStatus().setObjectschanged(true);
        return QspConstants.BLANK_STR;
    }
}
