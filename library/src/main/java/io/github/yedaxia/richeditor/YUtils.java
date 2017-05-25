package io.github.yedaxia.richeditor;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 一些工具类
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/5/25.
 */

class YUtils {

    /**
     * 判断空字符串
     * @param text
     * @return
     */
    public static boolean isEmpty(String text){
        return text == null || text.trim().isEmpty();
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
     * 获取图片大小
     * @param uri
     * @return
     */
    public static int[] getImageSize(Context context, Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        final String filePath = getRealPathFromUri(context, uri);
        BitmapFactory.decodeFile(filePath, options);
        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * 是否为本地的文件
     * @param uri
     * @return
     */
    public static boolean isLocalUri(Uri uri){
        final String scheme = uri.getScheme();
        return scheme.startsWith("file") || scheme.startsWith("content");
    }

    /**
     * 获取 uri 的真实路径
     * @param uri
     * @return
     */
    public static  String getRealPathFromUri(Context context, Uri uri) {

        final String scheme = uri.getScheme();

        if(scheme.startsWith("file")){
            return uri.getPath();
        }

        if(scheme.startsWith("content")){
            Cursor cursor = null;
            try {
                String[] proj = { MediaStore.Images.Media.DATA };
                cursor = context.getContentResolver().query(uri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return uri.getPath();
    }
}
