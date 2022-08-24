package com.qsp.player.libqsp;

import com.baijiacms.qsp.common.QspConstants;
import com.baijiacms.qsp.common.WindowType;
import com.baijiacms.qsp.dto.GameObject;
import com.baijiacms.qsp.dto.GameStatus;
import com.baijiacms.qsp.dto.QspMenuItem;
import com.baijiacms.qsp.dto.RefreshRequest;
import com.baijiacms.qsp.player.GameInterface;
import com.baijiacms.qsp.player.service.HtmlProcessor;
import com.baijiacms.qsp.player.thread.LibQspThread;
import com.baijiacms.qsp.util.Uri;
import com.baijiacms.qsp.vi.AudioPlayer;
import com.baijiacms.qsp.vo.GameVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import static com.baijiacms.qsp.util.FileUtil.*;
import static com.baijiacms.qsp.util.StringUtil.isNotEmpty;

public class LibQspProxyImpl implements LibQspProxy, LibQspCallbacks {
    private static final Logger logger = LoggerFactory.getLogger(LibQspProxyImpl.class);

    private final ReentrantLock libQspLock = new ReentrantLock();
    private final GameObject gameObject = new GameObject();
    private GameInterface gamePlayer;

    private final HtmlProcessor htmlProcessor;
    private final AudioPlayer audioPlayer;
    private LibQspThread libQspThread;
    private GameStatus gameStatus;
    public static final String QUICK_SAVE_NAME = "quickSave";
    private LibMethods nativeMethods;

    public LibQspProxyImpl(GameStatus gameStatus,
                           HtmlProcessor htmlProcessor,
                           AudioPlayer audioPlayer, GameInterface gamePlayer) {
        this.gameStatus = gameStatus;
        this.htmlProcessor = htmlProcessor;
        this.audioPlayer = audioPlayer;
        this.gamePlayer = gamePlayer;

    }

    // region LibQspProxy

    @Override
    public void start() {
        logger.info("command:start");
        nativeMethods = new NativeMethods(this);
        this.libQspThread = new LibQspThread(QspConstants.DEFAULT_USER, this);
        this.libQspThread.start();
        this.libQspThread.qspStart(gamePlayer);
    }

    @Override
    public void stop() {


    }

    @Override
    public void runGame(final String id, final String title, final File dir, final File file) {

        logger.info("command:runGame");
        doRunGame(id, title, dir, file);
    }

    private void doRunGame(final String id, final String title, final File dir, final File file) {

        logger.info("doRunGame Thread:" + Thread.currentThread().getName());


        gameObject.reset();
        gameObject.gameRunning = true;
        gameObject.gameId = id;
        gameObject.gameTitle = title;
        gameObject.gameDir = dir;
        gameObject.gameFile = file;
        this.libQspThread.loadGameWorld(gameObject, gamePlayer);
        gameStatus.gameStartTime = System.currentTimeMillis();
        gameStatus.lastMsCountCallTime = 0;


    }

    @Override
    public void restartGame() {

        logger.info("command:restartGame");
        GameObject state = gameObject;
        doRunGame(state.gameId, state.gameTitle, state.gameDir, state.gameFile);

    }

    @Override
    public void loadGameState(final Uri uri) {

        this.libQspThread.loadGameState(uri, this.gamePlayer);
    }

    @Override
    public void saveGameState(final Uri uri) {
        logger.info("command:saveGameState" + uri);
        if (uri != null) {

            logger.info("command:saveGameStateuri:" + uri.getmFile());
        }
        this.libQspThread.qspSaveGameAsData(gamePlayer, uri);
    }

    @Override
    public void onActionSelected(final int index) {

        logger.info("command:onActionSelected");

        this.libQspThread.qspSetSelActionIndex(index, gamePlayer);
    }

    @Override
    public void onActionClicked(final int index) {

        logger.info("command:onActionClicked");
        this.libQspThread.qspExecuteSelActionCode(index, gamePlayer);
    }

    @Override
    public void onObjectSelected(final int index) {

        logger.info("command:onObjectSelected");
        this.libQspThread.qspSetSelObjectIndex(index, gamePlayer);
    }

    @Override
    public void onInputAreaClicked() {

        logger.info("command:onInputAreaClicked");
        final GameInterface inter = gamePlayer;
        if (inter == null) {
            return;
        }

        String input = inter.showInputBox("userInput");
        this.libQspThread.qspSetInputStrText(input, gamePlayer);
    }

    @Override
    public void execute(final String code) {
        String code2 = code.trim();
        logger.info("command:execute:" + code2);
        if ("OPENGAME".equals(code2)) {
            if (gameStatus.isBigKuyash) {

                this.gameStatus.isOpenSaveWindow = true;
                return;
            }
        }
        this.libQspThread.qspExecString(code, gamePlayer);
    }

    @Override
    public void executeCounter() {

        logger.info("command:executeCounter");
        if (libQspLock.isLocked()) {
            return;
        }

        this.libQspThread.qspExecCounter(this.gamePlayer);
    }

    @Override
    public GameObject getGameObject() {
        //刷新
        return gameObject;
    }

