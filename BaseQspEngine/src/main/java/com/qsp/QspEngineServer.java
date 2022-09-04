package com.qsp;

import com.qsp.player.common.QspConstants;
import com.qsp.view.handler.ServerHandler;
import org.eclipse.jetty.server.Server;

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

    public QspEngineServer(int port) {
        this.localPort = port;
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
        start(null);
    }
    public void start(String gameId) throws Exception {
        if (isStart == false) {
            int port = checkPort();
            server = new Server(port);
            server.setHandler(new ServerHandler(isGUI,gameId));
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
