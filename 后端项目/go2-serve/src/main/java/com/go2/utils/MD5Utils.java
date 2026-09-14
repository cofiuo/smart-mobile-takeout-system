package com.go2.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {

    public static String md5(String str){
        return  DigestUtils.md5Hex(str);
    }

    private static final String privateKey="1xc2d34f3p";

    public static String inputPassToNewPass(String pass){


        String newPass= privateKey.charAt(0)+privateKey.charAt(1)+pass +privateKey.charAt(5);
        return newPass;
    }

    public static void main(String[] args) {
        String pass = "123123";
        System.out.println(MD5Utils.md5(MD5Utils.inputPassToNewPass(pass)));
    }
}
