package io.github.yedaxia.demo.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2016/11/30
 */

public class StringUtils {

    /**
     * 判断空字符串
     * @param text
     * @return
     */
    public static boolean isEmpty(String text){
        return text == null || text.trim().isEmpty();
    }


    /**
     *
     * @param text
     * @return
     */
    public static String checkNotNull(String text){
        return text== null ?"":text;
    }

    /**
     * 判断空字符串
     * @param text
     * @return
     */
    public static boolean isNotEmpty(String text){
        return text != null && !text.trim().isEmpty();
    }

    /**
     * 提取url中的参数
     * @param url url
     * @return 装着参数的 map
     */
    public static Map<String,String> extractParams(String url){
        if(isEmpty(url)){
            return null;
        }
        try {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int separatorIndex = url.indexOf('?');
        if(separatorIndex == -1){
            return null;
        }

        String paramsStr = url.substring(separatorIndex+1,url.length());
        String[] paramPairs = paramsStr.split("&");
        Map<String,String> paramMap = new HashMap<>(paramPairs.length);
        String[] nameValue;
        for(String param : paramPairs){
            nameValue = param.split("=");
            if(nameValue.length != 2){
                continue;
            }
            paramMap.put(nameValue[0],nameValue[1]);
        }
        return paramMap;
    }

    /**
     * 判断是否是http url
     * @param url
     * @return
     */
    public static boolean isHttp(String url){
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }
    /**
     * 判断是否是file URI
     * @param url
     * @return
     */
    public static boolean isFileUri(String url){
        return url != null && url.startsWith("file:");
    }

    /**
     * 获取可视效果更好的容量大小
     * @param bytes
     * @return
     */
    public static String getPrefStorageSize(long bytes){
        if(bytes >= G_BYTES){
            return String.format("%.2f GB", ((double)bytes)/G_BYTES);
        }else if(bytes >= M_BYTES){
            return String.format("%.1f MB", ((double)bytes)/M_BYTES);
        }else if(bytes >= 1024){
            return String.format("%.1f KB", bytes/1024f);
        }else{
            return String.format("%s B",bytes);
        }
    }

    private static final int M_BYTES = 1024 * 1024;
    private static final int G_BYTES = 1024 * 1024 * 1024;

    public static String getSimpleCountStr(int number, int maxCount){
        int count = 0;
        int tmp = number;
        while (tmp > 0) {
            tmp = tmp / 10;
            count++;
        }
        if(count > maxCount){
            int result = 1;
            for (int i = 0; i < maxCount; i++){
                result = result * 10;
            }
            return String.valueOf(result - 1)+"+";
        }
        return String.valueOf(number);
    }

    /**
     * 过滤html标签
     * @param htmlContent
     * @return
     */
    public static String filterHtml(String htmlContent){
        if(StringUtils.isEmpty(htmlContent)){
            return "";
        }
        return htmlContent.replaceAll("<[^>]+>", "");
    }
}
