package com.qsp.player.libqsp;

import com.qsp.player.libqsp.dto.ErrorData;
import com.qsp.player.libqsp.entity.QspMenuItem;
import com.qsp.player.libqsp.queue.QspAction;
import com.qsp.player.libqsp.queue.QspCore;
import com.qsp.player.libqsp.queue.QspTask;
import com.qsp.player.libqsp.queue.QspThread;
import com.qsp.player.libqsp.util.FileUtil;
import com.qsp.player.libqsp.util.HtmlProcessor;
import com.qsp.player.libqsp.util.QspUri;
import com.qsp.player.libqsp.util.StreamUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Locale;

/**
 * @author：ChenXingYu
 * @date 2024/2/29 9:04
 */
public class LibQspCallBacksImpl implements LibQspCallbacks{

    private HtmlProcessor htmlProcessor = new HtmlProcessor();
    private LibMethods libMethods;
    public LibQspCallBacksImpl(LibMethods libMethods)
    {
        this.libMethods=libMethods;
    }
    @Override
    public void RefreshInt() {
        QspTask aspTask=new QspTask();
        aspTask.action= QspAction.refresh.getAction();
        QspThread.addMessage(aspTask);
    }

    private static final Logger logger = LoggerFactory.getLogger(LibQspCallBacksImpl.class);
    @Override
    public void ShowPicture(String path) {

        logger.info("command:ShowPicture:" + path);
//        if (StringUtils.isNotEmpty(path)) {
//            if (new File(path).exists() == false) {
//                String testPath = QspUri.getFilePath(MemoryUtil.concurrentStringMap.get(MemoryUtil.GAME_FOLDER), path);
//                if (new File(testPath).exists()) {
//                    path = testPath;
//                }
//            }
//        }
    }

    @Override
    public void SetTimer(int msecs) {
        logger.info("command:SetTimer");
    }

    @Override
    public void ShowMessage(String message) {
        logger.info("command:ShowMessage");
    }

    @Override
    public void PlayFile(String path, int volume) {
        logger.info("command:PlayFile");
    }

    @Override
    public boolean IsPlayingFile(String path) {
        logger.info("command:PlayFile");
        return false;
    }

    @Override
    public void CloseFile(String path) {
        logger.info("command:CloseFile");
    }

    @Override
    public void OpenGame(String filename) {
        logger.info("command:OpenGame");
        if (StringUtils.isEmpty(filename)) {
            return;
        }
        String gameFolder= QspCore.concurrentStringMap.get(QspConstants.GAME_FOLDER);
        File savesDir = FileUtil.getOrCreateDirectory(new File(gameFolder), "saves");
        if (StringUtils.isEmpty(filename)) {
            filename = com.qsp.player.libqsp.common.QspConstants.QUICK_SAVE_NAME;
        }

        if (filename.endsWith(".sav") == false) {
            filename = filename + ".sav";
        }
        File saveFile = FileUtil.findFileOrDirectory(savesDir, filename);

        if (saveFile == null || saveFile.exists() == false) {
            logger.error("Save file not found: " + gameFolder+ "/" + filename);
            return;
        }

        logger.info("command:loadGameState");
        final byte[] gameData;

        try (InputStream in = StreamUtils.openInputStream(QspUri.toUri(saveFile))) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                StreamUtils.copy(in, out);
                gameData = out.toByteArray();
            }
        } catch (IOException ex) {
            logger.error("Failed to load game state", ex);
            return;
        }

        if (!libMethods.QSPOpenSavedGameFromData(gameData, gameData.length, true)) {
            showLastQspError();
        }


        RefreshInt();
    }

    @Override
    public void SaveGame(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return;
        }

        if (StringUtils.isEmpty(filename)) {
            filename = com.qsp.player.libqsp.common.QspConstants.QUICK_SAVE_NAME;
        }

        if (filename.endsWith(".sav") == false) {
            filename = filename + ".sav";
        }
        String saveFolder = com.qsp.player.libqsp.common.QspConstants.getSaveFolder(QspCore.concurrentStringMap.get(QspConstants.GAME_ID));
        new File(saveFolder).mkdir();
        qspSaveGameAsData(QspUri.toUri(QspUri.getFile(saveFolder, filename)));

    }
    private void qspSaveGameAsData(QspUri uri) {

        byte[] gameData = libMethods.QSPSaveGameAsData(false);
        if (gameData == null) {
            return;
        }

        try (OutputStream out = StreamUtils.openOutputStream(uri)) {
            out.write(gameData);
        } catch (IOException ex) {
            logger.error("Failed to save the game state", ex);
        }
    }
    @Override
    public String InputBox(String prompt) {

        String message = htmlProcessor.removeHtmlTags(prompt);
        if (message == null) {
            message = "";
        }
        logger.info("showInputBox:" + message);
        return "1";

    }

    @Override
    public int GetMSCount() {
        long now = System.currentTimeMillis();

        if (QspCore.concurrentLongMap.get(QspConstants.LAST_MS_COUNT_CALL_TIME)==null|| QspCore.concurrentLongMap.get(QspConstants.LAST_MS_COUNT_CALL_TIME).longValue()==0) {
            QspCore.concurrentLongMap.put(QspConstants.LAST_MS_COUNT_CALL_TIME, QspCore.concurrentLongMap.get(QspConstants.GAME_START_TIME));
        }
        int dt = (int) (now - QspCore.concurrentLongMap.get(QspConstants.LAST_MS_COUNT_CALL_TIME).longValue());
        QspCore.concurrentLongMap.put(QspConstants.LAST_MS_COUNT_CALL_TIME,now);
        return dt;
    }

    @Override
    public void AddMenuItem(String name, String imgPath) {
        QspMenuItem item = new QspMenuItem();
        item.setImgPath(imgPath);
        item.setName(name);
        QspCore.concurrentArrayListMap.get(QspConstants.MENU_ITEMS).add(item);
    }

    @Override
    public void ShowMenu() {
        QspCore.concurrentBooleanMap.put(QspConstants.SHOW_MENU,true);

    }

    @Override
    public void DeleteMenu() {

        QspCore.concurrentArrayListMap.get(QspConstants.MENU_ITEMS).clear();
        QspCore.concurrentBooleanMap.put(QspConstants.SHOW_MENU,false);
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
        QspCore.concurrentBooleanMap.put(QspConstants.SHOW_WINDOW,isShow);
    }

    @Override
    public byte[] GetFileContents(String path) {
        return FileUtil.getFileContents(path);
    }

    @Override
    public void ChangeQuestPath(String path) {
        logger.info("command:ChangeQuestPath");

    }
    private void showLastQspError() {
        ErrorData errorData = (ErrorData) libMethods.QSPGetLastErrorData();

        String locName = errorData.locName != null ? errorData.locName : "";
        String desc = libMethods.QSPGetErrorDesc(errorData.errorNum);
        desc = desc != null ? desc : "";
        final String message = String.format(
                Locale.getDefault(),
                "Location: %s\nAction: %d\nLine: %d\nError number: %d\nDescription: %s",
                locName,
                errorData.index,
                errorData.line,
                errorData.errorNum,
                desc);

        logger.error(message);
    }
}
