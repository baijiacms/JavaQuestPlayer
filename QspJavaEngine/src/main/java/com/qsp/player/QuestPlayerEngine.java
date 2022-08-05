package com.qsp.player;


import com.qsp.player.libqsp.service.AudioPlayer;
import com.qsp.player.libqsp.service.GameContentResolver;
import com.qsp.player.libqsp.service.HtmlProcessor;
import com.qsp.player.vi.AudioInterface;
import com.qsp.player.libqsp.LibQspProxy;
import com.qsp.player.libqsp.LibQspProxyImpl;


public class QuestPlayerEngine {
    private final GameContentResolver gameContentResolver = new GameContentResolver();
    private final HtmlProcessor htmlProcessor = new HtmlProcessor(gameContentResolver);
    private final AudioPlayer audioPlayer ;
    private final LibQspProxyImpl libQspProxy ;

    public QuestPlayerEngine(AudioInterface audioInterface) {
        audioPlayer = new AudioPlayer(audioInterface);
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
