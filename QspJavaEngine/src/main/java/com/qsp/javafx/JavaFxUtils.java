package com.qsp.javafx;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author cxy
 */
public class JavaFxUtils {
    public static String showTextInputDialog(String prompt) {
        final ArrayBlockingQueue<String> inputQueue = new ArrayBlockingQueue<>(1);
        new Thread(
                new Task<Void>() {

                    // call方法里面的线程非JavaFX线程
                    @Override
                    protected Void call() throws Exception {
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        TextInputDialog td = new TextInputDialog("");
                        // setHeaderText
                        td.setTitle("请输入"); //设置对话框窗口的标题列文本
                        td.setHeaderText(prompt);
                        td.showAndWait();
                        inputQueue.add(td.getEditor().getText());
                    }

                }).start();
        // launch方法会启动start方法

        try {
            return inputQueue.take();
        } catch (InterruptedException ex) {
            return null;
        }
    }
    public static void showPicture(String imagePath) {
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(new URL(imagePath));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JLabel label = new JLabel(icon);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(label);
        JOptionPane.showMessageDialog(null, panel, "Course",JOptionPane.DEFAULT_OPTION);
    }
    public static void showErrorialog(String prompt) {
        JOptionPane.showMessageDialog(null, prompt, "错误", JOptionPane.ERROR_MESSAGE);
    }
    public static void showAlertDialog(String prompt) {
        showDialog( prompt, Alert.AlertType.INFORMATION);
    }
    private static void showDialog(String prompt, Alert.AlertType alertType )
    {
        new Thread(
                new Task<Void>() {

                    // call方法里面的线程非JavaFX线程
                    @Override
                    protected Void call() throws Exception {
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        final Alert alert2 = new Alert(alertType); // 实体化Alert对话框对象，并直接在建构子设置对话框的消息类型
                        alert2.setTitle("提示"); //设置对话框窗口的标题列文本
                        alert2.setHeaderText(prompt); //设置对话框窗口里的标头文本。若设为空字符串，则表示无标头
                        alert2.show(); //显示对话框，并等待对话框被关闭时才继续运行之后的程序
//                        alert2.showAndWait();

                    }

                }).start();
    }
}
