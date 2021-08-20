package com.leo.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;


/**
 * @author Leo
 * @create 2021-08-09 16:30
 */
@Component
public class MD5Utils {

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt="1a2b3c4d";

    //一次加密
    public static String inputPassToFromPass(String inputPass){
        String str = "" + salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    //二次加密
    public static String formPassToDBPass(String formPass,String salt){
        String str = "" + salt.charAt(0)+salt.charAt(4)+formPass+salt.charAt(2)+salt.charAt(3);
        return md5(str);
    }

    //真正需要的
    public static String inputPassToDBPass(String inputPass,String salt){
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = formPassToDBPass(fromPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
        //ce21b747de5af71ab5c2e20ff0a60eea
        System.out.println(inputPassToFromPass("123456"));
        System.out.println(formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9", "1a2b3c4d"));
        //8390566950607e1497e3765fd5a104c1
        System.out.println(inputPassToDBPass("123456", "1a2b3c4d"));
    }

}
