package com.qsp.view.util;

import com.qsp.player.common.QspConstants;
import com.qsp.view.http.dto.QspHttpResponse;

import java.io.IOException;

/**
 * @author baijiacms
 * @version V1.0.0
 */
public final class ResponseUtil {


    public static void stringWriteToResponse(QspHttpResponse response, String content) {
        try {
            response.getOutputStream().write(content.getBytes(QspConstants.CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setContentType(QspHttpResponse response, String contentType) {
        response.setContentType(contentType);
//        response.setHeader("Content-Type", contentType);
    }
}
