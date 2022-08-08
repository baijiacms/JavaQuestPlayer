package com.qsp.player.libqsp.service;



import com.qsp.player.vi.AudioInterface;
import com.qsp.player.common.QspConstants;
import com.qsp.player.util.StringUtil;
import com.qsp.player.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class AudioPlayer {
    public AudioPlayer(AudioInterface audioInterface)
    {}
    public void playFile(String path, int volume) {

    }

    public boolean IsPlayingFile(final String path){
        return true;
    }

    public void closeFile(String path)
    {

    }
    public void closeAllFiles()
    {

    }

    public boolean isPlayingFile(final String path)
    {
        return true;
    }
}
