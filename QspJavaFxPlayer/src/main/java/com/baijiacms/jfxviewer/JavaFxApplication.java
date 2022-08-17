package com.baijiacms.jfxviewer;

import com.qsp.QspEngineServer;
import com.qsp.player.vi.impl.SwingUtils;
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
public class JavaFxApplication extends Application {

    @Override
    public void start(final Stage stage) {
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        //禁用右击
        if (false) {
            browser.setEventDispatcher(new MyEventDispatcher(browser.getEventDispatcher()));
        }
        BorderPane root = new BorderPane(browser);
        stage.setScene(new Scene(root, QspConstants.MIN_WIDTH, QspConstants.MIN_HEIGHT));
        stage.setTitle(QspConstants.ENGINE_TITLE+" "+ QspConstants.ENGINE_VERSION+" Powered By "+QspConstants.ENGINE_POWER_BY);
        webEngine.setJavaScriptEnabled(true);
        //alert提示
        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                SwingUtils.showAlertDialog(event.getData());
            }
        });
        webEngine.load(QspConstants.HTTP_LOCAL_URL);
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


}
