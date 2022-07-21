package android.os;

public final class Looper {

    private static final ThreadLocal<Looper> THREAD_LOCAL = new ThreadLocal<Looper>();

    private static Looper sMainLooper;
    private static Thread mainThread;

    private MessageQueue messageQueue;

    public static void prepare() {
        if (THREAD_LOCAL.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        THREAD_LOCAL.set(new Looper());
    }

    public static void prepareMainLooper() {
        prepare();
        synchronized (Looper.class) {
            if (sMainLooper != null) {
                throw new IllegalStateException("The main Looper has already been prepared.");
            }
            sMainLooper = myLooper();
            mainThread=Thread.currentThread();
        }
    }
    public Thread getThread()
    {
        return mainThread;
    }

    public static Looper getMainLooper() {
        if(sMainLooper==null)
        {
            prepareMainLooper();
        }
        synchronized (Looper.class) {
            return sMainLooper;
        }
    }

    public static Looper myLooper() {
        Looper looper =THREAD_LOCAL.get();
        if(looper==null)
        {
           return new Looper();
        }
        return looper;
    }

    private Looper() {
        messageQueue = new MessageQueue();
    }

    public MessageQueue getQueue() {
        return messageQueue;
    }

    public static void loop() {
        Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No looper");
        }
        MessageQueue queue = me.messageQueue;
        while(true) {
            Message msg = queue.next();
            if (msg == null) {
                return;
            }
            msg.target.dispatchMessage(msg);
            msg.recycleUnchecked();
        }
    }

    public void quit(){
        messageQueue.quit(false);
    }

    public void quitSafely() {
        messageQueue.quit(true);
    }
}