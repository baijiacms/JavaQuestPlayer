package com.qsp.webengine.vo;

import lombok.Data;

import java.io.InputStream;

@Data
public class ResponseVo {
    public InputStream responseStream;
    public String contentType = "text/html;charset=utf-8";
}
