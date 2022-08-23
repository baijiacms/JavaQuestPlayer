package com.qsp;

import com.qsp.jetty.JettyHandler;
import com.qsp.player.common.QspConstants;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;

import java.net.Socket;

public class QspEngineServer {
    private Server server;
    JettyHandler jettyHandler;
    private String httpUrl;
    private int localPort;

    public QspEngineServer(int port) {
        this.localPort = port;

        jettyHandler = new JettyHandler();
//        jettyHandler.setResourceBase(Gengine.class.getResource("/html/resource").toExternalForm());
//        jettyHandler.setResourceBase(QspConstants.WEB_RESOURCE_PATH);


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

        int port = checkPort();
        server = new Server(port);

        HashSessionIdManager idmanager = new HashSessionIdManager();

        server.setSessionIdManager(idmanager);

// Sessions are bound to a context.

        ContextHandler context = new ContextHandler("/");

        server.setHandler(context);

// Create the SessionHandler (wrapper) to handle the sessions

        HashSessionManager manager = new HashSessionManager();

        SessionHandler sessions = new SessionHandler(manager);

        context.setHandler(sessions);

// Put jettyHandler inside of SessionHandler

        sessions.setHandler(jettyHandler);

        server.start();
     //   System.out.println("Use browser to:" + httpUrl);
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public String getHttpUrl() {
        return httpUrl;
    }
}
