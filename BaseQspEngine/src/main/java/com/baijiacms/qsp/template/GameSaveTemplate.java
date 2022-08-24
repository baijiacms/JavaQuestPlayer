package com.baijiacms.qsp.template;

import com.baijiacms.qsp.common.QspConstants;
import com.baijiacms.qsp.dto.GameStatus;
import com.baijiacms.qsp.player.PlayerEngine;
import com.baijiacms.qsp.util.StreamUtils;
import com.baijiacms.qsp.vo.SaveGameVo;
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

    public String getHtml(GameStatus gameStatus) {
        List<SaveGameVo> gsaveList = new ArrayList<>();
        dirFolder(gsaveList, new File(gameStatus.getSaveFolder()));
        Collections.sort(gsaveList);
        VelocityContext context = new VelocityContext();

        context.put("gsaveList", gsaveList);
        String randomId = (UUID.randomUUID().toString()).toUpperCase();
        context.put("randomId", randomId);
        StringWriter writer = new StringWriter();

        if (gameStatus.isBigKuyash) {
            htmlTemplateKuyash.merge(context, writer);
        } else {
            if (gameStatus.isSobGame) {
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

    public String deleteGameSave(PlayerEngine mPlayerEngine, String fileName) {
        mPlayerEngine.deleteSaveGame(fileName);

        return StreamUtils.SUCCESS_STR;
    }

    public String saveGame(PlayerEngine mPlayerEngine, String fileName) {
        if (mPlayerEngine.getGameStatus().isStart == false) {
            return StreamUtils.BLANK_STR;
        }
        mPlayerEngine.saveGame(fileName);

        return StreamUtils.SUCCESS_STR;
    }

    public String loadSaveGame(PlayerEngine mPlayerEngine, String fileName) {
        if (mPlayerEngine.getGameStatus().isStart == false) {
            return StreamUtils.BLANK_STR;
        }
        String resp = mPlayerEngine.loadSaveGame(fileName);
        mPlayerEngine.getGameStatus().isOpenSaveWindow = false;
        return resp;
    }
}
