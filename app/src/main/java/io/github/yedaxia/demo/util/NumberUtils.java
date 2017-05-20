package io.github.yedaxia.demo.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 数字相关的实用类
 * @author Darcy yeguozhong@yeah.net
 */
public class NumberUtils {

    /**
     * 格式化浮点数,如果小数点后为0，则不保留后面的0
     * @param value 值
     * @param maxDecimal 最多保留的小数点
     * @return formatFloat(1.111,1) = 1.1 ; formatFloat(1.000,1) = 1
     */
    public static String formatJustDouble(double value, int maxDecimal){
        if(maxDecimal == 0){
            return String.valueOf((int)value);
        }
        StringBuilder pattenBuilder = new StringBuilder();
        pattenBuilder.append("0.");
        while(maxDecimal-- != 0){
            pattenBuilder.append('#');
        }
        NumberFormat f = new DecimalFormat(pattenBuilder.toString());
        return f.format(value);
    }

    /**
     * 格式化浮点数,保留decimal位小数点
     * @param value
     * @param decimal
     * @return
     */
    public static String formatReserveDouble(double value,int decimal){
        if(decimal == 0){
            return String.valueOf((int)value);
        }
        StringBuilder pattenBuilder = new StringBuilder();
        pattenBuilder.append("0.");
        while(decimal-- != 0){
            pattenBuilder.append('0');
        }
        NumberFormat f = new DecimalFormat(pattenBuilder.toString());
        return f.format(value);
    }

    /**
     * 转换Str为Int
     * @param numStr
     * @param defaultVal
     * @return
     */
    public static int parseInt(String numStr,int defaultVal){
        if(numStr == null){
            return defaultVal;
        }
        try{
            return Integer.parseInt(numStr.trim());
        }catch (NumberFormatException e){
            return defaultVal;
        }
    }

    /**
     * 转换Str为 long
     * @param numStr
     * @param defaultVal
     * @return
     */
    public static long parseLong(String numStr,long defaultVal){
        if(numStr == null){
            return defaultVal;
        }
        try{
            return Long.parseLong(numStr.trim());
        }catch (NumberFormatException e){
            return defaultVal;
        }
    }

    /**
     * 转换Str为 long
     * @param numStr
     * @param defaultVal
     * @return
     */
    public static float parseFloat(String numStr,float defaultVal){
        if(numStr == null){
            return defaultVal;
        }
        try{
            return Float.parseFloat(numStr.trim());
        }catch (NumberFormatException e){
            return defaultVal;
        }
    }

    /**
     * 获取以100作为基础的进度数
     * @param total
     * @param progress
     * @return
     */
    public static int getIProgress(double total,double progress){
        return (int)(progress / total * 100);
    }
}
