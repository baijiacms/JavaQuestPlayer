package com.baijiacms.qsp;

import com.baijiacms.qsp.common.QspConstants;
import com.baijiacms.qsp.handler.EngineHandler;
import org.eclipse.jetty.server.Server;

import java.net.Socket;

public class QspEngineServer {
    private Server server;
    private String httpUrl;
    private int localPort;
    private boolean isStart = false;

    public QspEngineServer(int port) {
        this.localPort = port;

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
            server.setHandler(new EngineHandler());
            server.start();
            isStart = true;
        }
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public String getHttpUrl() {
        return httpUrl;
    }
}
