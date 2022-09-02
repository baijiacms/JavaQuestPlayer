package com.qsp.player.libqsp;

/**
 * dll调用核心类
 * Методы данного класса определены в <code>androidqspwrapper.c</code>.
 * @author baijiacms
 */
public interface LibMethods {


    public void QSPInit();

    public void QSPDeInit();

    public boolean QSPIsInCallBack();

    public void QSPEnableDebugMode(boolean isDebug);

    public Object QSPGetCurStateData();//!!!STUB

    public String QSPGetVersion();

    public int QSPGetFullRefreshCount();

    public String QSPGetQstFullPath();

    public String QSPGetCurLoc();

    public String QSPGetMainDesc();

    public boolean QSPIsMainDescChanged();

    public String QSPGetVarsDesc();

    public boolean QSPIsVarsDescChanged();

    public Object QSPGetExprValue();//!!!STUB

    public void QSPSetInputStrText(String val);

    public int QSPGetActionsCount();

    public Object QSPGetActionData(int ind);//!!!STUB

    public boolean QSPExecuteSelActionCode(boolean isRefresh);

    public boolean QSPSetSelActionIndex(int ind, boolean isRefresh);

    public int QSPGetSelActionIndex();

    public boolean QSPIsActionsChanged();

    public int QSPGetObjectsCount();

    public Object QSPGetObjectData(int ind);//!!!STUB

    public boolean QSPSetSelObjectIndex(int ind, boolean isRefresh);

    public int QSPGetSelObjectIndex();

    public boolean QSPIsObjectsChanged();

    public void QSPShowWindow(int type, boolean isShow);

    public Object QSPGetVarValuesCount(String name);

    public Object QSPGetVarValues(String name, int ind);//!!!STUB

    public int QSPGetMaxVarsCount();

    public Object QSPGetVarNameByIndex(int index);//!!!STUB

    public boolean QSPExecString(String s, boolean isRefresh);

    public boolean QSPExecLocationCode(String name, boolean isRefresh);

    public boolean QSPExecCounter(boolean isRefresh);

    public boolean QSPExecUserInput(boolean isRefresh);

    public Object QSPGetLastErrorData();

    public String QSPGetErrorDesc(int errorNum);

    public boolean QSPLoadGameWorld(String fileName);

    public boolean QSPLoadGameWorldFromData(byte data[], int dataSize, String fileName);

    public boolean QSPSaveGame(String fileName, boolean isRefresh);

    public byte[] QSPSaveGameAsData(boolean isRefresh);

    public boolean QSPOpenSavedGame(String fileName, boolean isRefresh);

    public boolean QSPOpenSavedGameFromData(byte data[], int dataSize, boolean isRefresh);

    public boolean QSPRestartGame(boolean isRefresh);

    public void QSPSelectMenuItem(int index);
    //public void QSPSetCallBack(int type, QSP_CALLBACK func)
}
