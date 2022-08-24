package com.baijiacms.qsp.template;

import com.baijiacms.qsp.common.QspConstants;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

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

    public String getHtml() {
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
