package com.qsp.view.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author baijiacms
 */
public class UrlContants {
    public static final String ENGINE_URL_ROOT = "/engine/";
    public static final String ENGINE_LIB_URL_ROOT = "/engine/lib/";
    public static final String SOB_LIB_URL_ROOT = "/engine/lib/sob/";
    public static final String BIGKUYASH_LIB_URL_ROOT = "/engine/lib/bigKuyash/";
    public static final String ACTION_URL_ROOT = "/engine/action/";
    public static final String CONSOLE_URL_ROOT = "/engine/console/";
    public static final String GAME_SAVE_URL_ROOT = "/engine/save/";
    public static final String GAME_SELECT_URL_ROOT = "/engine/main/";
    public static final String HTML_URL_ROOT = "/engine/html/";
    public static final String INDEX_URL_ROOT = "/engine/index/";
    public static final String LOADING_URL_ROOT = "/engine/loading/";
    public static final String USER_URL_ROOT = "/engine/user/";
    public static final List<String> EXCLUDE_INDEX_URL= Arrays.asList(new String[]{INDEX_URL_ROOT + "exportGameToText",INDEX_URL_ROOT + "exportGameToQsp",INDEX_URL_ROOT + "loadGame"});
    public static final int IS_SESSION = 1;
    public static final int IS_NEW_SESSION = 2;
    public static final int IS_NOT_SESSION = 0;
    public static final int IS_UNKNOW = -1;
    public static final int IS_NOT_ACCESS = -2;


    public static int isSessionPath(String target) {

        switch (target) {
            case INDEX_URL_ROOT + "isNeedRefresh":
                return IS_SESSION;
            case INDEX_URL_ROOT + "isNeedRefreshHtml":
                return IS_SESSION;
            case INDEX_URL_ROOT + "isNeedRefreshAction":
                return IS_SESSION;
            case INDEX_URL_ROOT + "isNeedRefreshUser":
                return IS_SESSION;
            case INDEX_URL_ROOT + "isNeedRefreshConsole":
                return IS_SESSION;
            case HTML_URL_ROOT + "engineHtmlPage":
                return IS_SESSION;
            case USER_URL_ROOT + "engineUserPage":
                return IS_SESSION;
            case CONSOLE_URL_ROOT + "engineConsolePage":
                return IS_SESSION;
            case ACTION_URL_ROOT + "engineActionPage":
                return IS_SESSION;
            case HTML_URL_ROOT + "html":
                return IS_SESSION;
            case USER_URL_ROOT + "html":
                return IS_SESSION;
            case CONSOLE_URL_ROOT + "html":
                return IS_SESSION;
            case ACTION_URL_ROOT + "html":
                return IS_SESSION;
            case HTML_URL_ROOT + "htmlCall":
                return IS_SESSION;
            case USER_URL_ROOT + "userCall":
                return IS_SESSION;
            case ACTION_URL_ROOT + "actionCall":
                return IS_SESSION;
            case CONSOLE_URL_ROOT + "consoleCall":
                return IS_SESSION;
            case GAME_SAVE_URL_ROOT + "openSaveWindow":
                return IS_SESSION;
            case GAME_SAVE_URL_ROOT + "closeSaveWindow":
                return IS_SESSION;
            case GAME_SAVE_URL_ROOT + "deleteGameSave":
                return IS_SESSION;
            case GAME_SAVE_URL_ROOT + "GameSave":
                return IS_SESSION;
            case GAME_SAVE_URL_ROOT + "LoadGameSave":
                return IS_SESSION;
            case GAME_SAVE_URL_ROOT + "QuickSave":
                return IS_SESSION;
            case GAME_SAVE_URL_ROOT + "loadQuickSave":
                return IS_SESSION;
            case INDEX_URL_ROOT + "gameIndex":
                return IS_SESSION;
            case INDEX_URL_ROOT + "exportGameToText":
                return IS_SESSION;
            case INDEX_URL_ROOT + "exportGameToQsp":
                return IS_SESSION;
            case INDEX_URL_ROOT + "loadGame":
                return IS_NEW_SESSION;
            case "/":
            case GAME_SELECT_URL_ROOT + "index.html":
            case GAME_SELECT_URL_ROOT + "blank.html":
            case GAME_SELECT_URL_ROOT + "exportGameToText":
            case GAME_SELECT_URL_ROOT + "exportGameToQsp":
                return IS_NOT_SESSION;
            case LOADING_URL_ROOT + "loadingPage":
                return IS_NOT_SESSION;
            default:
                if (target.startsWith(ENGINE_LIB_URL_ROOT)) {
                    return IS_NOT_SESSION;
                }
        }
        return IS_UNKNOW;
    }

}
