package com.qsp.webengine.template;

import com.qsp.webengine.HtmlEngine;
import com.qsp.webengine.util.Utils;
import com.qsp.player.libqsp.QspGameStatus;
import com.qsp.player.GameShower;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * 游戏窗口-角色状态窗口 对应qsp的WindowType.VARIABLES
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

    public InputStream getUser(GameShower mGameShower) {
        QspGameStatus.varsdescchanged = false;
        if (HtmlEngine.isOpenSaveWindow) {
            return Utils.BlankInputStream();
        }
        String html = mGameShower.refreshVarsDesc();
        return Utils.StringToInputStream(html);
    }

    public InputStream execUser(GameShower mGameShower, String command) {
        // actionScript=new String( Base64.decode(actionScript.getBytes()),"utf-8");

        mGameShower.shouldOverrideUrlLoading(command);


        QspGameStatus.varsdescchanged = true;
        return Utils.BlankInputStream();
    }
}
