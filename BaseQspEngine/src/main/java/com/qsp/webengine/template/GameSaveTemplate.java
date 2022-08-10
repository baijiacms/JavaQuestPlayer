package com.qsp.webengine.template;

import com.qsp.player.PlayerEngine;
import com.qsp.webengine.util.Utils;
import com.qsp.webengine.vo.SaveGameVo;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    private PlayerEngine mPlayerEngine;

    public GameSaveTemplate(PlayerEngine mPlayerEngine, VelocityEngine ve) {
        this.mPlayerEngine = mPlayerEngine;

        htmlTemplate = ve.getTemplate("baijiacms/html/center/gamesave.vm", "utf-8");
        htmlTemplateKuyash = ve.getTemplate("baijiacms/html/center/gamesaveKuyash.vm", "utf-8");
    }

    public String getHtml() {
        List<SaveGameVo> gsaveList = new ArrayList<>();
        dirFolder(gsaveList, new File(mPlayerEngine.getGameStatus().getSaveFolder()));
        Collections.sort(gsaveList);
        VelocityContext context = new VelocityContext();

        context.put("gsaveList", gsaveList);
        String randomId = (UUID.randomUUID().toString()).toUpperCase();
        context.put("randomId", randomId);
        StringWriter writer = new StringWriter();

        if (mPlayerEngine.getGameStatus().isBigKuyash) {
            htmlTemplateKuyash.merge(context, writer);

        } else {
            htmlTemplate.merge(context, writer);
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

    public InputStream deleteGameSave(PlayerEngine mPlayerEngine, String fileName) {
        mPlayerEngine.deleteSaveGame(fileName);

        return Utils.stringToInputStream("1");
    }

    public InputStream saveGame(PlayerEngine mPlayerEngine, String fileName) {
        if (mPlayerEngine.getGameStatus().isStart == false) {
            return Utils.blankInputStream();
        }
        mPlayerEngine.saveGame(fileName);

        return Utils.stringToInputStream("1");
    }

    public InputStream loadSaveGame(PlayerEngine mPlayerEngine, String fileName) {
        if (mPlayerEngine.getGameStatus().isStart == false) {
            return Utils.blankInputStream();
        }
        String resp = mPlayerEngine.loadSaveGame(fileName);
        mPlayerEngine.getGameStatus().isOpenSaveWindow = false;
        return Utils.stringToInputStream(resp);
    }
}
