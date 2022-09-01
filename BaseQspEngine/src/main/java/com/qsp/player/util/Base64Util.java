package com.qsp.player.util;


import com.qsp.player.common.QspConstants;

import java.io.UnsupportedEncodingException;


public final class Base64Util {

    public static String encodeBase64(String str) {
        return java.util.Base64.getEncoder().encodeToString(str.getBytes());
    }

    public static String decodeBase64(String base64) {
        try {
            return new String(java.util.Base64.getDecoder().decode(base64), QspConstants.CHARSET_STR);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
