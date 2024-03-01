package com.qsp.player.libqsp;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author baijiacms
 */
public class DevMethodsHelper {

    private static final Logger logger = LoggerFactory.getLogger(DevMethodsHelper.class);
    private  LibDevMethods nativeDevMethods ;
    public DevMethodsHelper(LibDevMethods nativeDevMethods){
        this.nativeDevMethods=nativeDevMethods;
    }

    public void qspFileToText(String fromFile, String toFile, String password) {

        nativeDevMethods.qspFileToText(fromFile, toFile, password);
    }

    /**
     * qprojPath to one txt file
     */
    public void outputGameOneTxt(String srcFolder, String qprojPath, String outputTxtPath) {
        this.nativeDevMethods.outputFileOneText(srcFolder, qprojPath, outputTxtPath);
    }

    /**
     * qproj srcFolder to gem
     */
    public byte[] getGemDate(String srcFolder, String qprojPath) {
        return this.nativeDevMethods.getQspByteDate(srcFolder, qprojPath);
    }

    /**
     * qproj srcFolder to qsp
     */
    public void toGemFile(String srcFolder, String qprojPath, String toGemFile) {
        this.nativeDevMethods.toQspFile(srcFolder, qprojPath, toGemFile);
    }

    public void textToQsrc(String src, String desFolder) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(src), "utf-8"));
        String s = "";
        List<String> stringList = new LinkedList<>();
        while ((s = br.readLine()) != null) {
            stringList.add(s);
        }
        System.out.println(stringList.size());
        List<String> locationsList = new LinkedList<>();
        for (String txt : stringList) {
            if (txt.startsWith("--- ")) {
                if (txt.endsWith(" ---------------------------------")) {
                    String location = txt.replace("--- ", "").replace(" ---------------------------------", "");
                    locationsList.add(location);
                }
            }
        }
        System.out.println(locationsList.size());
        int x = 0;
        for (int i = 0; i < locationsList.size(); i++) {
            String location = locationsList.get(i);
            for (; x < stringList.size(); x++) {
                String txt = stringList.get(x);
                String stext = ("# " + location);
                if (txt.compareTo(stext) == 0) {
                    String newLocation = desFolder + location + ".qsrc";
                    System.out.println(newLocation);
                    if (new File(newLocation).exists()) {
                        newLocation = newLocation + "2";
                    }
                    FileWriter fw = new FileWriter(newLocation);
                    for (int y = x; y < stringList.size(); y++) {
                        String txt2 = stringList.get(y);
                        fw.write(txt2);
                        fw.write("\r\n");
                        if (txt2.compareTo(("--- " + location + " ---------------------------------")) == 0) {
                            fw.flush();
                            fw.close();
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * folder arrange (usefull)
     */

    public void arrangeData(String srcFolder, String desFolder, String qprojPath) throws Exception {

        //1.创建Reader对象
        SAXReader saxReader = new SAXReader();
        //2.加载xml
        Document document = saxReader.read(new File(qprojPath));
        //3.获取根节点
        Element rootElement = document.getRootElement();
        // rootElement.elements()获取根节点下所有的节点，
        List<Element> elements = rootElement.elements();
        for (Element element : elements) {
            if ("Location".equals(element.getName())) {
                String file = element.attribute("name").getValue();
                file = file.replace("#", "_");
                file = file.replace("$", "_");
                FileUtils.moveFile(new File(srcFolder + file + ".qsrc"), new File(desFolder + file + ".qsrc"));
                logger.info("Location:" + file);
            }
            if ("Folder".equals(element.getName())) {
                String folder = element.attribute("name").getValue();
                logger.info("Folder:" + folder);
                new File(desFolder + folder + "/").mkdir();
                for (Element element2 : element.elements()) {
                    String file2 = element2.attribute("name").getValue();
                    file2 = file2.replace("#", "_");
                    file2 = file2.replace("$", "_");
                    FileUtils.moveFile(new File(srcFolder + file2 + ".qsrc"), new File(desFolder + folder + "/" + file2 + ".qsrc"));

                    logger.info("Location2:" + file2);
                }
            }
        }

        logger.info("整理完成");
    }
}
