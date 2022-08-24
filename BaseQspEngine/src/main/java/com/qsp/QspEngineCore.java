package com.qsp;

import com.qsp.player.vi.impl.MediaPlayer;
import com.qsp.player.vi.impl.SwingViewImpl;
import com.qsp.player.vi.AudioInterface;
import com.qsp.player.vi.ViewInterface;
import com.qsp.webengine.HtmlEngine;
import com.qsp.webengine.vo.ResponseVo;

import java.io.IOException;
import java.net.URL;

public class QspEngineCore {
    private HtmlEngine htmlEngine;
    private ViewInterface javaFxViewImpl;
    private AudioInterface javaFxMediaPlayer;
    private String userId;

    public QspEngineCore() {
        this( new SwingViewImpl(), new MediaPlayer());
    }

    public QspEngineCore(ViewInterface qspViewImpl, AudioInterface audioInterfaceImp) {
        this.userId = userId;
        javaFxViewImpl = qspViewImpl;
        javaFxMediaPlayer = audioInterfaceImp;
        htmlEngine = new HtmlEngine(this);
    }

    public ResponseVo getInputStream(URL url, String target) throws IOException {

        return htmlEngine.getInputStream(url, target);
    }

    public ViewInterface getJavaFxViewImpl() {
        return javaFxViewImpl;
    }

    public AudioInterface getJavaFxMediaPlayer() {
        return javaFxMediaPlayer;
    }
}
