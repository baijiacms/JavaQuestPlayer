package com.qsp.view.http.dto;

import java.io.InputStream;
import java.io.OutputStream;

public interface QspHttpResponse {

    public OutputStream getOutputStream();

    public void setContentType(String type);

    public long getTotalBytes();

    public String getContentType();

    public InputStream getInputStream();
}
