package com.baijiacms.qsp.template;

import com.baijiacms.qsp.common.QspConstants;
import com.baijiacms.qsp.dto.QspListItem;
import com.baijiacms.qsp.player.PlayerEngine;
import com.baijiacms.qsp.util.StreamUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * 游戏窗口-动作选择界面 对应qsp的WindowType.ACTIONS
 *
 * @author cxy
 */
public class ActionTemplate {

    private Template actionTemplate;

    public ActionTemplate(VelocityEngine ve) {

        actionTemplate = ve.getTemplate("baijiacms/html/core/action.vm", QspConstants.CHARSET_STR);
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

    public String getAction(PlayerEngine mPlayerEngine) {
        StringBuffer response = new StringBuffer();
        mPlayerEngine.getGameStatus().setActionschanged(false);
        if (mPlayerEngine.getGameStatus().isOpenSaveWindow) {
            return StreamUtils.BLANK_STR;
        }
        ArrayList<QspListItem> list = mPlayerEngine.refreshActions();
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
     * @param mPlayerEngine
     * @param command
     * @return
     */
    public String execAction(PlayerEngine mPlayerEngine, String command) {

        mPlayerEngine.onItemClick(Integer.parseInt(command));
        mPlayerEngine.getGameStatus().setActionschanged(true);

        return StreamUtils.BLANK_STR;
    }
}
