package com.qsp.player.libqsp;

import com.qsp.webengine.vo.GameVo;
import com.qsp.player.core.QspConstants;
import com.qsp.player.core.game.DevUtils;
import com.qsp.player.core.util.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.qsp.player.core.QspGameStatus;
import com.qsp.player.core.game.service.AudioPlayer;
import com.qsp.player.core.game.service.GameContentResolver;
import com.qsp.player.core.game.service.HtmlProcessor;
import com.qsp.player.libqsp.dto.ActionData;
import com.qsp.player.libqsp.dto.ErrorData;
import com.qsp.player.libqsp.dto.GetVarValuesResponse;
import com.qsp.player.libqsp.dto.ObjectData;
import com.qsp.player.core.model.GameState;
import com.qsp.player.core.model.InterfaceConfiguration;
import com.qsp.player.core.model.QspListItem;
import com.qsp.player.core.model.QspMenuItem;
import com.qsp.player.core.model.RefreshInterfaceRequest;
import com.qsp.player.core.model.WindowType;
import com.qsp.player.core.util.StreamUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

import static com.qsp.player.core.util.FileUtil.findFileOrDirectory;
import static com.qsp.player.core.util.FileUtil.getFileContents;
import static com.qsp.player.core.util.FileUtil.getOrCreateDirectory;
import static com.qsp.player.core.util.StringUtil.getStringOrEmpty;
import static com.qsp.player.core.util.StringUtil.isNotEmpty;
import static com.qsp.player.core.util.ThreadUtil.throwIfNotMainThread;

public class LibQspProxyImpl implements LibQspProxy, LibQspCallbacks {
    private static final Logger logger = LoggerFactory.getLogger(LibQspProxyImpl.class);

    private final ReentrantLock libQspLock = new ReentrantLock();
    private final GameState gameState = new GameState();
    private final NativeMethods nativeMethods = new NativeMethods(this);
    private final  DevUtils devUtils= new DevUtils();
    private Thread libQspThread;
    private volatile Handler libQspHandler;
    private volatile boolean libQspThreadInited;
    private volatile long gameStartTime;
    private volatile long lastMsCountCallTime;
    private GameInterface gameInterface;

    private final GameContentResolver gameContentResolver;
    private final HtmlProcessor htmlProcessor;
    private final AudioPlayer audioPlayer;
    public static final String quickSaveName="quickSave";
    public LibQspProxyImpl(
            GameContentResolver gameContentResolver,
            HtmlProcessor htmlProcessor,
            AudioPlayer audioPlayer) {
        this.gameContentResolver = gameContentResolver;
        this.htmlProcessor = htmlProcessor;
        this.audioPlayer = audioPlayer;
    }

    private void runOnQspThread(final Runnable runnable) {
        throwIfNotMainThread();

        if (libQspThread == null) {
            logger.warn("libqsp thread has not been started");
            return;
        }
        if (!libQspThreadInited) {
            logger.warn("libqsp thread has been started, but not initialized");
            return;
        }
        Handler handler = libQspHandler;
        if (handler != null) {
            handler.post(() -> {
                libQspLock.lock();
                try {
                    runnable.run();
                } finally {
                    libQspLock.unlock();
                }
            });
        }
    }

