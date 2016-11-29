package com.woniukeji.jianguo.utils;

import android.content.Context;

import com.woniukeji.jianguo.base.Constants;

import java.security.MessageDigest;

public class MD5Util {

    public final static String MD5(String s) {  
        char hexDigits[] = { '0', '1', '2', '3', '4',  
                             '5', '6', '7', '8', '9',  
                             'A', 'B', 'C', 'D', 'E', 'F' };
        try {  
            byte[] btInput = s.getBytes();  
     //获得MD5摘要算法的 MessageDigest 对象  
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
     //使用指定的字节更新摘要  
            mdInst.update(btInput);  
     //获得密文  
            byte[] md = mdInst.digest();  
     //把密文转换成十六进制的字符串形式  
            int j = md.length;  
            char str[] = new char[j * 2];  
            int k = 0;  
            for (int i = 0; i < j; i++) {  
                byte byte0 = md[i];  
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];  
                str[k++] = hexDigits[byte0 & 0xf];  
            }  
            return new String(str);  
    }  
        catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }
    public static String getSign(Context context, long timeMillis){
//        String signSecret = "He37o6TaD0N";
//        String token= (String) SPUtils.getParam(context, Constants.LOGIN_INFO,Constants.SP_WQTOKEN,"");
//        String tel= (String) SPUtils.getParam(context, Constants.LOGIN_INFO,Constants.SP_TEL,"");
//        String appid=MD5(tel);
//        String sign=MD5(appid+signSecret+timeMillis+token+"jianguo");
        String signSecret = "He37o6TaD0N";
        String token= (String) SPUtils.getParam(context, Constants.LOGIN_INFO,Constants.SP_WQTOKEN,"");
        String tel= (String) SPUtils.getParam(context, Constants.LOGIN_INFO,Constants.SP_TEL,"");
        String appid=MD5(tel);
        String sign=MD5(appid+signSecret+timeMillis+token+"jianguo");
        LogUtils.e("sign",appid+"+"+signSecret+"+"+timeMillis+"+"+token+"+"+"jianguo");
        LogUtils.e("sign",sign);

        return sign;
    }


}
