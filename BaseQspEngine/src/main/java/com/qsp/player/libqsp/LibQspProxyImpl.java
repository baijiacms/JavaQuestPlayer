package com.qsp.player.libqsp;

import com.qsp.player.GameStatus;
import com.qsp.player.common.WindowType;
import com.qsp.player.libqsp.dto.GameObject;
import com.qsp.player.libqsp.dto.QspMenuItem;
import com.qsp.player.libqsp.dto.RefreshInterfaceRequest;
import com.qsp.player.libqsp.service.AudioPlayer;
import com.qsp.player.libqsp.service.GameContentResolver;
import com.qsp.player.libqsp.service.HtmlProcessor;
import com.qsp.player.libqsp.thread.LibQspThread;
import com.qsp.player.util.Uri;
import com.qsp.webengine.vo.GameVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import static com.qsp.player.util.FileUtil.*;
import static com.qsp.player.util.StringUtil.isNotEmpty;

public class LibQspProxyImpl implements LibQspProxy, LibQspCallbacks {
    private static final Logger logger = LoggerFactory.getLogger(LibQspProxyImpl.class);

    private final ReentrantLock libQspLock = new ReentrantLock();
    private final GameObject gameObject = new GameObject();
    private GameInterface gameInterface;

    private final GameContentResolver gameContentResolver;
    private final HtmlProcessor htmlProcessor;
    private final AudioPlayer audioPlayer;
    private LibQspThread libQspThread;
    private GameStatus gameStatus;
    public static final String quickSaveName = "quickSave";
    private NativeMethods nativeMethods;

    public LibQspProxyImpl(String userId, GameStatus gameStatus,
                           GameContentResolver gameContentResolver,
                           HtmlProcessor htmlProcessor,
                           AudioPlayer audioPlayer) {
        this.gameStatus = gameStatus;
        this.gameContentResolver = gameContentResolver;
        this.htmlProcessor = htmlProcessor;
        this.audioPlayer = audioPlayer;

        nativeMethods = new NativeMethods(this);
        this.libQspThread = new LibQspThread(userId, this);
        this.libQspThread.start();
    }

    // region LibQspProxy

    @Override
    public void start() {
        logger.info("command:start");
        this.libQspThread.QSPStart(gameInterface);
    }

    @Override
    public void stop() {

//        if (libQspThread == null) {
//            return;
//        }

//        if (libQspThreadInited) {
////            Handler handler = libQspHandler;
////            if (handler != null) {
////                handler.getLooper().quitSafely();
////            }
//            libQspThreadInited = false;
//        } else {
//            logger.warn("libqsp thread has been started, but not initialized");
//        }
//        libQspThread = null;
    }

    @Override
    public void runGame(final String id, final String title, final File dir, final File file) {

        logger.info("command:runGame");
//        runOnQspThread(() -> doRunGame(id, title, dir, file));
        doRunGame(id, title, dir, file);
    }

    private void doRunGame(final String id, final String title, final File dir, final File file) {

        logger.info("doRunGame Thread:" + Thread.currentThread().getName());

//        gameInterface.doWithCounterDisabled(() -> {

        //            audioPlayer.closeAllFiles();


        gameContentResolver.setGameDir(dir);


        gameObject.reset();
        gameObject.gameRunning = true;
        gameObject.gameId = id;
        gameObject.gameTitle = title;
        gameObject.gameDir = dir;
        gameObject.gameFile = file;
        this.libQspThread.loadGameWorld(gameObject, gameInterface);
        gameStatus.gameStartTime = System.currentTimeMillis();
        gameStatus.lastMsCountCallTime = 0;


//        });
    }

    @Override
    public void restartGame() {

        logger.info("command:restartGame");
//        runOnQspThread(() -> {
        GameObject state = gameObject;
        doRunGame(state.gameId, state.gameTitle, state.gameDir, state.gameFile);
//        });
    }

    @Override
    public void loadGameState(final Uri uri) {

        this.libQspThread.loadGameState(uri, this.gameInterface);
    }

    @Override
    public void saveGameState(final Uri uri) {
        logger.info("command:saveGameState" + uri);
        if (uri != null) {

            logger.info("command:saveGameStateuri:" + uri.getmFile());
        }
        //if (!isSameThread(libQspHandler.getLooper().getThread())) {
        //    runOnQspThread(() -> saveGameState(uri));
        //    return;
        //}
        this.libQspThread.QSPSaveGameAsData(gameInterface, uri);
    }

    @Override
    public void onActionSelected(final int index) {

        logger.info("command:onActionSelected");
//        runOnQspThread(() -> {

        this.libQspThread.QSPSetSelActionIndex(index, gameInterface);
//        });
    }

