package com.qsp.player.vi;

/**
 * 音乐播放器的接口类
 */
public interface AudioInterface {
    AudioInterface createNewPlayer(String fileName);

    public void stop();

    public void setVolume(float sysVolume);

    public void start();

    boolean isPlaying();

    public void release();

    public void pause();
}
