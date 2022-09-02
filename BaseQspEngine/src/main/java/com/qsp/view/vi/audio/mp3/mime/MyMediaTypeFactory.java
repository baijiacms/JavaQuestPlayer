package com.qsp.view.vi.audio.mp3.mime;


import com.qsp.player.common.QspConstants;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MyMediaTypeFactory {
    private static final Map<String, String> FILE_EXTENSION_TO_MEDIA_TYPES = parseMimeTypes();
    private static final String MEDIA_CHARSET = ";charset=" + QspConstants.CHARSET_STR;

    public static String getContentType(String fileName) {
        return FILE_EXTENSION_TO_MEDIA_TYPES.get(getFilenameExtension(fileName)) + MEDIA_CHARSET;
    }

    public static Map<String, String> getMediaType() {
        return FILE_EXTENSION_TO_MEDIA_TYPES;
    }

    private static Map<String, String> parseMimeTypes() {
        Map<String, String> result = new HashMap<>();

        InputStream is = null;

        try {
            is = MyMediaTypeFactory.class.getResourceAsStream("/baijiacms/mime.types");

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.US_ASCII));

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }

                String[] tokens = tokenizeToStringArray(line, " \t\n\r\f");

                int i = 1;

                while (i < tokens.length) {
                    String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
                    result.put(fileExtension, tokens[0]);
                    ++i;
                }
            }

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception var16) {
                    var16.printStackTrace();
                }
            }

        }

        return result;
    }


    private static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    private static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (str == null) {
            return new String[0];
        } else {
            StringTokenizer st = new StringTokenizer(str, delimiters);
            ArrayList tokens = new ArrayList();

            while (true) {
                String token;
                do {
                    if (!st.hasMoreTokens()) {
                        return toStringArray((Collection) tokens);
                    }

                    token = st.nextToken();
                    if (trimTokens) {
                        token = token.trim();
                    }
                } while (ignoreEmptyTokens && token.length() <= 0);

                tokens.add(token);
            }
        }
    }

    private static String[] toStringArray(Collection<String> collection) {
        return (String[]) collection.toArray(new String[0]);
    }

    private static String getFilenameExtension(String path) {
        if (path == null) {
            return null;
        } else {
            int extIndex = path.lastIndexOf(46);
            if (extIndex == -1) {
                return null;
            } else {
                int folderIndex = path.lastIndexOf("/");
                return folderIndex > extIndex ? null : path.substring(extIndex + 1);
            }
        }
    }
}
