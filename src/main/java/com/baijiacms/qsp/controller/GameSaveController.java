package com.baijiacms.qsp.controller;

import com.baijiacms.qsp.common.ResponseResult;
import com.baijiacms.qsp.vo.SaveGameVo;
import com.qsp.player.libqsp.common.QspConstants;
import com.qsp.player.libqsp.queue.QspAction;
import com.qsp.player.libqsp.queue.QspCore;
import com.qsp.player.libqsp.queue.QspTask;
import com.qsp.player.libqsp.queue.QspThread;
import com.qsp.player.libqsp.util.FileUtil;
import com.qsp.player.libqsp.util.QspUri;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author：ChenXingYu
 * @date 2023/8/4 11:08
 */
@RestController
@RequestMapping("/gameSave")
public class GameSaveController {
    @GetMapping("/saveList")
    @ResponseBody
    public ResponseResult<List<SaveGameVo>> saveList() {

        List<SaveGameVo> gsaveList = new ArrayList<>();

        dirFolder(gsaveList);
        Collections.sort(gsaveList);
        return ResponseResult.createSuccessResult(gsaveList);
    }
    private void dirFolder(List<SaveGameVo> list) {
        File file = new File(QspCore.currentQspGame.getGameSaveFolder());
        if (file.exists() == false) {
            file.mkdir();
        }
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

    @GetMapping("/deleteSave")
    @ResponseBody
    public ResponseResult<String> deleteGameSave(@RequestParam(value = "actionScript") String actionScript) {
        deleteSaveGame(actionScript);
        return ResponseResult.createSuccessResult("");
    }
    @GetMapping("/gameSave")
    @ResponseBody
    public ResponseResult<String> GameSave(@RequestParam(value = "actionScript") String actionScript) {

        saveGame(actionScript);
        QspCore.refreshAll();
        return ResponseResult.createSuccessResult("");
    }
    @GetMapping("/loadGameSave")
    @ResponseBody
    public ResponseResult<String> LoadGameSave(@RequestParam(value = "actionScript") String actionScript) {

        loadSaveGame(actionScript);
        QspCore.refreshAll();
        return ResponseResult.createSuccessResult("");
    }
    @GetMapping("/loadQuickSave")
    @ResponseBody
    public ResponseResult<String> loadQuickSave() {

      String result=  loadSaveGame(null);
        QspCore.refreshAll();
        return ResponseResult.createSuccessResult(result);

    }
    @GetMapping("/quickSave")
    @ResponseBody
    public ResponseResult<String> QuickSave() {
        saveGame(null);
        QspCore.refreshAll();
        return ResponseResult.createSuccessResult("");
    }


    private static final Logger logger = LoggerFactory.getLogger(GameSaveController.class);
    public String loadSaveGame(String filename) {
        if (StringUtils.isEmpty(filename)) {
            filename = QspConstants.QUICK_SAVE_NAME;
        }
        if (filename.endsWith(".sav") == false) {
            filename = filename + ".sav";
        }
        String saveFolder = FileUtil.getFolderPath(QspCore.currentQspGame.getGameSaveFolder());
        File saveFile = QspUri.getFile(saveFolder, filename);
        if (saveFile.exists() == false) {
            return "0";
        }

        QspTask aspTask=new QspTask();
        aspTask.action= QspAction.loadGame.getAction();
        aspTask.qspUri=QspUri.toUri(saveFile);
        QspThread.addMessage(aspTask);

        QspCore.refreshAll();
        return "1";
    }

    public void deleteSaveGame(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return;
        }

        if (filename.endsWith(".sav") == false) {
            filename = filename + ".sav";
        }
        String saveFolder = FileUtil.getFolderPath(QspCore.currentQspGame.getGameSaveFolder());
        logger.info(saveFolder + filename);

        File file = QspUri.getFile(saveFolder, filename);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        QspCore.refreshAll();
    }

    public void saveGame(String filename) {
        if (StringUtils.isEmpty(filename)) {
            filename = QspConstants.QUICK_SAVE_NAME;
        }     if ("NONAME".equals(filename)) {
            filename = UUID.randomUUID().toString().toUpperCase();
        }

        if (filename.endsWith(".sav") == false) {
            filename = filename + ".sav";
        }
        String saveFolder = FileUtil.getFolderPath(QspCore.currentQspGame.getGameSaveFolder());
        new File(saveFolder).mkdir();

        QspTask aspTask=new QspTask();
        aspTask.action= QspAction.saveGame.getAction();
        aspTask.qspUri=QspUri.toUri(QspUri.getFile(saveFolder, filename));
        QspThread.addMessage(aspTask);

        QspCore.refreshAll();
    }
}
