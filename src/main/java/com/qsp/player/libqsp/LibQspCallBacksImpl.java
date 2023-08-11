package com.qsp.player.libqsp;

import com.qsp.player.LibEngine;
import com.qsp.player.common.QspConstants;
import com.qsp.player.entity.QspMenuItem;
import com.qsp.player.thread.LibQspThread;
import com.qsp.player.util.FileUtil;
import com.qsp.player.util.HtmlProcessor;
import com.qsp.player.util.QspUri;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author baijiacms
 */
public class LibQspCallBacksImpl implements LibQspCallbacks {
    private LibEngine libEngine;
    private HtmlProcessor htmlProcessor = new HtmlProcessor();
    private LibQspThread libQspThread;
    private int userId;

    public LibQspCallBacksImpl(int userId, LibEngine libEngine, LibQspThread libQspThread) {
        this.userId = userId;
        this.libEngine = libEngine;
        this.libQspThread = libQspThread;
    }

    private static final Logger logger = LoggerFactory.getLogger(LibQspCallBacksImpl.class);

    @Override
    public void RefreshInt() {

        libQspThread.getLibQspProxy().getRefreshInterfaceRequest();
    }

    @Override
    public void ShowPicture(String path) {

        logger.info("command:ShowPicture:" + path);
        if (StringUtils.isNotEmpty(path)) {
            if (new File(path).exists() == false) {
                String testPath = QspUri.getFilePath(libEngine.getQspGame().getGameFolder(), path);
                if (new File(testPath).exists()) {
                    path = testPath;
                }
            }
            libEngine.getQspUI().showPicture(path);
        }
    }

    @Override
    public void SetTimer(int msecs) {
        logger.info("command:SetTimer");
    }

    @Override
    public void ShowMessage(String message) {

        logger.info("command:ShowMessage");
        libEngine.getQspUI().showAlertDialog(message);
    }

    @Override
    public void PlayFile(String path, int volume) {
        logger.info("command:PlayFile");
        if (StringUtils.isNotEmpty(path)) {
            libEngine.getAudioObject().getQspAudio().playFile(libEngine.getQspGame().getGameFolder(), path, volume);
        }
    }

    @Override
    public boolean IsPlayingFile(String path) {

        logger.info("command:IsPlayingFile");
        return StringUtils.isNotEmpty(path) && libEngine.getAudioObject().getQspAudio().isPlayingFile(path);
    }

    @Override
    public void CloseFile(String path) {
        logger.info("command:CloseFile");
        if (StringUtils.isNotEmpty(path)) {
            libEngine.getAudioObject().getQspAudio().closeFile(path);
        } else {
            libEngine.getAudioObject().getQspAudio().closeAllFiles();
        }
    }

    @Override
    public void OpenGame(String filename) {
        logger.info("command:OpenGame");
        if (StringUtils.isEmpty(filename)) {
            return;
        }
        File savesDir = FileUtil.getOrCreateDirectory(new File(libEngine.getQspGame().getGameFolder()), "saves");
        if (StringUtils.isEmpty(filename)) {
            filename = QspConstants.QUICK_SAVE_NAME;
        }

        if (filename.endsWith(".sav") == false) {
            filename = filename + ".sav";
        }
        File saveFile = FileUtil.findFileOrDirectory(savesDir, filename);
        libEngine.getGameStatus().refreshAll();
        if (saveFile == null || saveFile.exists() == false) {
            logger.error("Save file not found: " + libEngine.getQspGame().getGameFolder() + "/" + filename);
            return;
        }
        this.libQspThread.loadGameState(QspUri.toUri(saveFile));
    }

    @Override
    public void SaveGame(String filename) {
        logger.info("command:SaveGame:" + filename);
        if (StringUtils.isEmpty(filename)) {
            return;
        }
        libEngine.getGameStatus().refreshAll();

        if (StringUtils.isEmpty(filename)) {
            filename = QspConstants.QUICK_SAVE_NAME;
        }

        if (filename.endsWith(".sav") == false) {
            filename = filename + ".sav";
        }
        String saveFolder = QspConstants.getSaveFolder(libEngine.getQspGame().getGameId());
        new File(saveFolder).mkdir();
        this.libQspThread.qspSaveGameAsData(QspUri.toUri(QspUri.getFile(saveFolder, filename)));
        libEngine.getGameStatus().refreshAll();

    }

    @Override
    public String InputBox(String prompt) {
        String message = htmlProcessor.removeHtmlTags(prompt);
        if (message == null) {
            message = "";
        }
        logger.info("showInputBox:" + message);
        return libEngine.getQspUI().showTextInputDialog(prompt);
    }

    @Override
    public int GetMSCount() {

        logger.info("command:GetMSCount");
        long now = System.currentTimeMillis();
        if (libEngine.getGameStatus().getLastMsCountCallTime() == 0) {
            libEngine.getGameStatus().setLastMsCountCallTime(libEngine.getGameStatus().getGameStartTime());
        }
        int dt = (int) (now - libEngine.getGameStatus().getLastMsCountCallTime());
        libEngine.getGameStatus().setLastMsCountCallTime(now);

        return dt;
    }

    @Override
    public void AddMenuItem(String name, String imgPath) {
        logger.info("command:AddMenuItem");
        QspMenuItem item = new QspMenuItem();
        item.setImgPath(imgPath);
        item.setName(name);
        libEngine.getGameStatus().getMenuItems().add(item);
    }

    @Override
    public void ShowMenu() {
        logger.info("command:ShowMenu");
        libEngine.getGameStatus().setShowMenu(true);
    }

    @Override
    public void DeleteMenu() {
        logger.info("command:DeleteMenu");
        libEngine.getGameStatus().getMenuItems().clear();
        libEngine.getGameStatus().setShowMenu(false);
    }

    @Override
    public void Wait(int msecs) {
        logger.info("command:Wait");
        try {
            Thread.sleep(msecs);
        } catch (InterruptedException ex) {
            logger.error("Wait failed", ex);
        }
    }

    @Override
    public void ShowWindow(int type, boolean isShow) {
        logger.info("command:SetShowWindow:" + isShow);
        libEngine.getGameStatus().setShowWindow(isShow);

    }

    @Override
    public byte[] GetFileContents(String path) {
        logger.info("command:GetFileContents");
        return FileUtil.getFileContents(path);
    }

    @Override
    public void ChangeQuestPath(String path) {
        logger.info("command:ChangeQuestPath");

    }
}
