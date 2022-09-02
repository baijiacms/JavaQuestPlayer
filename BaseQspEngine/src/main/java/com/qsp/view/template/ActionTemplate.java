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
 * 游戏窗口-动作选择界面 对应qsp的WindowType.ACTIONS
 *
 * @author baijiacms
 * @version V1.0.0
 */
public class ActionTemplate {
    private Template actionTemplate;

    public ActionTemplate(VelocityEngine ve) {
        actionTemplate = ve.getTemplate("baijiacms/html/core/action.vm", QspConstants.CHARSET_STR);
    }

    public boolean handle(LibEngine libEngine, String target, HttpServletResponse response, String actionScript) throws Exception {
        String htmlCode = null;
        switch (target) {
            case UrlContants.ACTION_URL_ROOT + "engineActionPage":
                htmlCode = getActionHtml();
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.ACTION_URL_ROOT + "html":
                htmlCode = getAction(libEngine);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.ACTION_URL_ROOT + "actionCall":
                htmlCode = execAction(libEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            default:;
        }
        return false;
    }

    public String getActionHtml() {
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

    public String getAction(LibEngine libEngine) {
        StringBuffer response = new StringBuffer();
        libEngine.getGameStatus().setActionschanged(false);
        if (libEngine.getGameStatus().isOpenSaveWindow()) {
            return QspConstants.BLANK_STR;
        }
        ArrayList<QspListItem> list = libEngine.refreshActions();
        response.append(getActionListHtml(list));
        return response.toString();
    }

    /**
     * 生成动作按钮
     *
     * @param list
     * @return
     */
    private String getActionListHtml(ArrayList<QspListItem> list) {
        StringBuffer sb = new StringBuffer();
        for (QspListItem aspListItem : list) {

            sb.append("<button role=\"menuitem\" tabindex=\"0\" class=\"css-ic6cb5 egqqxmy0\">");
            sb.append("<div class=\"css-1e9tg5g eoej6yh0\" onclick=\"actionTo('" + aspListItem.index + "')\"><b>" + aspListItem.text + "</b></div>\n");
            sb.append("</button>");
        }
        return sb.toString();
    }

    /**
     * 执行动作按钮
     *
     * @param command
     * @return
     */
    public String execAction(LibEngine libEngine, String command) {

        libEngine.onItemClick(Integer.parseInt(command));
        libEngine.getGameStatus().setActionschanged(true);

        return QspConstants.BLANK_STR;
    }
}
