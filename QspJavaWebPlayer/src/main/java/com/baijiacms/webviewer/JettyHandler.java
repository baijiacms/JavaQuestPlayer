package com.baijiacms.webviewer;

import com.qsp.javafx.CommonViewImpl;
import com.qsp.javafx.JavaFxMediaPlayer;
import com.qsp.webengine.HtmlEngine;
import com.qsp.webengine.vo.ResponseVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;

public class JettyHandler extends MyFileHandler {
    private HtmlEngine htmlEngine;
    private String baseHttpPath;
    public JettyHandler(String baseHttpPath)
    {
        super();
        this.baseHttpPath=baseHttpPath;
        VelocityEngine ve=new VelocityEngine();
        ve.setProperty(Velocity.RESOURCE_LOADER,Velocity.RESOURCE_LOADER_CLASS);
        ve.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init();
        htmlEngine=new HtmlEngine(new CommonViewImpl(), new JavaFxMediaPlayer());
        htmlEngine.start();

    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String urlPath=request.getRequestURL().toString();
        if(StringUtils.isEmpty(request.getParameter("actionScript"))==false)
        {
            urlPath=urlPath+"?actionScript="+request.getParameter("actionScript");
        }
        ResponseVo responseVo =  htmlEngine.getInputStream(new URL( urlPath),target);
        InputStream inputStream=responseVo.getResponseStream();
        if(StringUtils.isEmpty(responseVo.getContentType())==false) {
            response.setContentType(responseVo.getContentType());
            response.setHeader("Content-Type",responseVo.getContentType());
        }
        try {
            copyStream(inputStream,response.getOutputStream());
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.handle( target,  baseRequest,  request,  response) ;
    }
    private    void  copyStream(InputStream ips,OutputStream ops)  throws  Exception {
        byte [] buf =  new  byte [ 1024 ];
        int  len = ips.read(buf);
        while (len != - 1 ) {
            ops.write(buf, 0 ,len);
            len = ips.read(buf);
        }
        ops.flush();
        ips.close();
    }
}

