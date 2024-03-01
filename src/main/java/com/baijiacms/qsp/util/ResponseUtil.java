package com.baijiacms.qsp.util;

import com.qsp.player.libqsp.common.QspConstants;;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author baijiacms
 * @version V1.0.0
 */
public final class ResponseUtil {


    public static void stringWriteToResponse(HttpServletResponse response, String content) {
        try {
            response.getOutputStream().write(content.getBytes(QspConstants.CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setContentType(HttpServletResponse response, String contentType) {
        response.setContentType(contentType);
//        response.setHeader("Content-Type", contentType);
    }
}
