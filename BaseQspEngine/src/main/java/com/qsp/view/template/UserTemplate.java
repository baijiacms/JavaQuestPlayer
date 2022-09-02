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
 * 游戏窗口-角色状态窗口 对应qsp的WindowType.VARIABLES
 *
 * @author cxy
 */
public class UserTemplate {


    private Template userTemplate;

    public UserTemplate(VelocityEngine ve) {

        userTemplate = ve.getTemplate("baijiacms/html/core/user.vm", QspConstants.CHARSET_STR);
    }

    public boolean handle(LibEngine libEngine, String target, HttpServletResponse response, String actionScript) throws Exception {
        String htmlCode = null;
        switch (target) {
            case UrlContants.USER_URL_ROOT + "engineUserPage":
                htmlCode = getUserHtml();
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.USER_URL_ROOT + "html":
                htmlCode = getUser(libEngine);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.USER_URL_ROOT + "userCall":
                htmlCode = execUser(libEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            default:;
        }
        return false;
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

    public String getUser(LibEngine libEngine) {
        libEngine.getGameStatus().setVarsdescchanged(false);
        if (libEngine.getGameStatus().isOpenSaveWindow()) {
            return QspConstants.BLANK_STR;
        }
        String html = libEngine.refreshVarsDesc();
        return html;
    }

    public String execUser(LibEngine libEngine, String command) {

        libEngine.shouldOverrideUrlLoading(command);


        libEngine.getGameStatus().setVarsdescchanged(true);
        return QspConstants.SUCCESS_STR;
    }
}
