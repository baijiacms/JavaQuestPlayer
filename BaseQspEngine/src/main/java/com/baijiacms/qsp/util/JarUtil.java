package com.baijiacms.qsp.util;

import com.baijiacms.qsp.common.QspConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class JarUtil {

    public static String getJarPath(Class classes) {
        String path = classes.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            path = (URLDecoder.decode(path, QspConstants.CHARSET_STR));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (System.getProperty("os.name").contains("dows")) {
            path = path.substring(1, path.length());
        }
        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
            return path.substring(0, path.lastIndexOf("/"));
        }
        return path.replace("target/classes/", "");
    }
}