    @Override
    public void RefreshInt() {

        logger.info("command:RefreshInt");
        RefreshRequest request = this.libQspThread.getRefreshInterfaceRequest(htmlProcessor);
        GameInterface inter = gamePlayer;
        if (inter != null) {
            inter.refresh(request);
        }
    }

    @Override
    public void ShowPicture(String path) {

        logger.info("command:ShowPicture");
        GameInterface inter = gamePlayer;
        if (inter != null && isNotEmpty(path)) {
            inter.showPicture(path);
        }
    }

    @Override
    public void SetTimer(int msecs) {

        logger.info("command:SetTimer");
        GameInterface inter = gamePlayer;
        if (inter != null) {
            inter.setCounterInterval(msecs);
        }
    }

    @Override
    public void ShowMessage(String message) {
        logger.info("command:ShowMessage");
        GameInterface inter = gamePlayer;
        if (inter != null) {
            inter.showMessage(message);
        }
    }

    @Override
    public void PlayFile(String path, int volume) {
        logger.info("command:PlayFile");
        if (isNotEmpty(path)) {
            audioPlayer.playFile(gameStatus, path, volume);
        }
    }

    @Override
    public boolean IsPlayingFile(final String path) {

        logger.info("command:IsPlayingFile");
        return isNotEmpty(path) && audioPlayer.isPlayingFile(path);
    }

    @Override
    public void CloseFile(String path) {
        logger.info("command:CloseFile");
        if (isNotEmpty(path)) {
            audioPlayer.closeFile(path);
        } else {
            audioPlayer.closeAllFiles();
        }
    }

    @Override
    public void OpenGame(String filename) {
        logger.info("command:OpenGame");
        if (StringUtils.isEmpty(filename)) {
            gameStatus.isOpenSaveWindow = true;
            return;
        }
        File savesDir = getOrCreateDirectory(gameObject.gameDir, "saves");
        if (StringUtils.isEmpty(filename)) {
            filename = QUICK_SAVE_NAME;
        }

        if (filename.endsWith(".sav") == false) {
            filename = filename + ".sav";
        }
        File saveFile = findFileOrDirectory(savesDir, filename);
        gameStatus.refreshAll();
        if (saveFile == null || saveFile.exists() == false) {
            logger.error("Save file not found: " + gameObject.gameDir + "/" + filename);
            return;
        }
        GameInterface inter = gamePlayer;
        if (inter != null) {
            loadGameState(Uri.toUri(saveFile));
        }
    }

    @Override
    public void SaveGame(String filename) {
        logger.info("command:SaveGame:" + filename);
        if (StringUtils.isEmpty(filename)) {
            gameStatus.isOpenSaveWindow = true;
            return;
        }
        GameInterface inter = gamePlayer;
        gameStatus.refreshAll();
        if (inter != null) {
            inter.saveGame(filename);
        }
    }

    @Override
    public String InputBox(String prompt) {
        logger.info("command:InputBox");
        GameInterface inter = gamePlayer;
        return inter != null ? inter.showInputBox(prompt) : null;
    }

    @Override
    public int GetMSCount() {

        logger.info("command:GetMSCount");
        long now = System.currentTimeMillis();
        if (gameStatus.lastMsCountCallTime == 0) {
            gameStatus.lastMsCountCallTime = gameStatus.gameStartTime;
        }
        int dt = (int) (now - gameStatus.lastMsCountCallTime);
        gameStatus.lastMsCountCallTime = now;

        return dt;
    }

    @Override
    public void AddMenuItem(String name, String imgPath) {

        logger.info("command:AddMenuItem");
        QspMenuItem item = new QspMenuItem();
        item.imgPath = imgPath;
        item.name = name;
        gameObject.menuItems.add(item);
    }

    @Override
    public void ShowMenu() {

        logger.info("command:ShowMenu");
        GameInterface inter = gamePlayer;
        if (inter == null) {
            return;
        }

        int result = inter.showMenu();
        if (result != -1) {
            this.libQspThread.qspSelectMenuItem(result);
        }
    }

    @Override
    public void DeleteMenu() {

        logger.info("command:DeleteMenu");
        gameObject.menuItems.clear();
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

        GameInterface inter = gamePlayer;
        if (inter != null) {
            WindowType windowType = WindowType.values()[type];
            inter.showWindow(windowType, isShow);
        }
    }

    @Override
    public byte[] GetFileContents(String path) {

        logger.info("command:GetFileContents");
        return getFileContents(path);
    }

    @Override
    public void ChangeQuestPath(String path) {
        logger.info("command:ChangeQuestPath");
        File dir = new File(path);
        if (!dir.exists()) {
            logger.error("Game directory not found: " + path);
            return;
        }
        if (!gameObject.gameDir.equals(dir)) {
            gameObject.gameDir = dir;
        }
    }

    @Override
    public void qspFileToText(GameVo gameVo, String toFile) {
        this.libQspThread.qspFileToText(gameVo, toFile);
    }

    @Override
    public void toGemFile(GameVo gameVo, String toFile) {

        this.libQspThread.toGemFile(gameVo, toFile);
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public LibMethods getNativeMethods() {
        return nativeMethods;
    }
}
