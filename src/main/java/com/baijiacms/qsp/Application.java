package com.baijiacms.qsp;

import com.qsp.player.libqsp.queue.QspAction;
import com.qsp.player.libqsp.queue.QspTask;
import com.qsp.player.libqsp.queue.QspThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
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
        SpringApplication.run(Application.class, args);
    }
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
