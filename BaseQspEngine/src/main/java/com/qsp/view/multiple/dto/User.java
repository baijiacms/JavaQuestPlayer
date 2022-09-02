package com.qsp.view.multiple.dto;

import com.qsp.player.LibEngine;
import com.qsp.player.vi.QspAudio;
import com.qsp.player.vi.QspUi;

/**
 * @author baijiacms
 */
public class User {
    private LibEngine libEngine;
    private int userId;

    public User(int userId, QspUi qspUi, QspAudio qspAudio) {
        this.userId = userId;
        libEngine = new LibEngine(userId, qspUi, qspAudio);
    }

    public LibEngine getLibEngine() {
        return libEngine;
    }

    public int getUserId() {
        return userId;
    }
}
