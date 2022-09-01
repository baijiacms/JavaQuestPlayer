package com.qsp.player.vi.audio.impl;


import com.qsp.player.util.QspUri;
import com.qsp.player.vi.audio.QspAudio;
import com.qsp.player.vi.audio.impl.util.MP3Player;

import java.io.File;

public class SwingAudio implements QspAudio {
    SwingAudio audioInterface;
    private MP3Player mediaPlayer;

    public SwingAudio() {
    }

    private SwingAudio(String gameResourcePath, String fileName, float volume) {
        String filePath = QspUri.getFilePath(gameResourcePath, fileName);
        File mediaFile = new File(filePath);
        if (mediaFile.exists() == false) {
            mediaFile = new File("file:///" + filePath);
        }
        if (mediaFile.exists()) {

            try {
                this.mediaPlayer = new MP3Player(mediaFile);

                mediaPlayer.getVolumeControl().setValue(volume);
                this.mediaPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playFile(String gameResourcePath, String path, int volume) {
        closeAllFiles();
        audioInterface = new SwingAudio(gameResourcePath, path, Float.parseFloat(String.valueOf(volume)));
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

    public void setVolume(float sysVolume) {
        if (mediaPlayer != null && mediaPlayer.getVolumeControl() != null) {
            mediaPlayer.getVolumeControl().setValue(sysVolume);
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.getPlayerStatus() == MP3Player.STATUS_PLAYING;
        } else {
            return false;
        }
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = null;
    }
}
