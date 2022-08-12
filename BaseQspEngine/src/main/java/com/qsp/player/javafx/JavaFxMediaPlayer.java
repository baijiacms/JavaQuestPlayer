package com.qsp.player.javafx;

import com.qsp.player.GameStatus;
import com.qsp.player.util.Uri;
import com.qsp.player.vi.AudioInterface;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * audio player
 */
public class JavaFxMediaPlayer implements AudioInterface {

    private MediaPlayer mediaPlayer;

    public JavaFxMediaPlayer() {

    }

    private JavaFxMediaPlayer(GameStatus gameStatus, String path) {
        if (gameStatus != null) {
            this.mediaPlayer = new MediaPlayer(new Media(Uri.getFilePath(gameStatus.gameResourcePath, path)));
            mediaPlayer.play();
        }
    }

    @Override
    public AudioInterface createNewPlayer(GameStatus gameStatus, String fileName) {
        return new JavaFxMediaPlayer(gameStatus, fileName);
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void release() {
        mediaPlayer.stop();
        mediaPlayer = null;
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void start() {
        mediaPlayer.play();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isAutoPlay();
    }

    @Override
    public void setVolume(float sysVolume) {
        mediaPlayer.setVolume(sysVolume);
    }
}
