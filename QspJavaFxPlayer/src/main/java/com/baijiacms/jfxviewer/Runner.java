package com.baijiacms.jfxviewer;

import com.qsp.QspEngineServer;
import com.qsp.player.common.QspConstants;
import javafx.application.Application;

public class Runner {
    public static void main(String[] args)  throws Exception {
        int port= QspConstants.HTTP_PORT;
        if(args!=null&&args.length>1)
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
        //继承了Application，launch是Application类的方法。英文意思是发射/发动的意思。
        Application.launch(JavaFxApplication.class);
    }
}
