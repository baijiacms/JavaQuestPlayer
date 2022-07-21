package com.qsp.webengine.vo;

import lombok.Data;

/**
 * @author cxy
 */
@Data
public class SaveGameVo implements Comparable {
    private String fileName;
    private long fileTime;
    private String fileTimeStr;

    @Override
    public int compareTo(Object o) {
        //时间倒叙
        long x = (fileTime - ((SaveGameVo) o).getFileTime());
        if (x > 0) {
            return -1;
        } else {
            if (x == 0) {
                return 0;
            } else {
                return 0;
            }

        }
    }
}
