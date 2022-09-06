package com.baijiacms.qsp.jxbrowser7;

import com.qsp.QspEngineServer;
import com.qsp.player.common.QspConstants;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.ProprietaryFeature;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

public class Runner {
    private static final String LICENSE_1="6P835FT5HAPTB03TPIEFPGU5ECGJN8GMGDD79MD7Y52NVP0K0IV6FHYZVQI25H0MLGI2";
    private static final String LICENSE_2="6P835FT5HAV2EG8VQ5ZBIC76PR2YH2T40MCSXZ361FBESJ24DO3U9RRANTA2PFUWI2NP";

    public static void main(String[] args) throws Exception {
        int port = QspConstants.HTTP_PORT;
        if (args != null && args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
                QspConstants.HTTP_PORT = port;
                QspConstants.HTTP_LOCAL_URL = "http://127.0.0.1:" + port;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        QspEngineServer qspEngineServer = new QspEngineServer(port);
        qspEngineServer.start();
        System.out.println("Loading....");

        System.setProperty("jxbrowser.license.key", LICENSE_1);
        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED)
                        .enableProprietaryFeature(ProprietaryFeature.AAC)
                        .enableProprietaryFeature(ProprietaryFeature.H_264)
                        .licenseKey(LICENSE_1).build());
        Browser browser = engine.newBrowser();


        BrowserView view = BrowserView.newInstance(browser);

        JPanel addressPane = new JPanel(new BorderLayout());

        JFrame frame = new JFrame(QspConstants.ENGINE_TITLE + " " + QspConstants.ENGINE_VERSION + " Powered By " + QspConstants.ENGINE_POWER_BY);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(addressPane, BorderLayout.NORTH);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(QspConstants.MIN_WIDTH, QspConstants.MIN_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle bounds = new Rectangle(screenSize);
        frame.setBounds(bounds);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(true);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {
                engine.close();
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

        browser.navigation().loadUrl(qspEngineServer.getHttpUrl() + "");
//        String cmd = "rundll32 url.dll,FileProtocolHandler "+baseHttpPath;//打开浏览器
//        Runtime.getRuntime().exec(cmd);
        qspEngineServer.join(); //这个是阻断器

    }

}