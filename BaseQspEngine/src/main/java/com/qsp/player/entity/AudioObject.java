package com.qsp.player.entity;

import com.qsp.player.vi.QspAudio;
import lombok.Data;
/**
 * @author baijiacms
 */
@Data
public class AudioObject {
    private QspAudio qspAudio;

    public AudioObject(QspAudio qspAudio) {
        this.qspAudio = qspAudio;
    }
}
