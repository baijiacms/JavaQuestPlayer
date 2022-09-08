package com.qsp.view.template;

import com.qsp.player.LibEngine;
import com.qsp.player.common.QspConstants;
import com.qsp.player.entity.GameStatus;
import com.qsp.view.common.UrlContants;
import com.qsp.view.http.dto.QspHttpResponse;
import com.qsp.view.template.vo.SaveGameVo;
import com.qsp.view.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 游戏存取
 *
 * @author cxy
 */
public class GameSaveTemplate {
    private Template htmlTemplate;
    private Template htmlTemplateKuyash;
    private Template htmlTemplateSob;


    public GameSaveTemplate(VelocityEngine ve) {
        htmlTemplate = ve.getTemplate("baijiacms/html/center/gamesave.vm", QspConstants.CHARSET_STR);
        htmlTemplateKuyash = ve.getTemplate("baijiacms/html/center/gamesaveKuyash.vm", QspConstants.CHARSET_STR);
        htmlTemplateSob = ve.getTemplate("baijiacms/html/center/gamesaveSob.vm", QspConstants.CHARSET_STR);

    }

    public boolean handle(LibEngine libEngine, String target, QspHttpResponse response, String actionScript) throws Exception {
        GameStatus gameStatus = libEngine.getGameStatus();
        String htmlCode = null;
        switch (target) {
            case UrlContants.GAME_SAVE_URL_ROOT + "openSaveWindow":
                libEngine.getGameStatus().setOpenSaveWindow(true);
                gameStatus.refreshAll();
                ResponseUtil.stringWriteToResponse(response, QspConstants.SUCCESS_STR);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.GAME_SAVE_URL_ROOT + "closeSaveWindow":
                libEngine.getGameStatus().setOpenSaveWindow(false);
                gameStatus.refreshAll();
                ResponseUtil.stringWriteToResponse(response, QspConstants.SUCCESS_STR);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.GAME_SAVE_URL_ROOT + "deleteGameSave":
                if (StringUtils.isEmpty(actionScript)) {
                    ResponseUtil.stringWriteToResponse(response, QspConstants.BLANK_STR);
                    ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                    return true;
                }
                htmlCode = deleteGameSave(libEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.GAME_SAVE_URL_ROOT + "GameSave":
                if (StringUtils.isEmpty(actionScript)) {
                    ResponseUtil.stringWriteToResponse(response, QspConstants.BLANK_STR);
                    ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                    return true;
                }

                htmlCode = saveGame(libEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.GAME_SAVE_URL_ROOT + "LoadGameSave":
                if (StringUtils.isEmpty(actionScript)) {
                    ResponseUtil.stringWriteToResponse(response, QspConstants.BLANK_STR);
                    ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                    return true;
                }
                htmlCode = loadSaveGame(libEngine, actionScript);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.GAME_SAVE_URL_ROOT + "QuickSave":
                htmlCode = saveGame(libEngine, null);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            case UrlContants.GAME_SAVE_URL_ROOT + "loadQuickSave":
                htmlCode = loadSaveGame(libEngine, null);
                ResponseUtil.stringWriteToResponse(response, htmlCode);
                ResponseUtil.setContentType(response, QspConstants.HTML_CONTENT_TYPE);
                return true;
            default:
                ;
        }
        return false;
    }

    public String getHtml(LibEngine libEngine) {
        List<SaveGameVo> gsaveList = new ArrayList<>();
        File file = new File(libEngine.getQspGame().getGameSaveFolder());
        if (file.exists() == false) {
            file.mkdir();
        }
        dirFolder(gsaveList, file);
        Collections.sort(gsaveList);
        VelocityContext context = new VelocityContext();

        context.put("gsaveList", gsaveList);
        String randomId = (UUID.randomUUID().toString()).toUpperCase();
        context.put("randomId", randomId);
        StringWriter writer = new StringWriter();

        if (libEngine.getQspGame().isBigKuyash()) {
            htmlTemplateKuyash.merge(context, writer);
        } else {
            if (libEngine.getQspGame().isSob()) {
                htmlTemplateSob.merge(context, writer);
            } else {
                htmlTemplate.merge(context, writer);
            }
        }
        writer.flush();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    private void dirFolder(List<SaveGameVo> list, File file) {
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
            } else {
                if (f.isFile() && f.getPath().endsWith(".sav")) {
                    SaveGameVo saveGameVo = new SaveGameVo();
                    saveGameVo.setFileName(f.getName().substring(0, f.getName().length() - 4));
                    saveGameVo.setFileTime(f.lastModified());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    saveGameVo.setFileTimeStr(sdf.format(new Date(f.lastModified())));
                    list.add(saveGameVo);
                }
            }
        }
    }

    public String deleteGameSave(LibEngine libEngine, String fileName) {
        libEngine.deleteSaveGame(fileName);

        return QspConstants.SUCCESS_STR;
    }

    public String saveGame(LibEngine libEngine, String fileName) {
        if (libEngine.getGameStatus().isGameRunning() == false) {
            return QspConstants.BLANK_STR;
        }
        libEngine.saveGame(fileName);

        return QspConstants.SUCCESS_STR;
    }

    public String loadSaveGame(LibEngine libEngine, String fileName) {
        if (libEngine.getGameStatus().isGameRunning() == false) {
            return QspConstants.BLANK_STR;
        }
        String resp = libEngine.loadSaveGame(fileName);
        libEngine.getGameStatus().setOpenSaveWindow(false);
        return resp;
    }
}
