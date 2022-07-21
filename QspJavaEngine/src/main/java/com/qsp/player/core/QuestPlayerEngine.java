package com.qsp.player.core;


import com.qsp.player.core.game.service.AudioPlayer;
import com.qsp.player.core.game.service.GameContentResolver;
import com.qsp.player.core.game.service.HtmlProcessor;
import com.qsp.player.libqsp.LibQspProxy;
import com.qsp.player.libqsp.LibQspProxyImpl;


public class QuestPlayerEngine {
    private final GameContentResolver gameContentResolver = new GameContentResolver();
    private final HtmlProcessor htmlProcessor = new HtmlProcessor(gameContentResolver);
    private final AudioPlayer audioPlayer ;
    private final LibQspProxyImpl libQspProxy ;

    public QuestPlayerEngine(QspAudioMediaPlayer qspAudioMediaPlayer) {
        audioPlayer = new AudioPlayer(qspAudioMediaPlayer);
        libQspProxy = new LibQspProxyImpl(gameContentResolver,  htmlProcessor, audioPlayer);
    }


    public HtmlProcessor getHtmlProcessor() {
        return htmlProcessor;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public LibQspProxy getLibQspProxy() {
        return libQspProxy;
    }
}
