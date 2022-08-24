package com.baijiacms.qsp.template;

import com.baijiacms.qsp.common.QspConstants;
import com.baijiacms.qsp.player.PlayerEngine;
import com.baijiacms.qsp.util.StreamUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.StringWriter;

/**
 * 游戏窗口-内容窗口 对应qsp的WindowType.VARIABLES
 *
 * @author cxy
 */
public class HtmlTemplate {
    private Template htmlTemplate;

    public HtmlTemplate(VelocityEngine ve) {

        htmlTemplate = ve.getTemplate("baijiacms/html/core/html.vm", QspConstants.CHARSET_STR);
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

    public String getHtml(PlayerEngine mPlayerEngine, GameSaveTemplate gameSaveTemplate) {
        String html = null;
        if (mPlayerEngine.getGameStatus().isOpenSaveWindow == false) {

            html = mPlayerEngine.refreshMainDesc();
        } else {
            html = gameSaveTemplate.getHtml(mPlayerEngine.getGameStatus());
        }
        return html;
    }

    public String execHtml(PlayerEngine mPlayerEngine, String command) {

        mPlayerEngine.shouldOverrideUrlLoading(command);
        mPlayerEngine.getGameStatus().refreshAll();

        return StreamUtils.SUCCESS_STR;
    }

}
