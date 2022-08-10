package com.qsp;

import com.qsp.player.javafx.JavaFxMediaPlayer;
import com.qsp.player.javafx.JavaFxViewImpl;
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
    public QspEngineCore(String userId) {
        this(userId,new JavaFxViewImpl(), new JavaFxMediaPlayer());
    }

    public QspEngineCore(String userId,ViewInterface qspViewImpl, AudioInterface audioInterfaceImp) {
        this.userId=userId;
        javaFxViewImpl = qspViewImpl;
        javaFxMediaPlayer = audioInterfaceImp;
        htmlEngine = new HtmlEngine(userId, this);
    }

    public ResponseVo getInputStream( URL url, String target) throws IOException {

        return htmlEngine.getInputStream(url, target);
    }

    public ViewInterface getJavaFxViewImpl() {
        return javaFxViewImpl;
    }

    public AudioInterface getJavaFxMediaPlayer() {
        return javaFxMediaPlayer;
    }
}
