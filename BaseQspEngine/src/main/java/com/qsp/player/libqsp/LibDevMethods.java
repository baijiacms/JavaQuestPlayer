package com.qsp.player.libqsp;

/**
 * 开发工具DLL
 */
public interface LibDevMethods {


    /**
     * 输入游戏text获取qsp二进制数据
     *
     * @param data
     * @param dataSize
     * @param fileName
     * @return
     */
    public byte[] GetQspDate(byte data[], int dataSize, String fileName);

    /**
     * 传入qsp文件地址，输出txt文件（含密码才能验证）
     *
     * @param fromFile
     * @param toFile
     * @param password
     * @return
     */
    public boolean QspToTxt(String fromFile, String toFile, String password);

    /**
     * qsp转txt
     *
     * @param fromFile
     * @param toFile
     * @param password
     */
    public void qspFileToText(String fromFile, String toFile, String password);


    /**
     * qsrc to one txt
     *
     * @param srcFolder
     * @param qprojPath
     * @param outputTxtPath
     */
    public void outputFileOneText(String srcFolder, String qprojPath, String outputTxtPath);

    /**
     * get qsp ByteDate
     *
     * @param srcFolder
     * @param qprojPath
     * @return
     */
    public byte[] getQspByteDate(String srcFolder, String qprojPath);

    /**
     * 输出qsp文件
     *
     * @param srcFolder
     * @param qprojPath
     * @param toGemFile
     */
    public void toQspFile(String srcFolder, String qprojPath, String toGemFile);
}
