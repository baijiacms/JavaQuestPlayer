package com.qsp.webengine.template;

import com.qsp.webengine.HtmlEngine;
import com.qsp.webengine.util.Utils;
import com.qsp.player.core.QspGameStatus;
import com.qsp.player.core.game.GameShower;
import com.qsp.player.core.model.QspListItem;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * 游戏窗口-动作选择界面 对应qsp的WindowType.ACTIONS
 * @author cxy
 */
public class ActionTemplate {

    private Template actionTemplate;

    public ActionTemplate(VelocityEngine ve) {

        actionTemplate = ve.getTemplate("baijiacms/html/core/action.vm", "utf-8");
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

    public InputStream getAction(GameShower mGameShower) {
        StringBuffer response = new StringBuffer();
        QspGameStatus.actionschanged = false;
        if (HtmlEngine.isOpenSaveWindow) {
            return Utils.BlankInputStream();
        }
        ArrayList<QspListItem> list = mGameShower.refreshActions();
        response.append(getActionListHtml(list));
        return Utils.StringToInputStream(response.toString());
    }

    /**
     * 生成动作按钮
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
     * @param mGameShower
     * @param command
     * @return
     */
    public InputStream execAction(GameShower mGameShower, String command) {
        // Looper.prepare();
        mGameShower.onItemClick(Integer.parseInt(command));
        QspGameStatus.actionschanged = true;
        // Looper.loop();
        return Utils.BlankInputStream();
    }
}