    @Override
    public void onActionClicked(final int index) {

        logger.info("command:onActionClicked");
//      runOnQspThread(() -> {
        this.libQspThread.QSPExecuteSelActionCode(index, gameInterface);
//       });
    }

    @Override
    public void onObjectSelected(final int index) {

        logger.info("command:onObjectSelected");
//        runOnQspThread(() -> {
        this.libQspThread.QSPSetSelObjectIndex(index, gameInterface);
//        });
    }

    @Override
    public void onInputAreaClicked() {

        logger.info("command:onInputAreaClicked");
        final GameInterface inter = gameInterface;
        if (inter == null) {
            return;
        }

//        runOnQspThread(() -> {
        String input = inter.showInputBox("userInput");
        this.libQspThread.QSPSetInputStrText(input, gameInterface);
//        });
    }

    @Override
    public void execute(final String code) {
        String code2 = code.trim();
        logger.info("command:execute:" + code2);
        if ("OPENGAME".equals(code2)) {
            if (gameStatus.IS_BIG_KUYASH) {

                this.gameStatus.isOpenSaveWindow = true;
                return;
            }
        }
//        runOnQspThread(() -> {
        this.libQspThread.QSPExecString(code, gameInterface);
//        });
    }

    @Override
    public void executeCounter() {

        logger.info("command:executeCounter");
        if (libQspLock.isLocked()) {
            return;
        }

//        runOnQspThread(() -> {
        this.libQspThread.QSPExecCounter(this.gameInterface);
//        });
    }

    @Override
    public GameObject getGameObject() {
        //刷新
        // logger.info("command:getGameState:");
        return gameObject;
    }

    @Override
    public void setGameInterface(GameInterface view) {

        logger.info("command:setGameInterface");
        gameInterface = view;
    }

    // endregion LibQspProxy

    // region LibQspCallbacks

    @Override
    public void RefreshInt() {

        logger.info("command:RefreshInt");
        RefreshInterfaceRequest request = this.libQspThread.getRefreshInterfaceRequest(htmlProcessor);
        GameInterface inter = gameInterface;
        if (inter != null) {
            inter.refresh(request);
        }
    }

    @Override
    public void ShowPicture(String path) {

        logger.info("command:ShowPicture");
        GameInterface inter = gameInterface;
        if (inter != null && isNotEmpty(path)) {
            inter.showPicture(path);
        }
    }

    @Override
    public void SetTimer(int msecs) {

        logger.info("command:SetTimer");
        GameInterface inter = gameInterface;
        if (inter != null) {
            inter.setCounterInterval(msecs);
        }
    }

    @Override
    public void ShowMessage(String message) {
        logger.info("command:ShowMessage");
        GameInterface inter = gameInterface;
        if (inter != null) {
            inter.showMessage(message);
        }
    }

    @Override
    public void PlayFile(String path, int volume) {
        logger.info("command:PlayFile");
        if (isNotEmpty(path)) {
            audioPlayer.playFile(path, volume);
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
        if (gameStatus.IS_BIG_KUYASH) {
            if (StringUtils.isEmpty(filename)) {

                gameStatus.isOpenSaveWindow = true;
                return;
            }
        }
        File savesDir = getOrCreateDirectory(gameObject.gameDir, "saves");
        if (StringUtils.isEmpty(filename)) {
            filename = quickSaveName;
        }

        if (filename.endsWith(".sav") == false) {
            filename = filename + ".sav";
        }
        File saveFile = findFileOrDirectory(savesDir, filename);
        gameStatus.refreshAll();
        if (saveFile == null || saveFile.exists() == false) {
            logger.error("Save file not found: " + savesDir + "/" + filename);
            return;
        }
        GameInterface inter = gameInterface;
        if (inter != null) {
            loadGameState(Uri.fromFile(saveFile));
        }
    }

    @Override
    public void SaveGame(String filename) {
        logger.info("command:SaveGame:" + filename);

        if (gameStatus.IS_BIG_KUYASH) {
            if (StringUtils.isEmpty(filename)) {

                gameStatus.isOpenSaveWindow = true;
                return;
            }
        }
        GameInterface inter = gameInterface;
        gameStatus.refreshAll();
        if (inter != null) {
            inter.saveGame(filename);
        }
    }

    @Override
    public String InputBox(String prompt) {
        logger.info("command:InputBox");
        GameInterface inter = gameInterface;
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
        GameInterface inter = gameInterface;
        if (inter == null) {
            return;
        }

        int result = inter.showMenu();
        if (result != -1) {
            this.libQspThread.QSPSelectMenuItem(result);
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

//        logger.info("command:ShowWindow");
        GameInterface inter = gameInterface;
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
            gameContentResolver.setGameDir(dir);
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

    public NativeMethods getNativeMethods() {
        return nativeMethods;
    }
// endregion LibQspCallbacks
}