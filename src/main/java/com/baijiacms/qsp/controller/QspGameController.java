package com.baijiacms.qsp.controller;

import com.baijiacms.qsp.vo.StatusVo;
import com.qsp.player.Engine;
import com.qsp.player.entity.QspListItem;
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
        Engine.INSTANCEOF.getLibEngine().shouldOverrideUrlLoading(actionScript);


        Engine.INSTANCEOF.getLibEngine().getGameStatus().setVarsdescchanged(true);
        return "";
    }

    @GetMapping("/user/user")
    public String userHtml() {
        Engine.INSTANCEOF.getLibEngine().getGameStatus().setVarsdescchanged(false);
        String html = Engine.INSTANCEOF.getLibEngine().refreshVarsDesc();
        return html;
    }

    @GetMapping("/html/htmlCall")
    public String htmlCall(@RequestParam(value = "actionScript") String actionScript) {
        Engine.INSTANCEOF.getLibEngine().shouldOverrideUrlLoading(actionScript);
        Engine.INSTANCEOF.getLibEngine().getGameStatus().refreshAll();

        return "";
    }

    @GetMapping("/html/html")
    public String htmlHtml() {
        String html = null;
         html = Engine.INSTANCEOF.getLibEngine().refreshMainDesc();

        return html;
    }

    @GetMapping("/console/consoleCall")
    public String consoleCall(@RequestParam(value = "actionScript") String actionScript) {
        // Looper.prepare();
        Engine.INSTANCEOF.getLibEngine().onObjectSelected(Integer.parseInt(actionScript));
        // Looper.loop();

        Engine.INSTANCEOF.getLibEngine().getGameStatus().setObjectschanged(true);
        return "";
    }

    @GetMapping("/console/console")
    public String consoleHtml() {
        StringBuffer response = new StringBuffer();
        Engine.INSTANCEOF.getLibEngine().getGameStatus().setObjectschanged(false);
        ArrayList<QspListItem> list = Engine.INSTANCEOF.getLibEngine().refreshObjects();
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
        Engine.INSTANCEOF.getLibEngine().onItemClick(Integer.parseInt(actionScript));
        Engine.INSTANCEOF.getLibEngine().getGameStatus().setActionschanged(true);

        return "";
    }
    @GetMapping("/action/action")
    public String actionHtml() {
        StringBuffer response = new StringBuffer();
        Engine.INSTANCEOF.getLibEngine().getGameStatus().setActionschanged(false);
        ArrayList<QspListItem> list = Engine.INSTANCEOF.getLibEngine().refreshActions();
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

    @GetMapping("/isNeedRefresh")
    @ResponseBody
    public StatusVo isNeedRefresh() {

        StatusVo statusVo=new StatusVo();
        statusVo.setActionschanged(Engine.INSTANCEOF.getLibEngine().isActionschanged());
        statusVo.setMaindescchanged(Engine.INSTANCEOF.getLibEngine().isMaindescchanged(false));
        statusVo.setObjectschanged(Engine.INSTANCEOF.getLibEngine().isObjectschanged());
        statusVo.setVarsdescchanged(Engine.INSTANCEOF.getLibEngine().isVarsdescchanged());
        return statusVo;
    }
    @GetMapping("/isNeedRefreshHtml")
    public String isNeedRefreshHtml() {
        String result = Engine.INSTANCEOF.getLibEngine().isMaindescchanged(false) ? "1" : "0";
        return result;
    }

    @GetMapping("/isNeedRefreshAction")
    public String isNeedRefreshAction() {
        String result = Engine.INSTANCEOF.getLibEngine().isActionschanged() ? "1" : "0";
        return result;
    }
    @GetMapping("/isNeedRefreshUser")
    public String isNeedRefreshUser() {
        String result = Engine.INSTANCEOF.getLibEngine().isVarsdescchanged() ? "1" : "0";
        return result;
    }

    @GetMapping("/isNeedRefreshConsole")
    public String isNeedRefreshConsole() {
        String result = Engine.INSTANCEOF.getLibEngine().isObjectschanged() ? "1" : "0";
        return result;
    }


}
