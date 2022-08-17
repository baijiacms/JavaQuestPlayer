package com.qsp.player.vi.impl;

import com.qsp.player.GameStatus;
import com.qsp.player.util.MP3Player;
import com.qsp.player.util.Uri;
import com.qsp.player.vi.AudioInterface;

import java.io.File;

/**
 * JavaFxMediaPlayer
 */
public class MediaPlayer implements AudioInterface {

    private MP3Player mediaPlayer;

    public MediaPlayer() {

    }

    private MediaPlayer(GameStatus gameStatus, String fileName, float volume) {
        String filePath= Uri.getFilePath(gameStatus.gameResourcePath, fileName);
        File mediaFile=new File(filePath);
        if(mediaFile.exists()==false)
        {
            mediaFile=new File("file:///"+filePath);
        }
        if(mediaFile.exists()) {

            try {
                this.mediaPlayer = new MP3Player(mediaFile);

                mediaPlayer.getVolumeControl().setValue(volume);
                this.mediaPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public AudioInterface createNewPlayer(GameStatus gameStatus,String fileName,float volume) {
        return new MediaPlayer(gameStatus,fileName,volume);
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void release() {
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
        }
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
            return mediaPlayer.getPlayerStatus() == MP3Player.STATUS_PLAYING;
        }else
        {
            return false;
        }
    }

    @Override
    public void setVolume(float sysVolume) {
        mediaPlayer.getVolumeControl().setValue(sysVolume);
    }
}
