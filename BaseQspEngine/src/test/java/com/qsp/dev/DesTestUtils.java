package com.qsp.dev;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class DesTestUtils {
    public static void main(String[] args) throws Exception {
        // 明文
        String plainText = "abcd测试";
        System.out.println("明文：" + plainText);

        // 提供原始秘钥:长度64位,8字节
        String originKey = "12345678";
        // 根据给定的字节数组构建一个秘钥
        SecretKeySpec key = new SecretKeySpec(originKey.getBytes(), "DES");

        // 加密
        // 1.获取加密算法工具类
        Cipher cipher = Cipher.getInstance("DES");
        // 2.对工具类对象进行初始化,
        // mode:加密/解密模式
        // key:对原始秘钥处理之后的秘钥
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // 3.用加密工具类对象对明文进行加密
        byte[] encipherByte = cipher.doFinal(plainText.getBytes());
        // 防止乱码，使用Base64编码
        String encode =   Base64.getEncoder().encodeToString(encipherByte);
        System.out.println("加密：" + encode);

        // 解密
        // 2.对工具类对象进行初始化
        cipher.init(Cipher.DECRYPT_MODE, key);
        // 3.用加密工具类对象对密文进行解密
        byte[] decode =  Base64.getDecoder().decode(encode);
        byte[] decipherByte = cipher.doFinal(decode);
        String decipherText = new String(decipherByte);
        System.out.println("解密：" + decipherText);
    }
}
