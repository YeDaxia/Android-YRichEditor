package io.github.yedaxia.richeditor;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.bumptech.glide.Glide;

/**
 * 这只是一个简单的ImageView，可以存放Bitmap和Path等信息
 *
 * @author xmuSistone
 */
public class EditImageView extends AppCompatImageView
        implements IUploadEngine.UploadProgressListener{

    private IUploadEngine uploadEngine;
    private Uri mUri;
    private String uploadUrl;

    private int uploadStatus = IUploadEngine.STATUS_UPLOAD_INITIAL;

    private Drawable mProgressDrawable;
    private Rect mProgressRect;
    private int progress;

    private int uploadImageWidth;
    private int uploadImageHeight;

    public EditImageView(Context context) {
        this(context, null);
    }

    public EditImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mProgressRect = new Rect();
        mProgressDrawable = new ColorDrawable(0x55000000);
    }

    void setUploadEngine(IUploadEngine engine){
        this.uploadEngine = engine;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mProgressDrawable != null && progress !=0 && progress != 100){
            int width = getWidth();
            mProgressRect.top = 0;
            mProgressRect.bottom = getHeight();
            mProgressRect.right = (int)((width * progress / 100f));
            mProgressDrawable.setBounds(mProgressRect);
            mProgressDrawable.draw(canvas);
        }
    }

    /**
     * 设置和上传图片
     * @param uri
     */
    public void setImageAndUpload(@Nullable final Uri uri){
        this.mUri = uri;
        doUpload();
        post(new Runnable() {
            @Override
            public void run() {
                resizeImageView(uri);
            }
        });
    }

    private void resizeImageView(Uri uri){
        final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight;
        if(uri == null || !uri.toString().startsWith("file://")){
            viewHeight = (int)(viewWidth * 0.5);
        }else{
            final int[] imgWH = getImageSize(uri);
            final float whProp = ((float)imgWH[1]) /imgWH[0];
            viewHeight = (int)(whProp > 2 ? viewWidth * 2 : viewWidth * whProp);
        }
        getLayoutParams().height = viewHeight;
        requestLayout();
        Glide.with(getContext()).load(uri).centerCrop().override(viewWidth,viewHeight).into(this);
    }

    /**
     * 发起上传
     */
    public void doUpload(){
        if(uploadEngine != null && mUri != null && mUri.toString().startsWith("file://")
                && (uploadStatus == 0 || uploadStatus == IUploadEngine.STATUS_UPLOAD_FAIL)){
            Log.i("EditImageView", "upload img start..." + mUri.getPath());
            uploadStatus = IUploadEngine.STATUS_UPLOAD_START;
            uploadEngine.uploadImage(mUri, this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelUpload();
    }

    /**
     * 取消上传
     */
    public void cancelUpload(){
        if(uploadStatus == IUploadEngine.STATUS_UPLOAD_START ||uploadStatus == IUploadEngine.STATUS_UPLOADING){
            uploadEngine.cancelUpload(mUri);
        }
    }

    @Override
    public final void onUploadProgress(Uri imgUri, int progress) {
        uploadStatus = IUploadEngine.STATUS_UPLOADING;
        this.progress = progress;
        invalidate();
    }

    @Override
    public void onUploadSuccess(Uri imgUri, String url, int width, int height) {
        uploadStatus = IUploadEngine.STATUS_UPLOAD_SUCCESS;
        this.uploadUrl = url;
        this.progress = 100;
        this.uploadImageWidth = width;
        this.uploadImageHeight = height;
        invalidate();
        Log.i("EditImageView", "upload success... " + url);
    }

    @Override
    public final void onUploadFail(Uri imgUri, String errorMsg) {
        uploadStatus = IUploadEngine.STATUS_UPLOAD_FAIL;
        Log.i("EditImageView", "upload fail ... " + imgUri);
    }

    /**
     * 获取上传的状态
     * @return
     */
    public int getUploadStatus(){
        return uploadStatus;
    }

    /**
     * 获取最终生成的html
     * @return
     */
    public String getHtml(){
        final String imgSrc = uploadUrl == null ? mUri.toString() : uploadUrl;
        return String.format("<img src=\"%s\" width=\"%d\" height=\"%d\"/>",imgSrc,uploadImageWidth,uploadImageHeight);
    }

    /**
     * 获取图片大小
     * @param uri
     * @return
     */
    private int[] getImageSize(Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        final String uriString = uri.toString();
        final String filePath = uriString.substring(uriString.indexOf("://") + 3);
        BitmapFactory.decodeFile(filePath, options);
        return new int[]{options.outWidth, options.outHeight};
    }
}
