package com.qsp.player.util;


import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author baijiacms
 */
public class HtmlProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HtmlProcessor.class);
    private final Pattern execPattern = Pattern.compile("href=\"exec:([\\s\\S]*?)\"", Pattern.CASE_INSENSITIVE);


    public HtmlProcessor() {
    }


    public String convertQspHtmlToWebViewHtml(String gameResourcePath, String html, boolean isMainDesc) {
        if (StringUtils.isEmpty(html)) {
            return "";
        }

        String result = unescapeQuotes(html);
        result = encodeExec(result, isMainDesc);
        result = htmlizeLineBreaks(result);

//        if(QspConstants.IS_SOB_GAME)
//        {
//            return  result;
//        }
        Document document = Jsoup.parse(result);
        document.outputSettings().prettyPrint(false);
        Element body = document.body();
        processUpateHtmlVideosUrl(body);
        processHtmlImages(gameResourcePath, body);
        processHtmlVideos(body);

        return document.toString();
    }

    private String unescapeQuotes(String str) {
        return str.replace("\\\"", "'");
    }


    private String encodeExec(String html, boolean isMainDesc) {
        Matcher matcher = execPattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String exec = normalizePathsInExec(matcher.group(1));
            String encodedExec = Base64Util.encodeBase64(exec);
            if (isMainDesc) {
                matcher.appendReplacement(sb, "onclick=\"htmlExecTo('exec:" + encodedExec + "')\" href=\"javascript:;\"");
            } else {
                matcher.appendReplacement(sb, "onclick=\"userExecTo('exec:" + encodedExec + "')\" href=\"javascript:;\"");

            }
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
//    原始代码
//    private String encodeExec(String html) {
//        Matcher matcher = execPattern.matcher(html);
//        StringBuffer sb = new StringBuffer();
//        while (matcher.find()) {
//            String exec = normalizePathsInExec(matcher.group(1));
//            String encodedExec = encodeBase64(exec, Base64.NO_WRAP);
//            matcher.appendReplacement(sb, "href=\"exec:" + encodedExec + "\"");
//        }
//        matcher.appendTail(sb);
//
//        return sb.toString();
//    }

    private String normalizePathsInExec(String exec) {
        return exec.replace("\\", "/");
    }

    private String htmlizeLineBreaks(String s) {
        return s.replace("\n", "<br>")
                .replace("\r", "");
    }

    private void processUpateHtmlVideosUrl(Element documentBody) {
        Elements elements = documentBody.select("video");
        for (Element element : elements) {
            String sourceUrl = null;
            if (element.children() != null) {
                Elements source = element.children().select("source");
                if (source != null && source.size() >= 1) {
                    sourceUrl = source.get(0).attr("src");
                }
            }
            String src = element.attr("src");

            if (StringUtils.isEmpty(src)) {
                if (sourceUrl != null) {
                    src = sourceUrl;
                }
            }
            if (StringUtils.isEmpty(src) == false) {
                if (src.startsWith("/") == false) {
                    element.attr("src", "/" + src);
                }
            }
        }

    }

    private void processHtmlImages(String gameResourcePath, Element documentBody) {
        for (Element img : documentBody.select("img")) {
            String src = img.attr("src");

            if (StringUtils.isEmpty(src) == false) {
                if (src.startsWith("/") == false) {
                    img.attr("src", "/" + src);
                }
            }
            boolean resize = shouldImageBeResized(gameResourcePath, img.attr("src"));
            if (resize) {
                img.attr("style", "max-width:100%;");
            }
        }
    }

    private boolean shouldImageBeResized(String gameResourcePath, String relPath) {
        if (relPath.startsWith("/") == false) {
            relPath = "/" + relPath;
        }
        File imagFile = QspUri.getFile(gameResourcePath, relPath);
        if (imagFile == null || imagFile.exists() == false) {
            return false;
        }
        try {
            BufferedImage bufferedImage = ImageIO.read(imagFile);
            if (bufferedImage == null) {
                logger.warn(imagFile.getPath() + " image is null");
                return false;
            }
            return bufferedImage.getWidth() > 400;
        } catch (IOException e) {
            return false;
        }
    }

    private void processHtmlVideos(Element documentBody) {
        documentBody.select("video")
                .attr("style", "max-width:100%;")
                .attr("muted", "true");
    }


    public String convertQspStringToWebViewHtml(String str) {
        return StringUtils.isNotEmpty(str) ? htmlizeLineBreaks(str) : "";
    }


    public String removeHtmlTags(String html) {
        if (StringUtils.isEmpty(html)) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        int len = html.length();
        int fromIdx = 0;
        while (fromIdx < len) {
            int idx = html.indexOf('<', fromIdx);
            if (idx == -1) {
                result.append(html.substring(fromIdx));
                break;
            }
            result.append(html, fromIdx, idx);
            int endIdx = html.indexOf('>', idx + 1);
            if (endIdx == -1) {
                logger.warn("Invalid HTML: element at {} is not closed", idx);
                result.append(html.substring(idx));
                break;
            }
            fromIdx = endIdx + 1;
        }

        return result.toString();
    }
}
