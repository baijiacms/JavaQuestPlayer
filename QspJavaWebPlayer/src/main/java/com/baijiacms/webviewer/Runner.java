package com.baijiacms.webviewer;

import com.qsp.QspEngineServer;
import com.qsp.player.common.QspConstants;
import javax.swing.*;

public class Runner {

    public  static void main(String[] args) throws Exception {
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
        System.out.println("使用浏览器访问地址:"+qspEngineServer.getHttpUrl());
        JOptionPane.showMessageDialog(null, "Use browser to(使用浏览器访问地址):"+qspEngineServer.getHttpUrl(), "提示", JOptionPane.DEFAULT_OPTION);
        qspEngineServer.join(); //这个是阻断器

    }
}