package com.qsp.player.libqsp.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author：ChenXingYu
 * @date 2024/2/29 14:22
 */
public class QspThread extends Thread{

    public static final Queue<QspTask> messageQueue = new LinkedList<>(); // 创建一个字符串类型的链表作为消息队列
    private static final Logger logger = LoggerFactory.getLogger(QspThread.class);
    private QspCore qspCore;
    public QspThread()
    {
        super("QspThread");
        qspCore=new QspCore();
    }

    public static void addMessage(QspTask message) {
        synchronized (messageQueue) {
            messageQueue.add(message); // 将新消息添加到队尾
            messageQueue.notify(); // 通知等待的线程有新消息了
        }
    }

    public static QspTask getNextMessage() throws InterruptedException {
        synchronized (messageQueue) {
            while (messageQueue.isEmpty()) {
                messageQueue.wait(); // 如果队列为空则等待其他线程发送消息
            }

            return messageQueue.poll(); // 从队头取出并移除第一条消息返回
        }
    }


    @Override
    public void run() {
        while(true) {
            try {
                QspTask qspTask = getNextMessage();
                qspCore.executeTask(qspTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
