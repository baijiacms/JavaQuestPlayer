package com.qsp.player.util;

import java.io.*;

public final class StreamUtil {
    private static final int BUFFER_SIZE = 8192;

    public static InputStream openInputStream(Uri uri) {
        try {
            return new FileInputStream(uri.getmFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("null");
        }
    }

    public static OutputStream openOutputStream(Uri uri, String action) {

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
}