    private boolean loadGameWorld() {
        byte[] gameData;
        GameVo gameVo= QspConstants.GAME_FOLDER_MAP.get(gameState.gameId);
        if(gameVo.getIsDevProject()==1)
        {
//            devUtils.toGemFile(gameVo.getGameDevFolder(),gameVo.getGameQproj(),"D:/1.qsp");
            gameData= devUtils.getGemDate(gameVo.getGameDevFolder(),gameVo.getGameQproj());
        }else {
            try (FileInputStream in = new FileInputStream(gameState.gameFile)) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    StreamUtil.copy(in, out);
                    gameData = out.toByteArray();
                }
            } catch (IOException ex) {
                logger.error("Failed to load the game world", ex);
                return false;
            }
        }
        String fileName = gameState.gameFile.getAbsolutePath();

        if (!nativeMethods.QSPLoadGameWorldFromData(gameData, gameData.length, fileName)) {
            showLastQspError();
            return false;
        }

        return true;
    }

    private void showLastQspError() {
        ErrorData errorData = (ErrorData) nativeMethods.QSPGetLastErrorData();
        String locName = getStringOrEmpty(errorData.locName);
        String desc = getStringOrEmpty(nativeMethods.QSPGetErrorDesc(errorData.errorNum));

        final String message = String.format(
                Locale.getDefault(),
                "Location: %s\nAction: %d\nLine: %d\nError number: %d\nDescription: %s",
                locName,
                errorData.index,
                errorData.line,
                errorData.errorNum,
                desc);

        logger.error(message);

        GameInterface inter = gameInterface;
        if (inter != null) {
            gameInterface.showError(message);
        }
    }

    /**
     * Загружает конфигурацию интерфейса - использование HTML, шрифт и цвета - из библиотеки.
     *
     * @return <code>true</code> если конфигурация изменилась, иначе <code>false</code>
     */
    private boolean loadInterfaceConfiguration() {
        InterfaceConfiguration config = gameState.interfaceConfig;
        boolean changed = false;

        GetVarValuesResponse htmlResult = (GetVarValuesResponse) nativeMethods.QSPGetVarValues("USEHTML", 0);
        if (htmlResult.success) {
            boolean useHtml = htmlResult.intValue != 0;
            if (config.useHtml != useHtml) {
                config.useHtml = useHtml;
                changed = true;
            }
        }
        GetVarValuesResponse fSizeResult = (GetVarValuesResponse) nativeMethods.QSPGetVarValues("FSIZE", 0);
        if (fSizeResult.success && config.fontSize != fSizeResult.intValue) {
            config.fontSize = fSizeResult.intValue;
            changed = true;
        }
        GetVarValuesResponse bColorResult = (GetVarValuesResponse) nativeMethods.QSPGetVarValues("BCOLOR", 0);
        if (bColorResult.success && config.backColor != bColorResult.intValue) {
            config.backColor = bColorResult.intValue;
            changed = true;
        }
        GetVarValuesResponse fColorResult = (GetVarValuesResponse) nativeMethods.QSPGetVarValues("FCOLOR", 0);
        if (fColorResult.success && config.fontColor != fColorResult.intValue) {
            config.fontColor = fColorResult.intValue;
            changed = true;
        }
        GetVarValuesResponse lColorResult = (GetVarValuesResponse) nativeMethods.QSPGetVarValues("LCOLOR", 0);
        if (lColorResult.success && config.linkColor != lColorResult.intValue) {
            config.linkColor = lColorResult.intValue;
            changed = true;
        }

        return changed;
    }

    private ArrayList<QspListItem> getActions() {
        ArrayList<QspListItem> actions = new ArrayList<>();
        int count = nativeMethods.QSPGetActionsCount();
        for (int i = 0; i < count; ++i) {
            ActionData actionData = (ActionData) nativeMethods.QSPGetActionData(i);
            QspListItem action = new QspListItem();
            action.index=i;
            action.text = gameState.interfaceConfig.useHtml ? htmlProcessor.removeHtmlTags(actionData.name) : actionData.name;
            actions.add(action);
        }
        return actions;
    }

    private ArrayList<QspListItem> getObjects() {
        ArrayList<QspListItem> objects = new ArrayList<>();
        int count = nativeMethods.QSPGetObjectsCount();
        for (int i = 0; i < count; i++) {
            ObjectData objectResult = (ObjectData) nativeMethods.QSPGetObjectData(i);
            QspListItem object = new QspListItem();
            object.index=i;
            object.text = gameState.interfaceConfig.useHtml ? htmlProcessor.removeHtmlTags(objectResult.name) : objectResult.name;
            objects.add(object);
        }
        return objects;
    }

    // region LibQspProxy

    @Override
    public void start() {
        libQspThread = new Thread("libqsp") {
            @Override
            public void run() {
                try {
                    nativeMethods.QSPInit();
                    Looper.prepare();
                    libQspHandler = new Handler();
                    libQspThreadInited = true;

                    Looper.loop();

                    nativeMethods.QSPDeInit();
                } catch (Throwable t) {
                    logger.error("libqsp thread has stopped exceptionally", t);
                }
            }
        };
        libQspThread.start();
    }

    @Override
    public void stop() {
        throwIfNotMainThread();

        if (libQspThread == null) {
            return;
        }

        if (libQspThreadInited) {
            Handler handler = libQspHandler;
            if (handler != null) {
                handler.getLooper().quitSafely();
            }
            libQspThreadInited = false;
        } else {
            logger.warn("libqsp thread has been started, but not initialized");
        }
        libQspThread = null;
    }

    @Override
    public void runGame(final String id, final String title, final File dir, final File file) {

        logger.info("command:runGame");
        runOnQspThread(() -> doRunGame(id, title, dir, file));
    }

    private void doRunGame(final String id, final String title, final File dir, final File file) {


        gameInterface.doWithCounterDisabled(() -> {
            audioPlayer.closeAllFiles();

            gameState.reset();
            gameState.gameRunning = true;
            gameState.gameId = id;
            gameState.gameTitle = title;
            gameState.gameDir = dir;
            gameState.gameFile = file;

            gameContentResolver.setGameDir(dir);

            if (!loadGameWorld()) {
                return;
            }

            gameStartTime = SystemClock.elapsedRealtime();
            lastMsCountCallTime = 0;

            if (!nativeMethods.QSPRestartGame(true)) {
                showLastQspError();
            }
        });
    }

    @Override
    public void restartGame() {

        logger.info("command:restartGame");
        runOnQspThread(() -> {
            GameState state = gameState;
            doRunGame(state.gameId, state.gameTitle, state.gameDir, state.gameFile);
        });
    }

    @Override
    public void loadGameState(final Uri uri) {

        logger.info("command:loadGameState");
//        if (!isSameThread(libQspHandler.getLooper().getThread())) {
//            runOnQspThread(() -> loadGameState(uri));
//            return;
//        }
        final byte[] gameData;

        try (InputStream in = openInputStream(uri)) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                StreamUtil.copy(in, out);
                gameData = out.toByteArray();
            }
        } catch (IOException ex) {
            logger.error("Failed to load game state", ex);
            return;
        }

        if (!nativeMethods.QSPOpenSavedGameFromData(gameData, gameData.length, true)) {
            showLastQspError();
        }
    }

    public OutputStream openOutputStream(Uri uri,String action)
    {

        try {
            return new FileOutputStream(uri.getmFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("null");
        }
    }
    public InputStream openInputStream(Uri uri)
    {
        try {
            return new FileInputStream(uri.getmFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("null");
        }
    }
    @Override
    public void saveGameState(final Uri uri) {
        logger.info("command:saveGameState");
        //if (!isSameThread(libQspHandler.getLooper().getThread())) {
        //    runOnQspThread(() -> saveGameState(uri));
        //    return;
        //}
        byte[] gameData = nativeMethods.QSPSaveGameAsData(false);
        if (gameData == null) {
            return;
        }

        try (OutputStream out = openOutputStream(uri, "w")) {
            out.write(gameData);
        } catch (IOException ex) {
            logger.error("Failed to save the game state", ex);
        }
    }

    @Override
    public void onActionSelected(final int index) {

        logger.info("command:onActionSelected");
        runOnQspThread(() -> {
            if (!nativeMethods.QSPSetSelActionIndex(index, true)) {
                showLastQspError();
            }
        });
    }

    @Override
    public void onActionClicked(final int index) {

        logger.info("command:onActionClicked");
      runOnQspThread(() -> {
            if (!nativeMethods.QSPSetSelActionIndex(index, false)) {
                showLastQspError();
            }
            if (!nativeMethods.QSPExecuteSelActionCode(true)) {
                showLastQspError();
            }
       });
    }

    @Override
    public void onObjectSelected(final int index) {

        logger.info("command:onObjectSelected");
        runOnQspThread(() -> {
            if (!nativeMethods.QSPSetSelObjectIndex(index, true)) {
                showLastQspError();
            }
        });
    }

    @Override
    public void onInputAreaClicked() {

        logger.info("command:onInputAreaClicked");
        final GameInterface inter = gameInterface;
        if (inter == null) {
            return;
        }

        runOnQspThread(() -> {
            String input = inter.showInputBox("userInput");
            nativeMethods.QSPSetInputStrText(input);

            if (!nativeMethods.QSPExecUserInput(true)) {
                showLastQspError();
            }
        });
    }
    @Override
    public void execute(final String code) {

        logger.info("command:execute:"+code);
        runOnQspThread(() -> {
            if (!nativeMethods.QSPExecString(code, true)) {
                showLastQspError();
            }
        });
    }

    @Override
    public void executeCounter() {

        logger.info("command:executeCounter");
        if (libQspLock.isLocked()) {
            return;
        }

        runOnQspThread(() -> {
            if (!nativeMethods.QSPExecCounter(true)) {
                showLastQspError();
            }
        });
    }

    @Override
    public GameState getGameState() {
        //刷新
       // logger.info("command:getGameState:");
        return gameState;
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
        RefreshInterfaceRequest request = new RefreshInterfaceRequest();

        boolean configChanged = loadInterfaceConfiguration();
        if (configChanged) {
            request.interfaceConfigChanged = true;
        }
        if (nativeMethods.QSPIsMainDescChanged()) {
            gameState.mainDesc = nativeMethods.QSPGetMainDesc();
            request.mainDescChanged = true;
        }
        if (nativeMethods.QSPIsActionsChanged()) {
            gameState.actions = getActions();
            request.actionsChanged = true;
        }
        if (nativeMethods.QSPIsObjectsChanged()) {
            gameState.objects = getObjects();
            request.objectsChanged = true;
        }
        if (nativeMethods.QSPIsVarsDescChanged()) {
            gameState.varsDesc = nativeMethods.QSPGetVarsDesc();
            request.varsDescChanged = true;
        }
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
        File savesDir = getOrCreateDirectory(gameState.gameDir, "saves");
        if(StringUtils.isEmpty(filename))
        {
            filename=quickSaveName;
        }
        File saveFile = findFileOrDirectory(savesDir, filename);
        QspGameStatus.refreshAll();
        if (saveFile == null||saveFile.exists()==false) {
            logger.error("Save file not found: " +savesDir+"/"+ filename);
            return;
        }
        GameInterface inter = gameInterface;
        if (inter != null) {
            inter.doWithCounterDisabled(() -> loadGameState(Uri.fromFile(saveFile)));
        }
    }

    @Override
    public void SaveGame(String filename) {
        logger.info("command:SaveGame");
        GameInterface inter = gameInterface;
        QspGameStatus.refreshAll();
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
        long now = SystemClock.elapsedRealtime();
        if (lastMsCountCallTime == 0) {
            lastMsCountCallTime = gameStartTime;
        }
        int dt = (int) (now - lastMsCountCallTime);
        lastMsCountCallTime = now;

        return dt;
    }

    @Override
    public void AddMenuItem(String name, String imgPath) {

        logger.info("command:AddMenuItem");
        QspMenuItem item = new QspMenuItem();
        item.imgPath = imgPath;
        item.name = name;
        gameState.menuItems.add(item);
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
            nativeMethods.QSPSelectMenuItem(result);
        }
    }

    @Override
    public void DeleteMenu() {

        logger.info("command:DeleteMenu");
        gameState.menuItems.clear();
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
        if (!gameState.gameDir.equals(dir)) {
            gameState.gameDir = dir;
            gameContentResolver.setGameDir(dir);
        }
    }
    @Override
    public void qspFileToText(GameVo gameVo, String toFile)
    {
        if(gameVo.getIsDevProject()==0) {
            this.devUtils.qspFileToText(gameVo.getGameFile(), toFile,gameVo.getQspPassword());
        }
    }
    @Override
    public void toGemFile(  GameVo gameVo, String toFile)
    {
        if(gameVo.getIsDevProject()==1) {
            devUtils.toGemFile(gameVo.getGameDevFolder(), gameVo.getGameQproj(), toFile);
        }
    }

    // endregion LibQspCallbacks
}
