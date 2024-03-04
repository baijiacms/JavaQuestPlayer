package com.baijiacms.qsp.controller;

import com.baijiacms.qsp.vo.StatusVo;
import com.qsp.player.libqsp.LibQspProxyImpl;
import com.qsp.player.libqsp.queue.QspAction;
import com.qsp.player.libqsp.queue.QspCore;
import com.qsp.player.libqsp.queue.QspTask;
import com.qsp.player.libqsp.entity.QspListItem;
import com.qsp.player.libqsp.queue.QspThread;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * @author：ChenXingYu
 * @date 2023/8/4 11:08
 */
@RestController
@RequestMapping("/qspGame")
public class QspGameController {
    @GetMapping("/user/userCall")
    public String userCall(@RequestParam(value = "actionScript") String actionScript) {
        QspTask aspTask=new QspTask();
        aspTask.action= QspAction.execute.getAction();
        aspTask.paramString=actionScript;
        QspThread.addMessage(aspTask);
        QspCore.varsdescchanged.set(true);
//        QspThread.libQspProxy.shouldOverrideUrlLoading(actionScript);
        return "";
    }

    @GetMapping("/user/user")
    public String userHtml() {
        QspCore.varsdescchanged.set(false);
        String html = LibQspProxyImpl.refreshVarsDesc();
        return html;
    }

    @GetMapping("/html/htmlCall")
    public String htmlCall(@RequestParam(value = "actionScript") String actionScript) {
        QspTask aspTask=new QspTask();
        aspTask.action= QspAction.execute.getAction();
        aspTask.paramString=actionScript;
        QspThread.addMessage(aspTask);
//        QspThread.libQspProxy.shouldOverrideUrlLoading(actionScript);
        QspCore.refreshAll();
        return "";
    }

    @GetMapping("/html/html")
    public String htmlHtml() {
        String html = null;
         html = LibQspProxyImpl.refreshMainDesc();

        return html;
    }

    @GetMapping("/console/consoleCall")
    public String consoleCall(@RequestParam(value = "actionScript") String actionScript) {
        // Looper.prepare();
        QspTask aspTask1=new QspTask();
        aspTask1.action= QspAction.setSelObject.getAction();
        aspTask1.paramInt=Integer.parseInt(actionScript);
        QspThread.addMessage(aspTask1);


        // Looper.loop();
        QspCore.objectschanged.set(true);
        return "";
    }

    @GetMapping("/console/console")
    public String consoleHtml() {
        StringBuffer response = new StringBuffer();
        QspCore.objectschanged.set(false);
        ArrayList<QspListItem> list = LibQspProxyImpl.refreshObjects();
        response.append(getConsoleActionHtml(list));
        return response.toString();
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
    @GetMapping("/action/actionCall")
    public String actionCall(@RequestParam(value = "actionScript") String actionScript) {
        QspTask aspTask1=new QspTask();
        aspTask1.action= QspAction.onActionClicked.getAction();
        aspTask1.paramInt=Integer.parseInt(actionScript);
        QspThread.addMessage(aspTask1);
//        Engine.INSTANCEOF.getLibEngine().onItemClick(Integer.parseInt(actionScript));

        QspCore.maindescchanged.set(true);
//        Engine.INSTANCEOF.getLibEngine().getGameStatus().setActionschanged(true);

        return "";
    }
    @GetMapping("/action/action")
    public String actionHtml() {
        StringBuffer response = new StringBuffer();
        QspCore.actionschanged.set(false);
        ArrayList<QspListItem> list = LibQspProxyImpl.refreshActions();
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

}
