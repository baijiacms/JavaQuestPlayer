package com.baijiacms.webviewer;

import org.eclipse.jetty.server.Server;

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
        String cmd = "rundll32 url.dll,FileProtocolHandler "+baseHttpPath;//打开浏览器
        Runtime.getRuntime().exec(cmd);
        System.out.println("访问地址："+baseHttpPath);
        server.join(); //这个是阻断器

    }
}