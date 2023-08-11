package com.baijiacms.qsp.controller;

import com.baijiacms.qsp.common.ResponseResult;
import com.baijiacms.qsp.vo.SaveGameVo;
import com.qsp.player.Engine;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        File file = new File(Engine.INSTANCEOF.getLibEngine().getQspGame().getGameSaveFolder());
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
        Engine.INSTANCEOF.getLibEngine().deleteSaveGame(actionScript);
        return ResponseResult.createSuccessResult("");
    }
    @GetMapping("/gameSave")
    @ResponseBody
    public ResponseResult<String> GameSave(@RequestParam(value = "actionScript") String actionScript) {

        Engine.INSTANCEOF.getLibEngine().saveGame(actionScript);
        return ResponseResult.createSuccessResult("");
    }
    @GetMapping("/loadGameSave")
    @ResponseBody
    public ResponseResult<String> LoadGameSave(@RequestParam(value = "actionScript") String actionScript) {

        Engine.INSTANCEOF.getLibEngine().loadSaveGame(actionScript);
        return ResponseResult.createSuccessResult("");
    }
    @GetMapping("/loadQuickSave")
    @ResponseBody
    public ResponseResult<String> loadQuickSave() {
        Engine.INSTANCEOF.getLibEngine().loadSaveGame(null);
        return ResponseResult.createSuccessResult("");

    }
    @GetMapping("/quickSave")
    @ResponseBody
    public ResponseResult<String> QuickSave() {

        Engine.INSTANCEOF.getLibEngine().saveGame(null);
        return ResponseResult.createSuccessResult("");
    }
}
