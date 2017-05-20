package io.github.yedaxia.richeditor;

import android.net.Uri;

/**
 * 上传本地图片
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/5/9.
 */

public interface IUploadEngine {

    int STATUS_UPLOAD_INITIAL = 0;
    int STATUS_UPLOAD_START = 1;
    int STATUS_UPLOADING = 2;
    int STATUS_UPLOAD_SUCCESS = 3;
    int STATUS_UPLOAD_FAIL = 4;

    /**
     * 上传图片
     * @param imgUri
     * @param listener
     */
    void uploadImage(Uri imgUri, UploadProgressListener listener);

    /**
     * 取消上传
     * @param imgUrl
     */
    void cancelUpload(Uri imgUrl);

    interface UploadProgressListener{

        /**
         * 上传进度回调
         * @param imgUri
         * @param progress
         */
        void onUploadProgress(Uri imgUri, int progress);

        /**
         * 上传成功回调
         * @param imgUri
         * @param url
         */
        void onUploadSuccess(Uri imgUri, String url, int width, int height);

        /**
         * 上传失败回调
         * @param imgUri
         * @param errorMsg
         */
        void onUploadFail(Uri imgUri, String errorMsg);
    }
}
