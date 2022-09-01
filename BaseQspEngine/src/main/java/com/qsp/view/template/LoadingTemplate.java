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
 * 载入等待窗口
 *
 * @author cxy
 */
public class LoadingTemplate {

    private Template indexTemplate;

    public LoadingTemplate(VelocityEngine ve) {

        indexTemplate = ve.getTemplate("baijiacms/html/center/loading.vm", QspConstants.CHARSET_STR);
    }

    public boolean handle(LibEngine libEngine, String actionScript, String target, HttpServletResponse response) throws Exception {
        String htmlCode = null;
        switch (target) {
            case UrlContants.LOADING_URL_ROOT + "loadingPage":
                htmlCode = getHtml();
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
        }
        return false;
    }

    private String getHtml() {
        VelocityContext context = new VelocityContext();

        context.put("engineTitle", QspConstants.ENGINE_TITLE);
        context.put("engineVersion", QspConstants.ENGINE_VERSION);


        StringWriter writer = new StringWriter();
        indexTemplate.merge(context, writer);
        writer.flush();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

}
