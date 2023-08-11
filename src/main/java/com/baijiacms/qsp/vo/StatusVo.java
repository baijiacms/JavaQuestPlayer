package com.baijiacms.qsp.vo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author：ChenXingYu
 * @date 2023/8/4 16:18
 */
public class StatusVo {
    private boolean maindescchanged;
    private boolean actionschanged ;
    private boolean objectschanged ;
    private boolean varsdescchanged;

    public boolean isMaindescchanged() {
        return maindescchanged;
    }

    public void setMaindescchanged(boolean maindescchanged) {
        this.maindescchanged = maindescchanged;
    }

    public boolean isActionschanged() {
        return actionschanged;
    }

    public void setActionschanged(boolean actionschanged) {
        this.actionschanged = actionschanged;
    }

    public boolean isObjectschanged() {
        return objectschanged;
    }

    public void setObjectschanged(boolean objectschanged) {
        this.objectschanged = objectschanged;
    }

    public boolean isVarsdescchanged() {
        return varsdescchanged;
    }

    public void setVarsdescchanged(boolean varsdescchanged) {
        this.varsdescchanged = varsdescchanged;
    }

}
