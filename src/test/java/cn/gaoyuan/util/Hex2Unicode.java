package cn.gaoyuan.util;


import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class Hex2Unicode {


    @Test
    public void test() {

        string2Unicode("我是aa中国人");
    }

    //    这变默认最多5个字算一个字符
    public static double[] string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        double[] result = new double[5];
        for (int i = 0; i < (string.length() <= 5 ? string.length() : 5); i++) {
            // 取出每一个字符
            int c1 = string.charAt(i);
            result[i] = c1;
        }
        return result;
    }


//    public static String string2Unicode(String string) {
//        StringBuffer unicode = new StringBuffer();
//        for (int i = 0; i < string.length(); i++) {
//            // 取出每一个字符
//            char c = string.charAt(i);
//            int c1=string.charAt(i);
//            System.out.println(c+"(Unicode编码对应的数字为：)"+c1);
//            // 转换为unicode
//            unicode.append("\\u" + Integer.toHexString(c));
//            System.out.println(Integer.toHexString(c1));
//        }
//        System.out.println("======*******====");
//        System.out.println(unicode.toString());
//        return unicode.toString();
//    }

}
