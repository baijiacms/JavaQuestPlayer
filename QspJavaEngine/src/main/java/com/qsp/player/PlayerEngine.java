package com.qsp.player;

import com.qsp.player.common.QspConstants;
import com.qsp.player.common.WindowType;
import com.qsp.player.libqsp.service.AudioPlayer;
import com.qsp.player.libqsp.service.GameContentResolver;
import com.qsp.player.util.Base64Util;
import com.qsp.player.util.JarUtil;
import com.qsp.player.util.Uri;
//import android.os.Handler;
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

public class PlayerEngine implements GameInterface {
    private static final Logger logger = LoggerFactory.getLogger(PlayerEngine.class);
    private  GameContentResolver gameContentResolver ;
    private  HtmlProcessor htmlProcessor ;
    private LibQspProxy libQspProxy;
    private ViewInterface viewInterface;
    private  AudioPlayer audioPlayer ;
    private GameStatus gameStatus;
    private String userId;
    public PlayerEngine(String userId,Class runnerClass, ViewInterface viewInterface, AudioInterface audioInterface)
    {
        this.userId=userId;
        this.viewInterface = viewInterface;
        gameStatus=new GameStatus();
        gameContentResolver = new GameContentResolver();
        htmlProcessor = new HtmlProcessor(gameContentResolver);

        audioPlayer = new AudioPlayer(audioInterface);
        QspConstants.setBaseFoler(JarUtil.getJarPath(runnerClass));
        libQspProxy = new LibQspProxyImpl(userId,gameStatus,gameContentResolver,  htmlProcessor, audioPlayer);

        libQspProxy.setGameInterface(this);
        libQspProxy.start();
        logger.info("qsp engine created");
    }


    private void initGame() {

        String gameId = gameStatus.GAME_ID;
        String gameTitle = gameStatus.GAME_TITLE;

        String gameDirUri = gameStatus.GAME_RESOURCE_PATH;
        File gameDir = new File(gameDirUri);

        String gameFileUri = gameStatus.GAME_FILE;
        File gameFile = new File(gameFileUri);

        libQspProxy.getGameObject().reset();
        libQspProxy.getGameObject().gameId = gameId;
        libQspProxy.getGameObject().gameTitle = gameTitle;
        libQspProxy.getGameObject().gameDir = gameDir;
        libQspProxy.getGameObject().gameFile = gameFile;
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

            if ( request.mainDescChanged) {

                refreshMainDesc();
                gameStatus.isMaindescchanged(true);
            }
            if (request.actionsChanged) {
                refreshActions();
                gameStatus.setActionschanged(true);
            }
            if (request.objectsChanged) {
                refreshObjects();
                gameStatus.setObjectschanged(true);
            }
            if ( request.varsDescChanged) {
                refreshVarsDesc();
                gameStatus.setVarsdescchanged(true);
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
                path = path.replace(gameStatus.URL_REPLACE_URL, "");
                if(path.startsWith("/")==false)
                {
                    path="/"+path;
                }
                path= gameStatus.URL_BASE_URL +path;
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
        return  viewInterface.getInputStr(message);
    }

    @Override
    public int showMenu() {
        final ArrayList<String> items = new ArrayList<>();

        for (QspMenuItem item : libQspProxy.getGameObject().menuItems) {
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
        if(filename.endsWith(".sav")==false)
        {
            filename=filename+".sav";
        }
        String saveFolder=gameStatus.getSaveFolder();
        File saveFile=   new File(saveFolder+filename);
        if(saveFile.exists()==false)
        {
            return "0";
        }
        libQspProxy.loadGameState(Uri.fromFile(saveFile));
        gameStatus.refreshAll();
        return "1";
    }
    @Override
    public void deleteSaveGame(String filename) {
        if(StringUtils.isEmpty(filename))
        {
            return;
        }

        if(filename.endsWith(".sav")==false)
        {
            filename=filename+".sav";
        }
        String saveFolder=gameStatus.getSaveFolder();
        logger.info(saveFolder+filename);
        new File(saveFolder+filename).delete();
        gameStatus.refreshAll();
    }
    @Override
    public void saveGame(String filename) {
        if(StringUtils.isEmpty(filename))
        {
            filename= LibQspProxyImpl.quickSaveName;
        }

        if(filename.endsWith(".sav")==false)
        {
            filename=filename+".sav";
        }
        String saveFolder=gameStatus.getSaveFolder();
        libQspProxy.saveGameState(Uri.fromFile(new File(saveFolder+filename)));
        gameStatus.refreshAll();
    }

    @Override
    public void showWindow(WindowType type, boolean show) {
        if (type == WindowType.ACTIONS) {
        }
    }

    @Override
    public void setCounterInterval(int millis) {

    }

    public void onDestroy() {
        libQspProxy.stop();
        libQspProxy.setGameInterface(null);
        logger.info(" destroyed");
    }
    public void onPause() {
    }

    public void onResume() {


        if (libQspProxy.getGameObject().gameRunning) {
            applyGameState();


        }
    }



    public void applyGameState() {
        refreshMainDesc();
        refreshVarsDesc();
        refreshActions();
        refreshObjects();
    }


    public String refreshMainDesc() {
        String mainDesc = getHtml(libQspProxy.getGameObject().mainDesc,true);

        return mainDesc;
    }

    private String getHtml(String str,boolean isMainDesc) {

        return true ?
                htmlProcessor.convertQspHtmlToWebViewHtml(this,str,isMainDesc) :
                htmlProcessor.convertQspStringToWebViewHtml(str);
    }

    public void  restartGame(String gameId) {

        gameStatus.isStart = true;
        gameStatus.IS_SOB_GAME=false;
        gameStatus.IS_BIG_KUYASH=false;
        gameStatus.setGamePathById(gameId);
        initGame();
        libQspProxy.restartGame();
        applyGameState();
    }

    public String  refreshVarsDesc() {
        String varsDesc = getHtml(libQspProxy.getGameObject().varsDesc,false);
//        logger.info("varsDesc:"+varsDesc);
        return varsDesc;
    }

    public ArrayList<QspListItem>  refreshActions() {
        ArrayList<QspListItem> actions = libQspProxy.getGameObject().actions;
        //logger.info("refreshActions:"+actions.size());
        return actions;
    }

    public ArrayList<QspListItem>  refreshObjects() {
        ArrayList<QspListItem> objects = libQspProxy.getGameObject().objects;
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


    public GameStatus getGameStatus() {
        return gameStatus;
    }
}