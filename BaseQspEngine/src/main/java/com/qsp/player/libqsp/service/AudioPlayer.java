package com.qsp.player.libqsp.service;


import com.qsp.player.GameStatus;
import com.qsp.player.vi.AudioInterface;

public class AudioPlayer {
    private AudioInterface audioFactory;
    private AudioInterface audioInterface;

    public AudioPlayer(AudioInterface audioFactory) {
        this.audioFactory = audioFactory;
    }

    public void playFile(GameStatus gameStatus, String path, int volume) {
        closeAllFiles();
        audioInterface = audioFactory.createNewPlayer(gameStatus, path,Double.parseDouble(String.valueOf(volume)));
        audioInterface.setVolume(volume);
        audioInterface.start();
    }

    public void closeFile(String path) {
        if (audioInterface != null) {
            audioInterface.release();
        }
    }

    public void closeAllFiles() {
        if (audioInterface != null) {
            audioInterface.release();
        }
    }

    public boolean isPlayingFile(final String path) {

        if (audioInterface != null) {
            return audioInterface.isPlaying();
        } else {
            return false;
        }
    }
}
