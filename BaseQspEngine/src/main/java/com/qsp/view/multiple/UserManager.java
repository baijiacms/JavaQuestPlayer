package com.qsp.view.multiple;


import com.qsp.player.vi.QspAudio;
import com.qsp.player.vi.QspUi;
import com.qsp.view.multiple.dto.User;
import com.qsp.view.multiple.dto.UserId;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author baijiacms
 */
public class UserManager {
    public static final UserManager INSTANCE = new UserManager();
    private Map<String, UserId> userIdMap = new Hashtable<>();

    private UserManager() {
        for (int i = 0; i <= 20; i++) {
            UserId userId = new UserId();
            userId.setUse(false);
            userId.setUserId(i);
            userIdMap.put(USER_STR + i, userId);
        }
    }

    Map<String, User> userMamager = new Hashtable<>();
    Map<String, UserId> sessionMamager = new Hashtable<>();
    private String USER_STR = "USER_";

    public synchronized UserId getUserIdBySessionId(String sessionId) {
        UserId userId = sessionMamager.get(sessionId);
        return userId;
    }

    public synchronized User getOrNewUserBySessionId(String sessionId, QspUi qspUi, QspAudio qspAudio) {
        UserId userId = sessionMamager.get(sessionId);
        if (userId == null) {
            userId = synchronizedUser(-1);
            if (userId != null) {
                sessionMamager.put(sessionId, userId);
            } else {
                throw new RuntimeException("Max user online");
            }
        }
        return getUser(userId.getUserId(), qspUi, qspAudio);
    }

    public synchronized void deleteUserBySessionId(String sessionId) {
        UserId userId = sessionMamager.get(sessionId);
        if (userId != null) {
            User user = userMamager.get(USER_STR + userId.getUserId());
            if (user != null) {
                if (user.getLibEngine() != null && user.getLibEngine().getGameStatus() != null) {
                    user.getLibEngine().getGameStatus().setGameRunning(false);
                }
            }
            userId.setUse(false);
            if (userId.getUserId() >= 9) {
                synchronizedUser(userId.getUserId());
            }
        }
        sessionMamager.remove(sessionId);
    }

    private synchronized UserId synchronizedUser(int userId) {

        if (userId < 0) {
            for (int i = 0; i <= 20; i++) {
                UserId user = userIdMap.get(USER_STR + i);
                if (user.isUse() == false) {
                    user.setUse(true);
                    return user;
                }
            }
        } else {
            UserId oldUser = userIdMap.get(USER_STR + userId);
            if (oldUser != null) {
                oldUser.setUse(false);
                return null;
            }
        }

        return null;
    }

    public User getUser(int userId) {
        User user = userMamager.get(USER_STR + userId);
        return user;
    }

    private synchronized User getUser(int userId, QspUi qspUi, QspAudio qspAudio) {
        User user = userMamager.get(USER_STR + userId);
        if (user == null) {
            user = new User(userId, qspUi, qspAudio);
            userMamager.put(USER_STR + userId, user);
        }
        return user;
    }
}
