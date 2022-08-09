package com.qsp.webengine.handler;


import com.qsp.webengine.HtmlEngine;
import com.qsp.webengine.vo.ResponseVo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author cxy
 */
public class MyQspURLConnection extends URLConnection {
    private HtmlEngine htmlEngine;
    private URL url;
    private InputStream input = null;

    protected MyQspURLConnection(URL url, HtmlEngine htmlEngine) {
        super(url);
        this.url = url;
        this.htmlEngine = htmlEngine;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (input == null) {
            String path = url.getPath();
            if ("file".equals(url.getProtocol())) {
                path = url.getAuthority() + path;
            }
            ResponseVo responseVo = htmlEngine.getInputStream(url, path);
            input = responseVo.getResponseStream();
        }
        return input;
    }

    @Override
    public void connect() throws IOException {
    }
}
