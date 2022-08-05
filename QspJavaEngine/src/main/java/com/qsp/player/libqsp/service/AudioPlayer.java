package com.qsp.player.libqsp.service;


import android.os.Handler;
import android.os.Looper;
import com.qsp.player.vi.AudioInterface;
import com.qsp.player.common.QspConstants;
import com.qsp.player.util.StringUtil;
import com.qsp.player.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class AudioPlayer {
    private static final Logger logger = LoggerFactory.getLogger(AudioPlayer.class);

    private final ConcurrentHashMap<String, Sound> sounds = new ConcurrentHashMap<>();

    private Thread audioThread;
    private volatile Handler audioHandler;
    private volatile boolean audioThreadInited;
    private boolean soundEnabled;
    private boolean paused;
    private AudioInterface localAudioInterface;
    public AudioPlayer(AudioInterface audioInterface) {
        this.localAudioInterface = audioInterface;
    }
    public void start() {
        ThreadUtil.throwIfNotMainThread();

        audioThread = new Thread(() -> {
            try {
                Looper.prepare();
                audioHandler = new Handler();
                audioThreadInited = true;
                Looper.loop();
            } catch (Throwable t) {
                logger.error("Audio thread has stopped exceptionally", t);
            }
        });
        audioThread.start();
    }

    public void stop() {
        ThreadUtil.throwIfNotMainThread();
        pause();

        if (audioThread == null) {
            return;
        }

        if (audioThreadInited) {
            Handler handler = audioHandler;
            if (handler != null) {
                handler.getLooper().quitSafely();
            }
            audioThreadInited = false;
        } else {
            logger.warn("Audio thread has been started, but not initialized");
        }
        audioThread = null;
    }

    public void playFile( String path1, final int volume) {

      final  String  path= QspConstants.GAME_RESOURCE_PATH+"/"+path1;
      if(new File(path).exists()==false)
      {
          System.out.println("未找到:"+path);
          return;
      }
        runOnAudioThread(() -> {
            Sound sound = sounds.get(path);
            if (sound != null) {
                sound.volume = volume;
            } else {
                sound = new Sound();
                sound.path = path;
                sound.volume = volume;
                sounds.put(path, sound);
            }
            if (soundEnabled && !paused) {
                doPlay(sound);
            }
        });
    }

    private void runOnAudioThread(final Runnable runnable) {
        if (audioThread == null) {
            logger.warn("Audio thread has not been started");
            return;
        }
        if (!audioThreadInited) {
            logger.warn("Audio thread has not been initialized");
            return;
        }
        Handler handler = audioHandler;
        if (handler != null) {
            handler.post(runnable);
        }
    }

    private void doPlay(final Sound sound) {
        float sysVolume = getSystemVolume(sound.volume);

        if (sound.player != null) {
            sound.player.setVolume(sysVolume);
            if (!sound.player.isPlaying()) {
                sound.player.start();
            }
            return;
        }

        final String normPath = sound.path.replace("\\", "/");
        File file = new File(normPath);
        if (!file.exists()) {
            logger.error("Sound file not found: " + normPath);
            return;
        }

        AudioInterface player = this.localAudioInterface.createNewPlayer(file.getAbsolutePath());

//            player.setDataSource(file.getAbsolutePath());
//            player.prepare();

//        player.setOnCompletionListener(mp -> sounds.remove(sound.path));
        player.setVolume(sysVolume);
        player.start();

        sound.player = player;
    }

    private float getSystemVolume(int volume) {
        return volume / 100.f;
    }

    public void closeAllFiles() {
        runOnAudioThread(() -> {
            for (Sound sound : sounds.values()) {
                doClose(sound);
            }
            sounds.clear();
        });
    }

    private void doClose(Sound sound) {
        if (sound.player == null) {
            return;
        }
        if (sound.player.isPlaying()) {
            sound.player.stop();
        }
        sound.player.release();
    }

    public void closeFile(final String path) {
        runOnAudioThread(() -> {
            Sound sound = sounds.remove(path);
            if (sound != null) {
                doClose(sound);
            }
        });
    }

    public void pause() {
        if (paused) {
            return;
        }

        paused = true;

        runOnAudioThread(() -> {
            for (Sound sound : sounds.values()) {
                if (sound.player != null && sound.player.isPlaying()) {
                    sound.player.pause();
                }
            }
        });
    }

    public void resume() {
        if (!paused) {
            return;
        }

        paused = false;

        if (!soundEnabled) {
            return;
        }

        runOnAudioThread(() -> {
            for (Sound sound : sounds.values()) {
                doPlay(sound);
            }
        });
    }

    public boolean isPlayingFile(String path) {
        path= QspConstants.GAME_RESOURCE_PATH+path;
        return StringUtil.isNotEmpty(path) && sounds.containsKey(path);
    }

    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    private static class Sound {
        private String path;
        private int volume;
        private AudioInterface player;
    }
}
