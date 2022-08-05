package com.demo;

import com.qsp.player.util.JarUtil;

import java.io.File;

public class Test2 {
    public static void main(String[] args) {
        System.out.println(JarUtil.getJarPath(Test2.class));
        System.out.println(new File(JarUtil.getJarPath(Test2.class)).getPath());
        System.out.println(new File(JarUtil.getJarPath(Test2.class)).getAbsolutePath());
    }
}
