package com.baijiacms.qsp.controller;

import com.baijiacms.qsp.common.ResponseResult;
import com.baijiacms.qsp.vo.StatusVo;
import com.qsp.player.Engine;
import com.qsp.player.common.FolderLoader;
import com.qsp.player.entity.QspGame;
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
            Engine.INSTANCEOF.getDevMethodsHelper().toGemFile(gameVo.getGameDevFolder(), gameVo.getGameQproj(), gameVo.getGameFolder() + "/exportQsp/game.qsp");

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
            Engine.INSTANCEOF.getDevMethodsHelper().qspFileToText(gameVo.getGameFile(), gameVo.getGameFolder() + "/exportText/source.txt", gameVo.getQspPassword());

        }
        return ResponseResult.createSuccessResult("");
    }

    @GetMapping("/loadGame")
    @ResponseBody
    public ResponseResult<String> loadGame(@RequestParam(value = "gameId") String gameId) {

        QspGame qspGame = FolderLoader.getFolderMap().get(gameId);
        if (qspGame != null) {
            Engine.INSTANCEOF.getLibEngine().restartGame(FolderLoader.getFolderMap().get(gameId));

        }
        return ResponseResult.createSuccessResult("");
    }
    @GetMapping("/isNeedRefresh")
    @ResponseBody
    public StatusVo isNeedRefresh() {

        StatusVo statusVo=new StatusVo();
        statusVo.setActionschanged(Engine.INSTANCEOF.getLibEngine().isActionschanged());
        statusVo.setMaindescchanged(Engine.INSTANCEOF.getLibEngine().isMaindescchanged(false));
        statusVo.setObjectschanged(Engine.INSTANCEOF.getLibEngine().isObjectschanged());
        statusVo.setVarsdescchanged(Engine.INSTANCEOF.getLibEngine().isVarsdescchanged());
        return statusVo;
    }
}
