package com.baijiacms.qsp.controller;

import com.baijiacms.qsp.common.ResponseResult;
import com.baijiacms.qsp.vo.StatusVo;
import com.qsp.player.libqsp.QspConstants;
import com.qsp.player.libqsp.common.FolderLoader;
import com.qsp.player.libqsp.entity.QspGame;
import com.qsp.player.libqsp.queue.QspAction;
import com.qsp.player.libqsp.queue.QspCore;
import com.qsp.player.libqsp.queue.QspTask;
import com.qsp.player.libqsp.queue.QspThread;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：ChenXingYu
 * @date 2023/8/4 11:08
 */
@RestController
@RequestMapping("/qsp")
public class MainController {
    @GetMapping("/gameList")
    @ResponseBody
    public List<QspGame> gameList() {

        QspCore.concurrentBooleanMap.put(QspConstants.GAME_IS_RUNNING,false);
        List<QspGame> gameList = new ArrayList<>();
        FolderLoader.loadGameFolder(gameList);
        return gameList;
    }
    @GetMapping("/exportGameToQsp")
    @ResponseBody
    public ResponseResult<String> exportGameToQsp(@RequestParam(value = "gameId") String gameId) {

        QspGame qspGame = FolderLoader.getFolderMap().get(gameId);
        if (qspGame != null) {
            QspGame gameVo = FolderLoader.getFolderMap().get(gameId);
            new File(gameVo.getGameFolder() + "/exportQsp/").mkdir();
            QspCore.devMethodsHelper.toGemFile(gameVo.getGameDevFolder(), gameVo.getGameQproj(), gameVo.getGameFolder() + "/exportQsp/game.qsp");

        }
        return ResponseResult.createSuccessResult("");
    }
    @GetMapping("/exportGameToText")
    @ResponseBody
    public ResponseResult<String> exportGameToText(@RequestParam(value = "gameId") String gameId) {

        QspGame qspGame = FolderLoader.getFolderMap().get(gameId);
        if (qspGame != null) {
            QspGame gameVo = FolderLoader.getFolderMap().get(gameId);
            new File(gameVo.getGameFolder() + "/exportText/").mkdir();
            QspCore.devMethodsHelper.qspFileToText(gameVo.getGameFile(), gameVo.getGameFolder() + "/exportText/source.txt", gameVo.getQspPassword());

        }
        return ResponseResult.createSuccessResult("");
    }

    @GetMapping("/loadGame")
    @ResponseBody
    public ResponseResult<String> loadGame(@RequestParam(value = "gameId") String gameId) {

        QspGame qspGame = FolderLoader.getFolderMap().get(gameId);
        if (qspGame != null) {
            QspTask aspTask=new QspTask();
            aspTask.action= QspAction.initGame.getAction();
            aspTask.qspGame=qspGame;
            QspThread.addMessage(aspTask);
        }
        return ResponseResult.createSuccessResult("");
    }
    @GetMapping("/isNeedRefresh")
    @ResponseBody
    public StatusVo isNeedRefresh() {

        StatusVo statusVo=new StatusVo();
        statusVo.setActionschanged(QspCore.actionschanged.get());
        statusVo.setMaindescchanged(QspCore.maindescchanged.get());
        statusVo.setObjectschanged(QspCore.objectschanged.get());
        statusVo.setVarsdescchanged(QspCore.varsdescchanged.get());
        return statusVo;
    }
}
