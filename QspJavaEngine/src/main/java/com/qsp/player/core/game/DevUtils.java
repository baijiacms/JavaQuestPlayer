package com.qsp.player.core.game;

import com.qsp.player.libqsp.NativeDevMethods;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

/**
 *
 * 开发工具实现类
 */
public class DevUtils {

    private final NativeDevMethods nativeDevMethods=new NativeDevMethods();
    public void qspFileToText(String fromFile, String toFile,String password)
    {

        nativeDevMethods.qspFileToText(fromFile,toFile,password);
    }
    /**
     * 文件夹合并成txt文件
     */
    public void outputGameOneTxt(String srcFolder,String qprojPath, String outputTxtPath) {
        this.nativeDevMethods.outputFileOneText(srcFolder,qprojPath,outputTxtPath);
    }
    /**
     * 文件夹合 转 gem 二进制
     */
    public byte[] getGemDate(String srcFolder,String qprojPath) {
        return this.nativeDevMethods.getQspByteDate(srcFolder,qprojPath);
    }
    /**
     * 文件夹合 转 qsp文件
     */
    public void toGemFile(String srcFolder,String qprojPath, String toGemFile)  {
        this.nativeDevMethods.toQspFile(srcFolder,qprojPath,toGemFile);
    }
    /**
     * 文件夹 整理
     */
    public  void arrangeData(String srcFolder,String desFolder,String qprojPath) throws Exception {

        //1.创建Reader对象
        SAXReader saxReader = new SAXReader();
        //2.加载xml
        Document document = saxReader.read(new File(qprojPath));
        //3.获取根节点
        Element rootElement = document.getRootElement();
        // rootElement.elements()获取根节点下所有的节点，
        List<Element> elements = rootElement.element("QGen-project").elements();
        for (Element element : elements) {
            if("Location".equals(element.getName()))
            {
                String file=element.attribute("name").getValue();
                file=file.replace("#","_");
                file=file.replace("$","_");
                FileUtils.copyFile(new File(srcFolder+file+".qsrc"),new File(desFolder+file+".qsrc"));
                System.out.println("Location:"+file);
            }
            if("Folder".equals(element.getName()))
            {
                String folder=element.attribute("name").getValue();
                System.out.println("Folder:"+folder);
                new File(desFolder+folder+"/").mkdir();
                for (Element element2 : element.elements()) {
                    String file2=element2.attribute("name").getValue();
                    file2=file2.replace("#","_");
                    file2=file2.replace("$","_");
                    FileUtils.copyFile(new File(srcFolder+file2+".qsrc"),new File(desFolder+folder+"/"+file2+".qsrc"));

                    System.out.println("Location2:"+file2);
                }
            }
        }

        System.out.println("整理完成");
    }
}
