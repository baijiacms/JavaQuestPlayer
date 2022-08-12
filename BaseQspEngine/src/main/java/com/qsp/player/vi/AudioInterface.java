package com.qsp.player.vi;

import com.qsp.player.GameStatus;

/**
 * 音乐播放器的接口类
 */
public interface AudioInterface {
    AudioInterface createNewPlayer(GameStatus gameStatus, String fileName);

    public void stop();

    public void setVolume(float sysVolume);

    public void start();

    boolean isPlaying();

    public void release();

    public void pause();
}
