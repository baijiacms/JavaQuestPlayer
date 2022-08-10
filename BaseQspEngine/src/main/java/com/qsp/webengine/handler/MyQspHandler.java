package com.qsp.webengine.handler;


import com.qsp.webengine.HtmlEngine;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;

/**
 * @author cxy
 */
public class MyQspHandler extends URLStreamHandler {
    private final ClassLoader classLoader;
    private HtmlEngine htmlEngine;

    public MyQspHandler(HtmlEngine htmlEngine) {
        this.htmlEngine = htmlEngine;
        this.classLoader = getClass().getClassLoader();
    }


    @Override
    protected MyQspURLConnection openConnection(URL u) throws IOException {

//        URL resourceUrl = classLoader.getResource(u.getPath());
//        if(resourceUrl == null)
//            throw new IOException("Resource not found: " + u);
//        System.out.println(u.getPath());

        return new MyQspURLConnection(u, htmlEngine);
    }
}