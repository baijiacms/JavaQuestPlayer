package android.os;

import java.util.Date;

public class SystemClock {
    public static long elapsedRealtime()
    {
        return System.currentTimeMillis();
    }
}
