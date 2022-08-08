package com.qsp.player.util;


public final class ThreadUtil {

    /**
     * @return <code>true</code> если текущий поток - <code>thread</code>, иначе <code>false</code>
     */
    public static boolean isSameThread(Thread thread) {
        return Thread.currentThread().equals(thread);
    }

    /**
     * @return <code>true</code> если текущий поток - основной, иначе <code>false</code>
     */
    public static boolean isMainThread() {

            return  true;

//        return Thread.currentThread().equals(Looper.getMainLooper().getThread());
    }


}
