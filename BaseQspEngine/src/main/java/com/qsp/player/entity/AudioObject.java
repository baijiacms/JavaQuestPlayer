package com.qsp.player.entity;

import com.qsp.player.vi.audio.QspAudio;
import lombok.Data;

@Data
public class AudioObject {
    private QspAudio qspAudio;

    public AudioObject(QspAudio qspAudio) {
        this.qspAudio = qspAudio;
    }
}
