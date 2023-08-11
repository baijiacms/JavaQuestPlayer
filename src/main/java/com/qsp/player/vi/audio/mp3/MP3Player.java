package com.qsp.player.vi.audio.mp3;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


/**
 * A simple MP3Player, which is able to play an .mp3-file and pause, resume, and stop the playblack.
 */
public class MP3Player {

    public final static int STATUS_READY = 0;


    public final static int STATUS_PLAYING = 1;


    public final static int STATUS_PAUSED = 2;


    public final static int STATUS_FINISHED = 3;


    private int playerStatus = STATUS_READY;


    private AudioInputStream audioStream;


    private Clip clip;


    private FloatControl volumeControl;


    private BooleanControl muteControl;


    /**
     * Creates a new MP3Player from given InputStream.
     *
     * @param file
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     */
    public MP3Player(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {


        audioStream = AudioSystem.getAudioInputStream(file);
        AudioFormat baseFormat = audioStream.getFormat();
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
        AudioInputStream stream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);
        clip = AudioSystem.getClip();
        clip.open(stream);
        clip.addLineListener(new LineListener() {

            @Override
            public void update(LineEvent event) {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (clip.getLongFramePosition() >= clip.getFrameLength()) {
                        stop();
                        playerStatus = STATUS_FINISHED;
                    }
                }
            }
        });

        volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        muteControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);

    }


    /**
     * Starts playback (resumes if paused).
     *
     * @throws JavaLayerException when there is a problem decoding the file.
     */
    public void play() {
        synchronized (clip) {
            if (clip != null && clip.isOpen()) {
                clip.start();
                playerStatus = STATUS_PLAYING;
            }
        }
    }


    /**
     * Pauses playback.
     */
    public void pause() {
        synchronized (clip) {
            if (playerStatus == STATUS_PLAYING) {
                clip.stop();
                playerStatus = STATUS_PAUSED;
            }
        }
    }


    /**
     * Resumes playback.
     */
    public void resume() {
        synchronized (clip) {
            if (playerStatus == STATUS_PAUSED) {
                clip.start();
                playerStatus = STATUS_PLAYING;
            }
        }
    }


    /**
     * Stops playback. If not playing, does nothing.
     */
    public void stop() {
        synchronized (clip) {
            clip.stop();
            clip.setFramePosition(0);
            playerStatus = STATUS_FINISHED;
        }
    }


    /**
     * Returns the current player status.
     *
     * @return the player status
     */
    public int getPlayerStatus() {
        return this.playerStatus;
    }


    /**
     * Closes the player, regardless of current state.
     */
    public void close() {
        synchronized (clip) {
            if (clip != null && clip.isOpen()) {
                clip.stop();
                clip.close();
            }
        }
        try {
            audioStream.close();
            audioStream = null;
        } catch (IOException e) {
            // we are terminating, thus ignore exception
        }
    }


    public long getLength() {
        return clip.getMicrosecondLength() / 1000000;
    }


    public long getPositionInSeconds() {
        return clip.getMicrosecondPosition() / 1000000;
    }


    public FloatControl getVolumeControl() {
        return volumeControl;
    }


    public void setMute(boolean mute) {
        muteControl.setValue(mute);
    }


}