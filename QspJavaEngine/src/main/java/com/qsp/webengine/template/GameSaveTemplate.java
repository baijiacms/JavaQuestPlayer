package com.qsp.webengine.template;

import com.qsp.webengine.HtmlEngine;
import com.qsp.webengine.util.Utils;
import com.qsp.webengine.vo.SaveGameVo;
import com.qsp.player.core.QspConstants;
import com.qsp.player.core.game.GameShower;
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
 * @author cxy
 */
public class GameSaveTemplate {
    private Template htmlTemplate;

    public GameSaveTemplate(VelocityEngine ve) {

        htmlTemplate = ve.getTemplate("baijiacms/html/center/gamesave.vm", "utf-8");
    }

    public String getHtml() {
        List<SaveGameVo> gsaveList = new ArrayList<>();
        dirFolder(gsaveList, new File(QspConstants.getSaveFolder()));
        Collections.sort(gsaveList);
        VelocityContext context = new VelocityContext();

        context.put("gsaveList", gsaveList);
        String randomId = (UUID.randomUUID().toString()).toUpperCase();
        context.put("randomId", randomId);
        StringWriter writer = new StringWriter();
        htmlTemplate.merge(context, writer);
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

    public InputStream deleteGameSave(GameShower mGameShower, String fileName) {
        mGameShower.deleteSaveGame(fileName);

        return Utils.StringToInputStream("1");
    }

    public InputStream saveGame(GameShower mGameShower, String fileName) {
        if (QspConstants.isStart == false) {
            return Utils.BlankInputStream();
        }
        mGameShower.saveGame(fileName);

        return Utils.StringToInputStream("1");
    }

    public InputStream loadSaveGame(GameShower mGameShower, String fileName) {
        if (QspConstants.isStart == false) {
            return Utils.BlankInputStream();
        }
        String resp = mGameShower.loadSaveGame(fileName);
        HtmlEngine.isOpenSaveWindow = false;
        return Utils.StringToInputStream(resp);
    }
}
