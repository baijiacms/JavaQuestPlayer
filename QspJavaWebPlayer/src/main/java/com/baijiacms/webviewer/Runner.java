package com.baijiacms.webviewer;

import org.eclipse.jetty.server.Server;

import javax.swing.*;

public class Runner {

    public static void main(String[] args) throws Exception {
        int port=19870;
        if(args!=null&&args.length>1)
        {
            try {
                port = Integer.parseInt(args[0]);
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
         Server server=new Server(port);
        String baseHttpPath="http://127.0.0.1:"+port;
        JettyHandler jettyHandler=new JettyHandler(baseHttpPath);
//        jettyHandler.setResourceBase(Gengine.class.getResource("/html/resource").toExternalForm());
//        jettyHandler.setResourceBase(QspConstants.WEB_RESOURCE_PATH);
        server.setHandler(jettyHandler );
        server.start();
        System.out.println("Use browser to(使用浏览器访问地址):"+baseHttpPath);
        JOptionPane.showMessageDialog(null, "Use browser to(使用浏览器访问地址):"+baseHttpPath, "提示", JOptionPane.DEFAULT_OPTION);
        server.join(); //这个是阻断器

    }
}