package com.baijiacms.browser;

import com.qsp.player.common.QspConstants;
import org.eclipse.jetty.server.Server;

import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
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


        final Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);
        browser.getCacheStorage().clearCache();
        browser.getLocalWebStorage().clear();
        browser.getSessionWebStorage().clear();

        JPanel addressPane = new JPanel(new BorderLayout());

        JFrame frame = new JFrame("Java-Quest-Soft-player "+ QspConstants.ENGINE_VERSION+" Powered By https://github.com/baijiacms/");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(addressPane, BorderLayout.NORTH);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(1280, 960);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {

                System.out.print("监听到窗口关闭，程序结束");
                System.exit(1);
            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });
        browser.loadURL(baseHttpPath+"");
//        String cmd = "rundll32 url.dll,FileProtocolHandler "+baseHttpPath;//打开浏览器
//        Runtime.getRuntime().exec(cmd);
        System.out.println("访问地址："+baseHttpPath);
        server.join(); //这个是阻断器

    }

}