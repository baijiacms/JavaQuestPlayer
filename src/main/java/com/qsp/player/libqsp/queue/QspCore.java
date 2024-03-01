package com.qsp.player.libqsp.queue;

import com.qsp.player.libqsp.*;
import com.qsp.player.libqsp.dto.ErrorData;
import com.qsp.player.libqsp.entity.QspGame;
import com.qsp.player.libqsp.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author：ChenXingYu
 * @date 2024/3/1 8:52
 */
public class QspCore {

    private static final Logger logger = LoggerFactory.getLogger(QspCore.class);
    public static final ConcurrentMap<String, String> concurrentStringMap = new ConcurrentHashMap<>();
    public static final ConcurrentMap<String, Boolean> concurrentBooleanMap = new ConcurrentHashMap<>();
    public static final ConcurrentMap<String, ArrayList> concurrentArrayListMap = new ConcurrentHashMap<>();
    public static final ConcurrentMap<String, Long> concurrentLongMap = new ConcurrentHashMap<>();
    public  static QspGame currentQspGame=null;
    public static DevMethodsHelper devMethodsHelper;
    public static AtomicBoolean maindescchanged = new AtomicBoolean(false);
    public static AtomicBoolean actionschanged = new AtomicBoolean(false);
    public static AtomicBoolean objectschanged = new AtomicBoolean(false);
    public static AtomicBoolean varsdescchanged = new AtomicBoolean(false);
    private  LibQspProxyImpl libQspProxy;
    private   LibMethods libMethods;
    private  LibDevMethods nativeDevMethods;

    public static  void refreshAll() {
        maindescchanged.set(true);
        actionschanged.set(true);
        objectschanged.set(true);
        varsdescchanged.set(true);
    }

    public  void executeTask(QspTask qspTask)
    {
            switch (QspAction.getByCode(qspTask.action)) {
                case init:
                    LibQspCallbacks callbacks = new LibQspCallBacksImpl(libMethods);
                    libMethods = new NativeMethods(callbacks);
                    libQspProxy=new LibQspProxyImpl(libMethods);
                    libQspProxy.start();
                    nativeDevMethods = new NativeDevMethods();
                    devMethodsHelper=new DevMethodsHelper(nativeDevMethods);
                    concurrentArrayListMap.put(QspConstants.ACTIONS,new ArrayList());
                    concurrentArrayListMap.put(QspConstants.OBJECTS,new ArrayList());
                    concurrentArrayListMap.put(QspConstants.MENU_ITEMS,new ArrayList());

                    concurrentStringMap.put(QspConstants.MAIN_DESC,"");
                    concurrentStringMap.put(QspConstants.VARS_DESC,"");
                    break;
                case execute:
                    String actionScript = qspTask.paramString;
                    if (actionScript.toLowerCase().startsWith("exec:")) {
                        String code = Base64Util.decodeBase64(actionScript.substring(5));
                        if (!libMethods.QSPExecString(code, true)) {
                            showLastQspError();
                        }
                    }
                    break;
                case onActionClicked:
                    if (!libMethods.QSPSetSelActionIndex(qspTask.paramInt, false)) {
                        showLastQspError();
                    }
                    if (!libMethods.QSPExecuteSelActionCode(true)) {
                        showLastQspError();
                    }
                    break;
                case setSelObject:
                    if (!libMethods.QSPSetSelObjectIndex(qspTask.paramInt, true)) {
                        showLastQspError();
                    }
                    break;
                case initGame:
                    libQspProxy.initGame(qspTask.qspGame);
                    break;
                case loadGame:
                    libQspProxy.loadGameState(qspTask.qspUri);
                    break;
                case saveGame:
                    libQspProxy.saveGameState(qspTask.qspUri);
                    break;
                case refresh:
                    libQspProxy.getRefreshInterfaceRequest();
                    break;

            }
            libQspProxy.getRefreshInterfaceRequest();

    }
    private  void showLastQspError() {
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
