package com.qsp.player;

import com.qsp.player.common.QspConstants;
import com.qsp.player.libqsp.DevMethodsHelper;
import com.qsp.player.vi.QspAudio;
import com.qsp.player.vi.QspUi;
import com.qsp.player.vi.audio.SwingAudio;
import com.qsp.player.vi.audio.WebAudio;
import com.qsp.player.vi.box.SwingUi;
import com.qsp.player.vi.box.WebUi;

/**
 * @author：ChenXingYu
 * @date 2023/8/4 15:55
 */
public class Engine {
    public static final Engine INSTANCEOF=new Engine(false);
    private DevMethodsHelper devMethodsHelper;
    private LibEngine libEngine;
    private QspUi qspUi;
    private QspAudio qspAudio;
    private Engine(boolean isGUI)
    {
        if (isGUI) {
            qspUi = new SwingUi();
            qspAudio = new SwingAudio();
        } else {
            qspUi = new WebUi();
            qspAudio = new WebAudio();
        }
        libEngine = new LibEngine(QspConstants.DEFAULT_USER, qspUi, qspAudio);
        devMethodsHelper=new DevMethodsHelper();
    }

    public DevMethodsHelper getDevMethodsHelper() {
        return devMethodsHelper;
    }

    public LibEngine getLibEngine() {
        return libEngine;
    }
}
