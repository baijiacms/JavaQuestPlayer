package com.qsp.player.libqsp;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * dev tools
 */
public class DevMethodsHelper {

    private static final Logger logger = LoggerFactory.getLogger(DevMethodsHelper.class);
    private final LibDevMethods nativeDevMethods = new NativeDevMethods();

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
        List<Element> elements = rootElement.element("QGen-project").elements();
        for (Element element : elements) {
            if ("Location".equals(element.getName())) {
                String file = element.attribute("name").getValue();
                file = file.replace("#", "_");
                file = file.replace("$", "_");
                FileUtils.copyFile(new File(srcFolder + file + ".qsrc"), new File(desFolder + file + ".qsrc"));
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
                    FileUtils.copyFile(new File(srcFolder + file2 + ".qsrc"), new File(desFolder + folder + "/" + file2 + ".qsrc"));

                    logger.info("Location2:" + file2);
                }
            }
        }

        logger.info("整理完成");
    }
}
