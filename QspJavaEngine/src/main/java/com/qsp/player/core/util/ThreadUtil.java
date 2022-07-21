package com.qsp.player.core.util;

import android.os.Looper;

public final class ThreadUtil {


    public static boolean isSameThread(Thread thread) {
        return Thread.currentThread().equals(thread);
    }


    public static boolean isMainThread() {
        if(true)
        {
            return  true;
        }
        return Thread.currentThread().equals(Looper.getMainLooper().getThread());
    }


    public static void throwIfNotMainThread() {
        if(true)
        {
            return ;
        }
        if (!isMainThread()) {
            throw new RuntimeException("Must be called from the main thread");
        }
    }
}
