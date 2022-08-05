package com.qsp.player;

import com.qsp.player.common.QspConstants;
import com.qsp.player.libqsp.QspGameStatus;
import com.qsp.player.common.WindowType;
import com.qsp.player.util.Base64Util;
import com.qsp.player.util.JarUtil;
import com.qsp.player.util.Uri;
import android.os.Handler;
import com.qsp.player.libqsp.service.AudioPlayer;
import com.qsp.player.libqsp.service.HtmlProcessor;
import com.qsp.player.vi.AudioInterface;
import com.qsp.player.vi.ViewInterface;
import com.qsp.player.libqsp.GameInterface;
import com.qsp.player.libqsp.LibQspProxy;
import com.qsp.player.libqsp.LibQspProxyImpl;
import com.qsp.player.libqsp.dto.QspListItem;
import com.qsp.player.libqsp.dto.QspMenuItem;
import com.qsp.player.libqsp.dto.RefreshInterfaceRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

public class GameShower implements GameInterface {
    private static final Logger logger = LoggerFactory.getLogger(GameShower.class);
    private QuestPlayerEngine mEngine;
    private ViewInterface viewInterface;
    public  GameShower(Class runnerClass, ViewInterface viewInterface, AudioInterface audioInterface)
    {
        this.viewInterface = viewInterface;
        QspConstants.setBaseFoler(JarUtil.getJarPath(runnerClass));
        this.mEngine =new QuestPlayerEngine(audioInterface);
        onCreate();
        //applyGameState();
    }
    private boolean showActions = true;
    private HtmlProcessor htmlProcessor;
    private LibQspProxy libQspProxy;
    private AudioPlayer audioPlayer;
    private final Handler counterHandler = new Handler();
    private int counterInterval = 500;
    private final Runnable counterTask = new Runnable() {
        @Override
        public void run() {
            libQspProxy.executeCounter();
            counterHandler.postDelayed(this, counterInterval);
        }
    };

    private void onCreate() {

        initServices();
        //initGame();

        logger.info("GameShower created");
    }
    public QuestPlayerEngine getEngine()
    {
        return mEngine;
    }
    private void initServices() {

        QuestPlayerEngine engine = getEngine();

        htmlProcessor = engine.getHtmlProcessor();

        audioPlayer = engine.getAudioPlayer();
        audioPlayer.start();

        libQspProxy = engine.getLibQspProxy();
        libQspProxy.setGameInterface(this);
        libQspProxy.start();
    }
    private void initGame() {

        String gameId = QspConstants.GAME_ID;
        String gameTitle = QspConstants.GAME_TITLE;

        String gameDirUri = QspConstants.GAME_RESOURCE_PATH;
        File gameDir = new File(gameDirUri);

        String gameFileUri = QspConstants.GAME_FILE;
        File gameFile = new File(gameFileUri);

        libQspProxy.getGameState().reset();
        libQspProxy.getGameState().gameId = gameId;
        libQspProxy.getGameState().gameTitle = gameTitle;
        libQspProxy.getGameState().gameDir = gameDir;
        libQspProxy.getGameState().gameFile = gameFile;
        //libQspProxy.runGame(gameId, gameTitle, gameDir, gameFile);
    }
    public void onItemClick(int position)
    {
        libQspProxy.onActionClicked(position);
    }
    public void onItemSelected(int position)
    {
        libQspProxy.onActionSelected(position);
    }
    public void onObjectSelected(int position)
    {
        libQspProxy.onObjectSelected(position);
    }
    @Override
    public void refresh(RefreshInterfaceRequest request) {

            if (request.interfaceConfigChanged || request.mainDescChanged) {

                refreshMainDesc();
                QspGameStatus.maindescchanged=true;
            }
            if (request.actionsChanged) {
                refreshActions();
                QspGameStatus.actionschanged=true;
            }
            if (request.objectsChanged) {
                refreshObjects();
                QspGameStatus.objectschanged=true;
            }
            if (request.interfaceConfigChanged || request.varsDescChanged) {
                refreshVarsDesc();
                QspGameStatus.varsdescchanged=true;
            }

    }

    @Override
    public void showError(String message) {
        logger.error("showError:"+message);
        viewInterface.showErrorBox(message);
    }

    @Override
    public void showPicture(String path) {
       // logger.info("gameDirUri:"+libQspProxy.getGameState().gameDir.getAbsolutePath());
        logger.info("imagePath:"+path);
        String newStr=path.toLowerCase();
        if (newStr.endsWith("webm") ||newStr.endsWith("mp4") || newStr.endsWith("mp3") )
        {
            if(newStr.startsWith("file://")==false) {
                path = path.replace(QspConstants.URL_REPLACE_URL, "");
                if(path.startsWith("/")==false)
                {
                    path="/"+path;
                }
                path= QspConstants.URL_BASE_URL +path;
            }
        }
        this.viewInterface.showPicture( path);
    }

