package com.qsp;

import com.qsp.player.javafx.JavaFxMediaPlayer;
import com.qsp.player.javafx.JavaFxViewImpl;
import com.qsp.player.vi.AudioInterface;
import com.qsp.player.vi.ViewInterface;
import com.qsp.webengine.HtmlEngine;
import com.qsp.webengine.vo.ResponseVo;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class QspEngineCore {
    private HtmlEngine htmlEngine;
    private Map<String,HtmlEngine> htmlEngineMap=new HashMap<>();
    private ViewInterface javaFxViewImpl;
    private AudioInterface javaFxMediaPlayer;
    public QspEngineCore()
    {
        this(new JavaFxViewImpl(), new JavaFxMediaPlayer());
    }
    public QspEngineCore(ViewInterface qspViewImpl, AudioInterface audioInterfaceImp) {
        javaFxViewImpl=qspViewImpl;
        javaFxMediaPlayer=audioInterfaceImp;
    }

    public ResponseVo getInputStream(String userId,URL url, String target) throws IOException {
        htmlEngine=htmlEngineMap.get(userId);
        if(htmlEngine==null) {
            htmlEngine = new HtmlEngine(userId,javaFxViewImpl, javaFxMediaPlayer);
            htmlEngineMap.put(userId,htmlEngine);
        }
        return htmlEngine.getInputStream(url, target);
    }

}
