package com.qsp.player.libqsp;


import com.qsp.player.common.QspConstants;

import java.util.Objects;

/**
 * dll调用核心类
 * Методы данного класса определены в <code>androidqspwrapper.c</code>.
 * @author baijiacms
 */
public class NativeMethods implements LibMethods {
    private final LibQspCallbacks callbacks;

    static {
        // System.loadLibrary("ndkqsp");
        System.load(QspConstants.getLibQspPath());
    }

    public NativeMethods(LibQspCallbacks callbacks) {
        this.callbacks = Objects.requireNonNull(callbacks, "callbacks");
    }

    @Override
    public native void QSPInit();

    @Override
    public native void QSPDeInit();

    @Override
    public native boolean QSPIsInCallBack();

    @Override
    public native void QSPEnableDebugMode(boolean isDebug);

    @Override
    public native Object QSPGetCurStateData();//!!!STUB

    @Override
    public native String QSPGetVersion();

    @Override
    public native int QSPGetFullRefreshCount();

    @Override
    public native String QSPGetQstFullPath();

    @Override
    public native String QSPGetCurLoc();

    @Override
    public native String QSPGetMainDesc();

    @Override
    public native boolean QSPIsMainDescChanged();

    @Override
    public native String QSPGetVarsDesc();

    @Override
    public native boolean QSPIsVarsDescChanged();

    @Override
    public native Object QSPGetExprValue();//!!!STUB

    @Override
    public native void QSPSetInputStrText(String val);

    @Override
    public native int QSPGetActionsCount();

    @Override
    public native Object QSPGetActionData(int ind);//!!!STUB

    @Override
    public native boolean QSPExecuteSelActionCode(boolean isRefresh);

    @Override
    public native boolean QSPSetSelActionIndex(int ind, boolean isRefresh);

    @Override
    public native int QSPGetSelActionIndex();

    @Override
    public native boolean QSPIsActionsChanged();

    @Override
    public native int QSPGetObjectsCount();

    @Override
    public native Object QSPGetObjectData(int ind);//!!!STUB

    @Override
    public native boolean QSPSetSelObjectIndex(int ind, boolean isRefresh);

    @Override
    public native int QSPGetSelObjectIndex();

    @Override
    public native boolean QSPIsObjectsChanged();

    @Override
    public native void QSPShowWindow(int type, boolean isShow);

    @Override
    public native Object QSPGetVarValuesCount(String name);

    @Override
    public native Object QSPGetVarValues(String name, int ind);//!!!STUB

    @Override
    public native int QSPGetMaxVarsCount();

    @Override
    public native Object QSPGetVarNameByIndex(int index);//!!!STUB

    @Override
    public native boolean QSPExecString(String s, boolean isRefresh);

    @Override
    public native boolean QSPExecLocationCode(String name, boolean isRefresh);

    @Override
    public native boolean QSPExecCounter(boolean isRefresh);

    @Override
    public native boolean QSPExecUserInput(boolean isRefresh);

    @Override
    public native Object QSPGetLastErrorData();

    @Override
    public native String QSPGetErrorDesc(int errorNum);

    @Override
    public native boolean QSPLoadGameWorld(String fileName);

    @Override
    public native boolean QSPLoadGameWorldFromData(byte data[], int dataSize, String fileName);

    @Override
    public native boolean QSPSaveGame(String fileName, boolean isRefresh);

    @Override
    public native byte[] QSPSaveGameAsData(boolean isRefresh);

    @Override
    public native boolean QSPOpenSavedGame(String fileName, boolean isRefresh);

    @Override
    public native boolean QSPOpenSavedGameFromData(byte data[], int dataSize, boolean isRefresh);

    @Override
    public native boolean QSPRestartGame(boolean isRefresh);

    @Override
    public native void QSPSelectMenuItem(int index);
    //public native void QSPSetCallBack(int type, QSP_CALLBACK func)
}
