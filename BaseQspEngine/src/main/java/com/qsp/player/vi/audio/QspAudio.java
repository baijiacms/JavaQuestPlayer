package com.qsp.player.vi.audio;

public interface QspAudio {
    void playFile(String gameResourcePath, String path, int volume);

    void closeFile(String path);

    void closeAllFiles();

    boolean isPlayingFile(final String path);

    void setVolume(float sysVolume);

    void pause();

    void start();

    boolean isPlaying();

    void stop();

    void release();
}
