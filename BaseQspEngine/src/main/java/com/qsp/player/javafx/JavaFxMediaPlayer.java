package com.qsp.player.javafx;

import com.qsp.player.GameStatus;
import com.qsp.player.util.Uri;
import com.qsp.player.vi.AudioInterface;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * audio player
 */
public class JavaFxMediaPlayer implements AudioInterface {

    private MediaPlayer mediaPlayer;

    public JavaFxMediaPlayer() {
        new JFXPanel();
    }

    private JavaFxMediaPlayer(GameStatus gameStatus, String path,double volume) {
        if (gameStatus != null) {
            this.mediaPlayer = new MediaPlayer(new Media("file:///"+Uri.getFilePath(gameStatus.gameResourcePath, path)));
            if(volume>0) {
                mediaPlayer.setVolume(volume);
            }
            mediaPlayer.play();
        }
    }

    @Override
    public AudioInterface createNewPlayer(GameStatus gameStatus, String fileName,double volume) {
        stop();
        return new JavaFxMediaPlayer(gameStatus, fileName,volume);
    }

    @Override
    public void stop() {
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void release() {
        stop();
        mediaPlayer = null;
    }

    @Override
    public void pause() {
        if(mediaPlayer!=null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void start() {
        if(mediaPlayer!=null) {
            mediaPlayer.play();
        }
    }

    @Override
    public boolean isPlaying() {
        if(mediaPlayer!=null) {
            return mediaPlayer.isAutoPlay();
        }else
        {
            return false;
        }
    }

    @Override
    public void setVolume(float sysVolume) {
        if(mediaPlayer!=null) {
            mediaPlayer.setVolume(sysVolume);
        }
    }
}
