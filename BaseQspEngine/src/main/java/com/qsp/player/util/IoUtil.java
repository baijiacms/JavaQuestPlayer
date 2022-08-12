package com.qsp.player.util;

import org.apache.commons.lang3.StringUtils;

import java.io.*;

public final class IoUtil {
    private static final int BUFFER_SIZE = 8192;
    public static InputStream blankInputStream = null;

    public static InputStream openInputStream(Uri uri) {
        try {
            return new FileInputStream(uri.getmFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("null");
        }
    }

    public static OutputStream openOutputStream(Uri uri) {

        try {
            return new FileOutputStream(uri.getmFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("null");
        }
    }


    public static void copy(InputStream from, OutputStream to) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = from.read(buffer)) > 0) {
            to.write(buffer, 0, bytesRead);
        }
    }

    public static InputStream blankInputStream() {
        if (blankInputStream == null) {
            blankInputStream = new ByteArrayInputStream(
                    "".getBytes());
        }
        return blankInputStream;
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
}
