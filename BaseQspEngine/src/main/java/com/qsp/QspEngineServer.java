package com.qsp;

import com.qsp.player.common.QspConstants;
import com.qsp.view.handler.ServerHandler;
import com.qsp.view.multiple.SessionListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;

import java.net.Socket;

/**
 * @author baijiacms
 * @version V1.0.0
 */
public class QspEngineServer {
    private Server server;
    private String httpUrl;
    private int localPort;
    private boolean isStart = false;
    private boolean isGUI;
    private boolean isMultiple;

    public QspEngineServer(int port) {
        this.localPort = port;
        this.isMultiple = false;
        this.isGUI = true;
    }

    private void updatePort(int port) {
        this.localPort = port;
        QspConstants.HTTP_PORT = port;
        QspConstants.HTTP_LOCAL_URL = "http://127.0.0.1:" + port;
        httpUrl = QspConstants.HTTP_LOCAL_URL;
    }

    private int checkPort() {
        int port = localPort;
        for (int i = 1; i < 100; i++) {
            try {
                new Socket("127.0.0.1", port);
                port = port + i;
            } catch (Exception e) {
                break;
            }
        }
        updatePort(port);
        return port;
    }

    public void start() throws Exception {
        if (isStart == false) {
            int port = checkPort();
            server = new Server(port);
            if (isMultiple == false) {
                server.setHandler(new ServerHandler(isGUI, isMultiple));
            } else {
                HashSessionIdManager idmanager = new HashSessionIdManager();
                server.setSessionIdManager(idmanager);
                ContextHandler context = new ContextHandler("/");
                HashSessionManager manager = new HashSessionManager();
                manager.setMaxInactiveInterval(60);
                SessionHandler sessions = new SessionHandler(manager);
                sessions.addEventListener(new SessionListener());
                context.setHandler(new ServerHandler(isGUI, isMultiple));
                sessions.setHandler(context);
                server.setHandler(sessions);
            }
            server.start();
            isStart = true;
        }
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getHttpUrl() {
        return httpUrl;
    }
}
