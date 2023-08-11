package com.qsp.player.entity;

import com.qsp.player.vi.QspAudio;

/**
 * @author baijiacms
 */
public class AudioObject {
    private QspAudio qspAudio;

    public AudioObject(QspAudio qspAudio) {
        this.qspAudio = qspAudio;
    }

    public QspAudio getQspAudio() {
        return qspAudio;
    }

}
