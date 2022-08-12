package com.qsp.dev;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

/**
 * 文件夹生成
 */
public class DevMoveUtils {

    public static void main(String[] args) throws Exception {
        String srcFolder = "D:/javaproject/resources/game/pwdQspExample/locations/";
        String desFolder = "D:/javaproject/resources/game/pwdQspExample/new/";
        //1.创建Reader对象
        SAXReader saxReader = new SAXReader();
        //2.加载xml
        Document document = saxReader.read(new File("D:/javaproject/resources/game/pwdQspExample/glife.xml"));
        //3.获取根节点
        Element rootElement = document.getRootElement();
        // rootElement.elements()获取根节点下所有的节点，
        List<Element> elements = rootElement.element("Structure").elements();
        for (Element element : elements) {
            if ("Location".equals(element.getName())) {
                String file = element.attribute("name").getValue();
                file = file.replace("#", "_");
                file = file.replace("$", "_");
                if (new File(srcFolder + file + ".qsrc").exists()) {
                    FileUtils.copyFile(new File(srcFolder + file + ".qsrc"), new File(desFolder + file + ".qsrc"));
                }
                System.out.println("Location:" + file);
            }
            if ("Folder".equals(element.getName())) {
                String folder = element.attribute("name").getValue();
                System.out.println("Folder:" + folder);
                new File(desFolder + folder + "/").mkdir();
                for (Element element2 : element.elements()) {
                    String file2 = element2.attribute("name").getValue();
                    file2 = file2.replace("#", "_");
                    file2 = file2.replace("$", "_");

                    if (new File(srcFolder + file2 + ".qsrc").exists()) {
                        System.out.println(srcFolder + file2 + ".qsrc" + "--move--" + desFolder + folder + "/" + file2 + ".qsrc");
                        FileUtils.copyFile(new File(srcFolder + file2 + ".qsrc"), new File(desFolder + folder + "/" + file2 + ".qsrc"));
                    }
                    System.out.println("Location2:" + file2);
                }
            }
        }
    }
}
