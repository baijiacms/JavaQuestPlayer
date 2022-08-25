package com.baijiacms.jxbrowser;

import com.baijiacms.qsp.QspEngineServer;
import com.baijiacms.qsp.common.QspConstants;

import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

/**
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
 */
public class Runner {
    public static void main(String[] args) throws Exception {
        int port=QspConstants.HTTP_PORT;
        if(args!=null&&args.length>=1)
        {
            try {
                port = Integer.parseInt(args[0]);
                QspConstants.HTTP_PORT=port;
                QspConstants.HTTP_LOCAL_URL ="http://127.0.0.1:"+ port;
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        QspEngineServer qspEngineServer=new QspEngineServer(port);
        qspEngineServer.start();
        System.out.println("Loading....");

        final Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);
        browser.getCacheStorage().clearCache();
        browser.getLocalWebStorage().clear();
        browser.getSessionWebStorage().clear();
        JPanel addressPane = new JPanel(new BorderLayout());

        JFrame frame = new JFrame(QspConstants.ENGINE_TITLE+" "+ QspConstants.ENGINE_VERSION+" Powered By "+QspConstants.ENGINE_POWER_BY);
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
        browser.loadURL(qspEngineServer.getHttpUrl()+"");
//        String cmd = "rundll32 url.dll,FileProtocolHandler "+baseHttpPath;//打开浏览器
//        Runtime.getRuntime().exec(cmd);
        qspEngineServer.join(); //这个是阻断器

    }

}