package com.qsp.webengine.util;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author cxy
 */
public class Utils {
    private static InputStream blankInputStream = null;

    public static InputStream blankInputStream() {
        if (Utils.blankInputStream == null) {
            Utils.blankInputStream = new ByteArrayInputStream(
                    "".getBytes());
        }
        return Utils.blankInputStream;
    }

    public static InputStream stringToInputStream(String string) {
        if (StringUtils.isEmpty(string)) {
            return blankInputStream();
        } else {
            InputStream inputStreamRoute = new ByteArrayInputStream(
                    string.getBytes());
            return inputStreamRoute;
        }
    }

    public static void copyStream(InputStream ips, OutputStream ops) throws Exception {
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
