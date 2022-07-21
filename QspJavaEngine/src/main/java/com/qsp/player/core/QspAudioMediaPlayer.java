package com.qsp.player.core;

/**
 * 音乐播放器的接口类
 */
public interface QspAudioMediaPlayer {
    QspAudioMediaPlayer createNewPlayer(String fileName);
    public void stop();
    public void setVolume(float sysVolume);
    public void start();
    boolean isPlaying();
    public void release();
    public void pause();
}
