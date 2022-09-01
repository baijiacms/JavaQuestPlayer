package com.qsp.player.util;

import com.qsp.player.common.QspConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

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
        path = path.replace("target/classes/", "");
        return getFolderPath(path);
    }

    public static String getFolderPath(String path) {
        path = path.replaceAll("\\\\", "/");
        if (path.endsWith("/")) {
            return path;
        } else {
            return path + "/";

        }
    }

    public static File getOrCreateDirectory(File parentDir, String name) {
        File dir = findFileOrDirectory(parentDir, name);
        if (dir == null) {
            dir = createDirectory(parentDir, name);
        }
        return dir;
    }

    private static File createDirectory(File parentDir, String name) {
        File dir = new File(parentDir, name);
        dir.mkdir();
        return dir;
    }

    public static File findFileOrDirectory(File parentDir, final String name) {
        File[] files = parentDir.listFiles((dir, filename) -> filename.equalsIgnoreCase(name));
        if (files == null || files.length == 0) {
            return null;
        }

        return files[0];
    }

    public static byte[] getFileContents(String path) {
        File file = new File(path);
        try (FileInputStream in = new FileInputStream(file)) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                StreamUtils.copy(in, out);
                return out.toByteArray();
            }
        } catch (IOException ex) {
            logger.error("Error reading file: " + path, ex);
            return null;
        }
    }
}
