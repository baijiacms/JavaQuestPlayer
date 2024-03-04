package com.baijiacms.qsp.socket;

import com.baijiacms.qsp.vo.StatusVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qsp.player.libqsp.queue.QspCore;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author：ChenXingYu
 * @date 2024/3/4 9:08
 */
@ServerEndpoint("/ws/user")
@Component
public class SocketServer {
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static ConcurrentHashMap<String, SocketServer> webSocketSet = new ConcurrentHashMap<String, SocketServer>();

    //连接打开时执行
    @OnOpen
    public void onOpen( Session session) {
        this.session = session;
        webSocketSet.put(session.getId(), this);
        SocketServer.sendFreshMessage();
    }
    public void sendMessage(String message) throws IOException {
//        this.session.getBasicRemote().sendText(message);
        this.session.getAsyncRemote().sendText(message);
    }
    //收到消息时执行
    @OnMessage
    public String onMessage(String message, Session session) {
        SocketServer.sendFreshMessage();
        return message;
    }

    //连接关闭时执行
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        try {
            webSocketSet.remove(session.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //连接错误时执行
    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static void sendFreshMessage()
    {
        StatusVo statusVo=new StatusVo();
        statusVo.setActionschanged(QspCore.actionschanged.get());
        statusVo.setMaindescchanged(QspCore.maindescchanged.get());
        statusVo.setObjectschanged(QspCore.objectschanged.get());
        statusVo.setVarsdescchanged(QspCore.varsdescchanged.get());
        for (String key : webSocketSet.keySet()) {
            try {
                webSocketSet.get(key).sendMessage(objectMapper.writeValueAsString(statusVo));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
