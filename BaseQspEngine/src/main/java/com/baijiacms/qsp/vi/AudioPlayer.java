package com.baijiacms.qsp.vi;


import com.baijiacms.qsp.dto.GameStatus;
import com.baijiacms.qsp.util.MP3Player;
import com.baijiacms.qsp.util.Uri;

import java.io.File;

public class AudioPlayer {
    AudioPlayer audioInterface;
    private MP3Player mediaPlayer;

    public AudioPlayer() {
    }

    private AudioPlayer(GameStatus gameStatus, String fileName, float volume) {
        String filePath = Uri.getFilePath(gameStatus.gameResourcePath, fileName);
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

    public void playFile(GameStatus gameStatus, String path, int volume) {
        closeAllFiles();
        audioInterface = new AudioPlayer(gameStatus, path, Float.parseFloat(String.valueOf(volume)));
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
