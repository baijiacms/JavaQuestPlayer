package com.baijiacms.jfxviewer;

import com.qsp.player.javafx.JavaFxUtils;
import com.qsp.webengine.HtmlEngine;
import com.qsp.player.common.QspConstants;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * jsf启动类
 *
 * @author cxy
 */
public class Runner extends Application {

    @Override
    public void start(final Stage stage) {
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        //禁用右击
        if (false) {
            browser.setEventDispatcher(new MyEventDispatcher(browser.getEventDispatcher()));
        }
        BorderPane root = new BorderPane(browser);
        stage.setScene(new Scene(root, 1024, 768));
        stage.setTitle("Java-Quest-Soft-player "+QspConstants.ENGINE_VERSION+" Powered By https://github.com/baijiacms/");
        webEngine.setJavaScriptEnabled(true);
        //alert提示
        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                JavaFxUtils.showAlertDialog(event.getData());
            }
        });
        webEngine.load(QspConstants.LOCAL_URL);
        stage.setMaximized(true);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.print("监听到窗口关闭，程序结束");
                System.exit(1);
            }

        });
        stage.show();
    }

    public static void main(String[] args) {
//        Logger rootLogger = LogManager.getLogManager().getLogger("");
//
//        rootLogger.setLevel(Level.ALL);
//
//        for (Handler h : rootLogger.getHandlers()) {
//
//            h.setLevel(Level.ALL);
//
//        }

        new HtmlEngine().startProxy();
        //继承了Application，launch是Application类的方法。英文意思是发射/发动的意思。
        launch(args);
    }
}
