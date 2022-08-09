package com.qsp.webengine.template;

import com.qsp.player.PlayerEngine;
import com.qsp.webengine.util.Utils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * 游戏窗口-角色状态窗口 对应qsp的WindowType.VARIABLES
 *
 * @author cxy
 */
public class UserTemplate {


    private Template userTemplate;

    public UserTemplate(VelocityEngine ve) {

        userTemplate = ve.getTemplate("baijiacms/html/core/user.vm", "utf-8");
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

    public InputStream getUser(PlayerEngine mPlayerEngine) {
        mPlayerEngine.getGameStatus().setVarsdescchanged(false);
        if (mPlayerEngine.getGameStatus().isOpenSaveWindow) {
            return Utils.BlankInputStream();
        }
        String html = mPlayerEngine.refreshVarsDesc();
        return Utils.StringToInputStream(html);
    }

    public InputStream execUser(PlayerEngine mPlayerEngine, String command) {
        // actionScript=new String( Base64.decode(actionScript.getBytes()),"utf-8");

        mPlayerEngine.shouldOverrideUrlLoading(command);


        mPlayerEngine.getGameStatus().setVarsdescchanged(true);
        return Utils.BlankInputStream();
    }
}