    @Override
    public void showMessage(String message) {
        String processedMsg =  htmlProcessor.removeHtmlTags(message) ;
        if (processedMsg == null) {
            processedMsg = "";
        }
        this.viewInterface.showMessageBox(message);
    }

    @Override
    public String showInputBox(String prompt) {
        String message =  htmlProcessor.removeHtmlTags(prompt) ;
        if (message == null) {
            message = "";
        }
        logger.info("showInputBox:"+message);
        return viewInterface.getInputStr(message);
    }

    @Override
    public int showMenu() {
        final ArrayList<String> items = new ArrayList<>();

        for (QspMenuItem item : libQspProxy.getGameState().menuItems) {
            items.add(item.name);
        }
//        logger.info("showMenu");
        return -1;
    }
    @Override
    public String loadSaveGame(String filename) {
        if(StringUtils.isEmpty(filename))
        {
            filename= LibQspProxyImpl.quickSaveName;
        }
        String saveFolder=QspConstants.getSaveFolder();
        File saveFile=   new File(saveFolder+filename+".sav");
        if(saveFile.exists()==false)
        {
            return "0";
        }
        libQspProxy.loadGameState(Uri.fromFile(saveFile));
        QspGameStatus.refreshAll();
        return "1";
    }
    @Override
    public void deleteSaveGame(String filename) {
        if(StringUtils.isEmpty(filename))
        {
            return;
        }
        String saveFolder=QspConstants.getSaveFolder();
        logger.info(saveFolder+filename+".sav");
        new File(saveFolder+filename+".sav").delete();
        QspGameStatus.refreshAll();
    }
    @Override
    public void saveGame(String filename) {
        if(StringUtils.isEmpty(filename))
        {
            filename= LibQspProxyImpl.quickSaveName;
        }
        String saveFolder=QspConstants.getSaveFolder();
        libQspProxy.saveGameState(Uri.fromFile(new File(saveFolder+filename+".sav")));
        QspGameStatus.refreshAll();
    }

    @Override
    public void showWindow(WindowType type, boolean show) {
        if (type == WindowType.ACTIONS) {
            showActions = show;

        }
//        else {
//            logger.warn("Unsupported window type: " + type);
//        }
    }

    @Override
    public void setCounterInterval(int millis) {
        counterInterval = millis;
    }

    public void onDestroy() {
        audioPlayer.stop();
        libQspProxy.stop();
        libQspProxy.setGameInterface(null);
        counterHandler.removeCallbacks(counterTask);
        logger.info(" destroyed");
    }
    public void onPause() {
        audioPlayer.pause();
        counterHandler.removeCallbacks(counterTask);
    }

    public void onResume() {


        if (libQspProxy.getGameState().gameRunning) {
            applyGameState();

            audioPlayer.setSoundEnabled(true);
            audioPlayer.resume();

            counterHandler.postDelayed(counterTask, counterInterval);
        }
    }
    @Override
    public void doWithCounterDisabled(Runnable runnable) {
        counterHandler.removeCallbacks(counterTask);
        runnable.run();
        counterHandler.postDelayed(counterTask, counterInterval);
    }



    public void applyGameState() {
        refreshMainDesc();
        refreshVarsDesc();
        refreshActions();
        refreshObjects();
    }


    public String refreshMainDesc() {
        String mainDesc = getHtml(libQspProxy.getGameState().mainDesc,true);

        return mainDesc;
    }

    private String getHtml(String str,boolean isMainDesc) {

        return true ?
                htmlProcessor.convertQspHtmlToWebViewHtml(str,isMainDesc) :
                htmlProcessor.convertQspStringToWebViewHtml(str);
    }

    public void  restartGame() {
        //libQspProxy.stop();
        //libQspProxy.start();
        initGame();
        libQspProxy.restartGame();
        applyGameState();
    }

    public String  refreshVarsDesc() {
        String varsDesc = getHtml(libQspProxy.getGameState().varsDesc,false);
//        logger.info("varsDesc:"+varsDesc);
        return varsDesc;
    }

    public ArrayList<QspListItem>  refreshActions() {
        ArrayList<QspListItem> actions = libQspProxy.getGameState().actions;
        //logger.info("refreshActions:"+actions.size());
        return actions;
    }

    public ArrayList<QspListItem>  refreshObjects() {
        ArrayList<QspListItem> objects = libQspProxy.getGameState().objects;
        // logger.info("refreshObjects:"+objects.size());
        return objects;
    }

    public LibQspProxy getLibQspProxy() {
        return libQspProxy;
    }

    public boolean shouldOverrideUrlLoading(final String href) {
            if (href.toLowerCase().startsWith("exec:")) {
                String code = Base64Util.decodeBase64(href.substring(5));
                libQspProxy.execute(code);
            }
            return true;
      }




}
