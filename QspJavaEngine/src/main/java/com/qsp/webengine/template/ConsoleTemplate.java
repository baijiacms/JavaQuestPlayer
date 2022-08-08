package com.qsp.webengine.template;

import com.qsp.webengine.util.Utils;
import com.qsp.player.PlayerEngine;
import com.qsp.player.libqsp.dto.QspListItem;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * 游戏窗口-控制台 对应qsp的WindowType.OBJECTS
 * @author cxy
 */
public class ConsoleTemplate {

    private Template actionTemplate;

    public ConsoleTemplate(VelocityEngine ve) {

        actionTemplate = ve.getTemplate("baijiacms/html/core/console.vm", "utf-8");
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

    public InputStream getConsole(PlayerEngine mPlayerEngine) {
        StringBuffer response = new StringBuffer();
        mPlayerEngine.getGameStatus().setObjectschanged(false);
        if (mPlayerEngine.getGameStatus().isOpenSaveWindow) {
            return Utils.BlankInputStream();
        }
        ArrayList<QspListItem> list = mPlayerEngine.refreshObjects();
        response.append(getConsoleActionHtml(list));
        return Utils.StringToInputStream(response.toString());
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

    public InputStream execConsole(PlayerEngine mPlayerEngine, String command) {
        // Looper.prepare();
        mPlayerEngine.onObjectSelected(Integer.parseInt(command));
        // Looper.loop();

        mPlayerEngine.getGameStatus().setObjectschanged(true);
        return Utils.BlankInputStream();
    }
}
