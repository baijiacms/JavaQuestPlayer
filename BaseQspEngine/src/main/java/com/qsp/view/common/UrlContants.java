package com.qsp.view.common;

/**
 * @author baijiacms
 */
public class UrlContants {
    public static final String URL_ROOT = "/";
    public static final String ENGINE_URL_ROOT = "/engine/";
    public static final String ENGINE_LIB_URL_ROOT = ENGINE_URL_ROOT+"lib/";
    public static final String SOB_LIB_URL_ROOT = ENGINE_URL_ROOT+"lib/sob/";
    public static final String BIGKUYASH_LIB_URL_ROOT = ENGINE_URL_ROOT+"lib/bigKuyash/";
    public static final String ACTION_URL_ROOT = ENGINE_URL_ROOT+"action/";
    public static final String CONSOLE_URL_ROOT = ENGINE_URL_ROOT+"console/";
    public static final String GAME_SAVE_URL_ROOT = ENGINE_URL_ROOT+"save/";
    public static final String GAME_SELECT_URL_ROOT = ENGINE_URL_ROOT+"main/";
    public static final String HTML_URL_ROOT = ENGINE_URL_ROOT+"html/";
    public static final String INDEX_URL_ROOT = ENGINE_URL_ROOT+"index/";
    public static final String LOADING_URL_ROOT = ENGINE_URL_ROOT+"loading/";
    public static final String USER_URL_ROOT = ENGINE_URL_ROOT+"user/";
    public static final int IS_SESSION = 1;
    public static final int IS_NOT_SESSION = 0;
    public static final int IS_UN_KNOW = -1;


    public static int isSessionPath(String target) {

        switch (target) {
            case INDEX_URL_ROOT + "isNeedRefresh":
            case INDEX_URL_ROOT + "isNeedRefreshHtml":
            case INDEX_URL_ROOT + "isNeedRefreshAction":
            case INDEX_URL_ROOT + "isNeedRefreshUser":
            case INDEX_URL_ROOT + "isNeedRefreshConsole":
            case HTML_URL_ROOT + "engineHtmlPage":
            case USER_URL_ROOT + "engineUserPage":
            case CONSOLE_URL_ROOT + "engineConsolePage":
            case ACTION_URL_ROOT + "engineActionPage":
            case HTML_URL_ROOT + "html":
            case USER_URL_ROOT + "html":
            case CONSOLE_URL_ROOT + "html":
            case ACTION_URL_ROOT + "html":
            case HTML_URL_ROOT + "htmlCall":
            case USER_URL_ROOT + "userCall":
            case ACTION_URL_ROOT + "actionCall":
            case CONSOLE_URL_ROOT + "consoleCall":
            case GAME_SAVE_URL_ROOT + "openSaveWindow":
            case GAME_SAVE_URL_ROOT + "closeSaveWindow":
            case GAME_SAVE_URL_ROOT + "deleteGameSave":
            case GAME_SAVE_URL_ROOT + "GameSave":
            case GAME_SAVE_URL_ROOT + "LoadGameSave":
            case GAME_SAVE_URL_ROOT + "QuickSave":
            case GAME_SAVE_URL_ROOT + "loadQuickSave":
            case INDEX_URL_ROOT + "gameIndex":
                return IS_SESSION;
            case URL_ROOT:
            case GAME_SELECT_URL_ROOT + "index.html":
            case GAME_SELECT_URL_ROOT + "blank.html":
            case GAME_SELECT_URL_ROOT + "exportGameToText":
            case GAME_SELECT_URL_ROOT + "exportGameToQsp":
            case GAME_SELECT_URL_ROOT + "loadGame":
            case LOADING_URL_ROOT + "loadingPage":
                return IS_NOT_SESSION;
            default:
                if (target.startsWith(ENGINE_LIB_URL_ROOT)) {
                    return IS_NOT_SESSION;
                }
        }
        return IS_UN_KNOW;
    }

}
