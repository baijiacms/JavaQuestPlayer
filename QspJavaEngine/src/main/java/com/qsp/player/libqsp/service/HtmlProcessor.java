package com.qsp.player.libqsp.service;


import com.qsp.player.PlayerEngine;
import com.qsp.player.util.Base64Util;
import com.qsp.player.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlProcessor {
    private static final int IMAGE_WIDTH_THRESHOLD = 400;

    private static final Logger logger = LoggerFactory.getLogger(HtmlProcessor.class);
    private static final Pattern execPattern = Pattern.compile("href=\"exec:([\\s\\S]*?)\"", Pattern.CASE_INSENSITIVE);

    private final GameContentResolver gameContentResolver;

    public HtmlProcessor(GameContentResolver gameContentResolver) {
        this.gameContentResolver = gameContentResolver;
    }

    /**
     * Привести HTML-код <code>html</code>, полученный из библиотеки к
     * HTML-коду, приемлемому для отображения в {}.
     */
    public String convertQspHtmlToWebViewHtml(PlayerEngine playerEngine, String html, boolean isMainDesc) {
        if (StringUtil.isNullOrEmpty(html)) {
            return "";
        }

        String result = unescapeQuotes(html);
        result = encodeExec(result,isMainDesc);
        result = htmlizeLineBreaks(result);

//        if(QspConstants.IS_SOB_GAME)
//        {
//            return  result;
//        }
        Document document = Jsoup.parse(result);
        document.outputSettings().prettyPrint(false);
        Element body = document.body();
        processUpateHtmlVideosUrl(body);
        processHtmlImages(playerEngine,body);
        processHtmlVideos(body);

        return document.toString();
    }

    private String unescapeQuotes(String str) {
        return str.replace("\\\"", "'");
    }


    private String encodeExec(String html,boolean isMainDesc) {
        Matcher matcher = execPattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String exec = normalizePathsInExec(matcher.group(1));
            String encodedExec = Base64Util.encodeBase64(exec);
            if(isMainDesc) {
                matcher.appendReplacement(sb, "onclick=\"htmlExecTo('exec:" + encodedExec + "')\" href=\"javascript:;\"");
            }else
            {
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
        Elements elements= documentBody.select("video");
        for(Element element:elements)
        {
            String sourceUrl=null;
            if(   element.children()!=null) {
                Elements source = element.children().select("source");
                if(source!=null&&source.size()>=1)
                {
                    sourceUrl=source.get(0).attr("src");
                }
            }
            String src= element.attr("src");

            if(StringUtils.isEmpty(src)) {
                if(sourceUrl!=null)
                {
                    src=sourceUrl;
                }
            }
            if(StringUtils.isEmpty(src)==false) {
                if (src.startsWith("/") == false) {
                    element.attr("src", "/" + src);
                }
            }
        }

    }
    private void processHtmlImages(PlayerEngine playerEngine, Element documentBody) {
        for (Element img : documentBody.select("img")) {
           String src= img.attr("src");

            if(StringUtils.isEmpty(src)==false) {
                if (src.startsWith("/") == false) {
                    img.attr("src", "/" + src);
                }
            }
            boolean resize = shouldImageBeResized(playerEngine,img.attr("src"));
            if (resize) {
                img.attr("style", "max-width:100%;");
            }
        }
    }

    private boolean shouldImageBeResized(PlayerEngine playerEngine, String relPath) {
        if(relPath.startsWith("/")==false)
        {
            relPath="/"+relPath;
        }
        File imagFile=new File(playerEngine.getGameStatus().GAME_RESOURCE_PATH +relPath);
        if(imagFile.exists()==false)
        {
            return false;
        }
        try {
           return ImageIO.read(imagFile).getWidth()>400;
        } catch (IOException e) {
            return false;
        }
    }
    private void processHtmlVideos(Element documentBody) {
        documentBody.select("video")
                .attr("style", "max-width:100%;")
                .attr("muted", "true");
    }

    /**
     * Привести строку <code>str</code>, полученную из библиотеки, к HTML-коду,
     * приемлемому для отображения в {}.
     */
    public String convertQspStringToWebViewHtml(String str) {
        return StringUtil.isNotEmpty(str) ? htmlizeLineBreaks(str) : "";
    }

    /**
     * Удалить HTML-теги из строки <code>html</code> и вернуть результирующую строку.
     */
    public String removeHtmlTags(String html) {
        if (StringUtil.isNullOrEmpty(html)) {
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
