package com.qsp.player.javafx;

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
    private static final Frame frame=new Frame(){{this.setAlwaysOnTop(true);}};
    public static String showTextInputDialog(String prompt) {
            String input= JOptionPane.showInputDialog(frame,prompt);
            if(input==null)
            {
                return "";
            }else
            {
                return input;
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
        JOptionPane.showMessageDialog(frame, panel, "Course",JOptionPane.DEFAULT_OPTION);
    }
    public static void showErrorialog(String prompt) {
        JOptionPane.showMessageDialog(frame, prompt, "错误", JOptionPane.ERROR_MESSAGE);
    }
    public static void showAlertDialog(String prompt) {
        JOptionPane.showMessageDialog(frame, prompt, "提示", JOptionPane.WARNING_MESSAGE);
    }
}
