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
 * 游戏窗口-角色状态窗口 对应qsp的WindowType.VARIABLES
 *
 * @author cxy
 */
public class UserTemplate {


    private Template userTemplate;

    public UserTemplate(VelocityEngine ve) {

        userTemplate = ve.getTemplate("baijiacms/html/core/user.vm", QspConstants.CHARSET_STR);
    }

    public String getUserHtml() {
        VelocityContext context = new VelocityContext();

        StringWriter writer = new StringWriter();
        userTemplate.merge(context, writer);
        writer.flush();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public String getUser(PlayerEngine mPlayerEngine) {
        mPlayerEngine.getGameStatus().setVarsdescchanged(false);
        if (mPlayerEngine.getGameStatus().isOpenSaveWindow) {
            return StreamUtils.BLANK_STR;
        }
        String html = mPlayerEngine.refreshVarsDesc();
        return html;
    }

    public String execUser(PlayerEngine mPlayerEngine, String command) {

        mPlayerEngine.shouldOverrideUrlLoading(command);


        mPlayerEngine.getGameStatus().setVarsdescchanged(true);
        return StreamUtils.SUCCESS_STR;
    }
}
