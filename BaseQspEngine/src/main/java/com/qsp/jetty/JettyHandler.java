package com.qsp.jetty;

import com.qsp.QspEngineCore;
import com.qsp.player.common.QspConstants;
import com.qsp.webengine.vo.ResponseVo;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class JettyHandler extends MyFileHandler {
    private QspEngineCore qspEngineCore;
    public JettyHandler() {
        super();
        qspEngineCore = new QspEngineCore(QspConstants.DEFAULT_USER);

    }
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String urlPath = request.getRequestURL().toString();
        if (StringUtils.isEmpty(request.getParameter("actionScript")) == false) {
            urlPath = urlPath + "?actionScript=" + request.getParameter("actionScript");
        }
        ResponseVo responseVo = qspEngineCore.getInputStream(new URL(urlPath), target);
        InputStream inputStream = responseVo.getResponseStream();
        if (StringUtils.isEmpty(responseVo.getContentType()) == false) {
            response.setContentType(responseVo.getContentType());
            response.setHeader("Content-Type", responseVo.getContentType());
        }
        try {
            copyStream(inputStream, response.getOutputStream());
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.handle(target, baseRequest, request, response);
    }

    private void copyStream(InputStream ips, OutputStream ops) throws Exception {
        byte[] buf = new byte[1024];
        int len = ips.read(buf);
        while (len != -1) {
            ops.write(buf, 0, len);
            len = ips.read(buf);
        }
        ops.flush();
        ips.close();
    }
}

