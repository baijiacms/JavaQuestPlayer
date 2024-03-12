package com.baijiacms.qsp;

import com.baijiacms.qsp.common.HttpContent;
import com.qsp.player.libqsp.queue.QspAction;
import com.qsp.player.libqsp.queue.QspTask;
import com.qsp.player.libqsp.queue.QspThread;
import com.qsp.player.libqsp.util.Base64Util;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.ProprietaryFeature;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

/**
 *
 *jre\bin\java.exe -jar -Dfile.encoding=utf-8 lib/QspJxBrowser7Player-1.0.0-SNAPSHOT.jar
 * 弹出菜单被遮住
 * 解决：JPopupMenu.setDefaultLightWeightPopupEnabled(false);//弹出菜单重量级
 *
 * 设置当前的焦点控件
 * 应用皮肤beautyeye后有问题
 * 因为应用了 BeautyEyeLNFHelper.FrameBorderStyle.translucencySmallShadow
 * 改成：BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated; 就可以了
 *
 * 默认不出现右键
 * 需要注册browser.setDialogHandler(new DefaultDialogHandler(view));
 *
 * @author：ChenXingYu
 * @date 2023/8/4 11:07
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        QspThread task1 =new QspThread();
        task1.start();
        QspTask aspTask=new QspTask();
        aspTask.action= QspAction.init.getAction();
        QspThread.addMessage(aspTask);
//        SpringApplication.run(Application.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
         builder.headless(false).bannerMode(Banner.Mode.OFF).run(args);//显示swing
        System.out.println("game port: http://127.0.0.1:19999");


        System.setProperty(key(HttpContent.JX_PROPERTY_NAME), key(HttpContent.JX_KEY));
    //        BrowserPreferences.setChromiumSwitches("--remote-debugging-port=19999");//debug
        Engine engine = Engine.newInstance(EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED).enableProprietaryFeature(ProprietaryFeature.AAC).disableChromiumTraffic().enableProprietaryFeature(ProprietaryFeature.H_264).licenseKey(key(HttpContent.JX_KEY)).build());
        Browser browser = engine.newBrowser();
        BrowserView view = BrowserView.newInstance(browser);

        JFrame frame = new JFrame("JavaQuestPlayer 2024 Powered By https://github.com/baijiacms/");
        frame.setDefaultCloseOperation(3);
        frame.add(view, "Center");
        frame.setSize(1280, 960);
        frame.setLocationRelativeTo(null);
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        Rectangle bounds = new Rectangle(screenSize);
//        frame.setBounds(bounds);
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

                System.out.print("application exit");
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
        browser.navigation().loadUrl("http://127.0.0.1:19999");
        frame.setVisible(true);


    }
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


    public static String key(String key) {
        return Base64Util.decodeBase64( Base64Util.decodeBase64(key+"="));
    }
}
