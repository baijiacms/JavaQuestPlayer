package com.qsp.player.libqsp;

import com.qsp.player.common.QspConstants;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 开发工具DLL
 */
public class NativeDevMethods {
    private void dirFolder(File folder, String folderName, Map<String, String> qsrcMap) {
        File[] files = folder.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                dirFolder(f, "/" + f.getName() + "/", qsrcMap);
            } else {
                if (f.isFile() && f.getPath().endsWith(".qsrc")) {
                    qsrcMap.put(folderName + f.getName(), f.getPath());
                }
            }
        }
    }

    static {
        System.load(QspConstants.QSP_DEV_DLL_PATH);
    }

    /**
     * 输入游戏text获取qsp二进制数据
     *
     * @param data
     * @param dataSize
     * @param fileName
     * @return
     */
    public native byte[] GetQspDate(byte data[], int dataSize, String fileName);

    /**
     * 传入qsp文件地址，输出txt文件（含密码才能验证）
     *
     * @param fromFile
     * @param toFile
     * @param password
     * @return
     */
    public native boolean QspToTxt(String fromFile, String toFile, String password);

    /**
     * qsp转txt
     *
     * @param fromFile
     * @param toFile
     * @param password
     */
    public void qspFileToText(String fromFile, String toFile, String password) {
        QspToTxt(fromFile, toFile, password);
    }

    /**
     * 获取qsrc列表
     *
     * @param qprojPath
     * @return
     */
    private List<String> getQsrcListFromQproj(String qprojPath) {
        List<String> list = new LinkedList<>();
        try {
            //1.创建Reader对象
            SAXReader saxReader = new SAXReader();
            //2.加载xml
            Document document = saxReader.read(new File(qprojPath));
            //3.获取根节点
            Element rootElement = document.getRootElement();
            // rootElement.elements()获取根节点下所有的节点，
            List<Element> elements = null;
            Element structure = rootElement.element("Structure");
            if (structure != null) {
                elements = structure.elements();
            } else {
                elements = rootElement.elements();
            }
            for (Element element : elements) {
                if ("Location".equals(element.getName())) {
                    String file = element.attribute("name").getValue();
                    file = file.replace("#", "_");
                    file = file.replace("$", "_");
                    file = file + ".qsrc";
                    list.add("/" + file);
                }
                if ("Folder".equals(element.getName())) {
                    String folder = element.attribute("name").getValue();

                    for (Element element2 : element.elements()) {
                        String file2 = element2.attribute("name").getValue();
                        file2 = file2.replace("#", "_");
                        file2 = file2.replace("$", "_");

                        list.add("/" + folder + "/" + file2 + ".qsrc");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * 读取文件夹的所有qsrc目录
     *
     * @param srcFolder
     * @param qprojPath
     * @return
     */
    private byte[] readQrcFolder(String srcFolder, String qprojPath) {

        StringBuilder result = new StringBuilder();
        List<String> qsrcList = new LinkedList<>();
        Map<String, String> qsrcMap = new HashMap<>();
        dirFolder(new File(srcFolder), "/", qsrcMap);
        List<String> qprojList = getQsrcListFromQproj(qprojPath);
        for (String name : qprojList) {
            if (qsrcMap.get(name) != null) {
                qsrcList.add(qsrcMap.get(name));
            }
        }

        try {
            BufferedReader br = null;
            for (String qsrc : qsrcList) {
                br = new BufferedReader(new FileReader(new File(qsrc)));
                String s = "";
                while ((s = br.readLine()) != null) {
                    result.append(s + "\r\n");
                }
                result.append("\r\n");
            }
            result.append("#addbuilddate\r\n");
            result.append("$builddate = 'July 01, 2022'\r\n");
            result.append("--- addbuilddate ---------------------------------");

            byte[] textdata = result.toString().getBytes("UTF-16LE");

            byte[] utf16lemessage = new byte[2 + textdata.length];
            utf16lemessage[0] = (byte) 0xFF;
            utf16lemessage[1] = (byte) 0xFE;
            System.arraycopy(textdata, 0,
                    utf16lemessage, 2,
                    textdata.length);
            return utf16lemessage;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * qsrc to one txt
     *
     * @param srcFolder
     * @param qprojPath
     * @param outputTxtPath
     */
    public void outputFileOneText(String srcFolder, String qprojPath, String outputTxtPath) {

        byte[] data = readQrcFolder(srcFolder, qprojPath);

        try {
            FileOutputStream fileOutputStream2 = new FileOutputStream(new File(outputTxtPath));
            fileOutputStream2.write(data);
            fileOutputStream2.flush();
            fileOutputStream2.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream2, "UTF-16LE");


    }

    /**
     * get qsp ByteDate
     *
     * @param srcFolder
     * @param qprojPath
     * @return
     */
    public byte[] getQspByteDate(String srcFolder, String qprojPath) {
        byte[] utf16lemessage = readQrcFolder(srcFolder, qprojPath);


        byte[] data = GetQspDate(utf16lemessage, utf16lemessage.length, "");
        return data;


    }

    /**
     * 输出qsp文件
     *
     * @param srcFolder
     * @param qprojPath
     * @param toGemFile
     */
    public void toQspFile(String srcFolder, String qprojPath, String toGemFile) {

        byte[] data = getQspByteDate(srcFolder, qprojPath);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(toGemFile));

            fileOutputStream.write(data);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
