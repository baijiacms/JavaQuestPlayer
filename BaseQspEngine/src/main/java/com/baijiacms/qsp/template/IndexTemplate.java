package com.baijiacms.qsp.template;

import com.baijiacms.qsp.common.QspConstants;
import com.baijiacms.qsp.dto.GameStatus;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.StringWriter;

/**
 * 游戏主窗口，iframe关联其他窗口
 *
 * @author cxy
 */
public class IndexTemplate {
    private Template indexTemplate;
    private Template indexSobTemplate;
    private Template indexBigKuyashTemplate;

    public IndexTemplate(VelocityEngine ve) {
        indexTemplate = ve.getTemplate("baijiacms/html/core/index.vm", QspConstants.CHARSET_STR);
        indexSobTemplate = ve.getTemplate("baijiacms/html/diy/sobIndex.vm", QspConstants.CHARSET_STR);
        indexBigKuyashTemplate = ve.getTemplate("baijiacms/html/diy/bigKuyashIndex.vm", QspConstants.CHARSET_STR);
    }

    public String getIndexHtml(GameStatus gameStatus) {
        VelocityContext context = new VelocityContext();

//        context.put("screen_width", screen_width);
//        context.put("screen_height", screen_height);
//        context.put("user_width", user_width);
//        context.put("user_height", user_height);
//        context.put("action_width", action_width);
//        context.put("action_height", action_height);
//        context.put("root_width", root_width);
//        context.put("root_height", root_height);
        context.put("gameTitle", gameStatus.gameTitle);
        context.put("gameVersion", gameStatus.gameVersion);
        context.put("enginePowerBy", QspConstants.ENGINE_POWER_BY);
        context.put("engineTitle", QspConstants.ENGINE_TITLE);
        context.put("engineVersion", QspConstants.ENGINE_VERSION);


        StringWriter writer = new StringWriter();
        if (gameStatus.isSobGame) {
            indexSobTemplate.merge(context, writer);
        } else {
            if (gameStatus.isBigKuyash) {
                indexBigKuyashTemplate.merge(context, writer);
            } else {
                indexTemplate.merge(context, writer);
            }
        }
        writer.flush();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }
}
