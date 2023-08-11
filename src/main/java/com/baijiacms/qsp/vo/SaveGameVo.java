
package com.baijiacms.qsp.vo;

/**
 * @author baijiacms
 */
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileTime() {
        return fileTime;
    }

    public void setFileTime(long fileTime) {
        this.fileTime = fileTime;
    }

    public String getFileTimeStr() {
        return fileTimeStr;
    }

    public void setFileTimeStr(String fileTimeStr) {
        this.fileTimeStr = fileTimeStr;
    }
}
