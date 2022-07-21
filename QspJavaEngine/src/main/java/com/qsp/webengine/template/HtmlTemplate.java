package com.qsp.webengine.template;

import com.qsp.webengine.HtmlEngine;
import com.qsp.webengine.util.Utils;
import com.qsp.player.core.QspGameStatus;
import com.qsp.player.core.game.GameShower;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
/**
 * 游戏窗口-内容窗口 对应qsp的WindowType.VARIABLES
 * @author cxy
 */
public class HtmlTemplate {
    private Template htmlTemplate;

    public HtmlTemplate(VelocityEngine ve) {

        htmlTemplate = ve.getTemplate("baijiacms/html/core/html.vm", "utf-8");
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

    public InputStream getHtml(GameShower mGameShower, GameSaveTemplate gameSaveTemplate) {
        QspGameStatus.maindescchanged = false;
        String html = null;
        if (HtmlEngine.isOpenSaveWindow == false) {

            html = mGameShower.refreshMainDesc();
        } else {
            html = gameSaveTemplate.getHtml();
        }
        return Utils.StringToInputStream(html);
    }

    public InputStream execHtml(GameShower mGameShower, String command) {

        mGameShower.shouldOverrideUrlLoading(command);
        QspGameStatus.varsdescchanged = true;
        QspGameStatus.actionschanged = true;
        QspGameStatus.maindescchanged = true;

        return Utils.BlankInputStream();
    }

}
