package com.qsp.view.http.dto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class QspHttpResponseImpl implements QspHttpResponse {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private String contentType = null;

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void setContentType(String type) {
        contentType = type;
    }

    @Override
    public long getTotalBytes() {
        return outputStream.size();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
