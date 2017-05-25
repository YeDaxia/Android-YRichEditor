package io.github.yedaxia.demo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.yedaxia.demo.util.YUtils;
import io.github.yedaxia.richeditor.IImageLoader;
import io.github.yedaxia.richeditor.IRichEditor;
import io.github.yedaxia.richeditor.IUploadEngine;
import io.github.yedaxia.richeditor.RichTextEditor;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String KEY_CONTENT = "key_content";

    private static final int REQUEST_CODE_PERMISSION = 0x12;
    private static final int REQUEST_CODE_SELECT_IMG = 0x11;

    private static final int MAX_TRY_UPLOAD_TIME = 10;

    private RichTextEditor richTextEditor;
    private Dialog linkInputDialog;
    private ProgressDialog progressDialog;


    private Handler uiHandler = new Handler();

    private SpHelper spHelper;

    private Map<Uri, AsyncTask> taskMap = new HashMap<>();

    private int tryPostTime;
    private boolean isPosting;

    public static void launch(Context context){
        Intent intent = new Intent(context,EditorActivity.class);
        context.startActivity(intent);
    }

    public static void launch(Context context, String htmlContent){
        Intent intent = new Intent(context,EditorActivity.class);
        intent.putExtra(KEY_CONTENT, htmlContent);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        richTextEditor = (RichTextEditor)findViewById(R.id.rich_editor);
        initRichEditor();

        findViewById(R.id.tv_add_img).setOnClickListener(this);
        findViewById(R.id.tv_bold).setOnClickListener(this);
        findViewById(R.id.tv_heading).setOnClickListener(this);
        findViewById(R.id.tv_paragraph).setOnClickListener(this);
        findViewById(R.id.tv_link).setOnClickListener(this);

        spHelper = new SpHelper(getApplication());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initRichEditor(){

        //设置图片加载器，必须
        richTextEditor.setImageLoader(new IImageLoader() {
            @Override
            public void loadIntoImageView(ImageView imageView, Uri uri) {
                Glide.with(EditorActivity.this).load(uri).centerCrop().into(imageView);
            }
        });

        //设置图片上传，用户一选择图片就开始上传，非必须
        richTextEditor.setUploadEngine(new IUploadEngine() {

            @Override
            public void uploadImage(Uri imgUri, UploadProgressListener listener) {
                UploadImageTask uploadTask = new UploadImageTask(imgUri, listener);
                taskMap.put(imgUri, uploadTask);
                uploadTask.execute();
            }

            @Override
            public void cancelUpload(Uri imgUrl) {
                AsyncTask task = taskMap.get(imgUrl);
                if(task != null && !task.isCancelled()){
                    task.cancel(true);
                }
            }
        });


        String htmlContent = getIntent().getStringExtra(KEY_CONTENT);
        if(!YUtils.isEmpty(htmlContent)){
            richTextEditor.setHtmlContent(htmlContent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_add_img:
                onAddImageClick(v);
                break;
            case R.id.tv_bold:
                onBoldClick(v);
                break;
            case R.id.tv_heading:
                onHeadingClick(v);
                break;
            case R.id.tv_paragraph:
                onParagraphClick(v);
                break;
            case R.id.tv_link:
                onLinkClick(v);
                break;
            default:
                break;
        }
    }

    private void onLinkClick(View v) {
        if(linkInputDialog == null){
            View linkInputView = LayoutInflater.from(this).inflate(R.layout.dialog_link, null);
            final EditText etText = (EditText)linkInputView.findViewById(R.id.et_text);
            final EditText etLink = (EditText)linkInputView.findViewById(R.id.et_link);
            linkInputDialog = new AlertDialog.Builder(this)
                    .setTitle("添加链接")
                    .setView(linkInputView)
                    .setCancelable(true)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = etText.getText().toString().trim();
                            String link = etLink.getText().toString().trim();
                            if(text.isEmpty() || link.isEmpty()){
                                Toast.makeText(getApplication(), "内容不能为空", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            addLinkText(text, link);
                        }
                    }).create();
        }

        linkInputDialog.show();
    }

    private void addLinkText(String text, String link) {
        richTextEditor.insertHyperlink(text, link);
    }

    private void onParagraphClick(View v) {
        richTextEditor.insertParagraph();
    }

    private void onHeadingClick(View v) {
        richTextEditor.insertHeading(IRichEditor.HEADING_1);
    }

    private void onBoldClick(View v) {
        richTextEditor.toggleBoldSelectText();
    }

    private void onAddImageClick(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }else{
            startSelectImageIntent();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMG && resultCode == RESULT_OK) {
            List<Uri> imgUris = Matisse.obtainResult(data);
            for(Uri imgUri : imgUris){
                richTextEditor.insertImage(imgUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(REQUEST_CODE_PERMISSION == requestCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSelectImageIntent();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save_draft:
                onSaveDraftClick();
                break;
            case R.id.action_post:
                onPostClick();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHandler.removeCallbacksAndMessages(null);
        for(AsyncTask task : taskMap.values()){
            task.cancel(true);
        }
    }

    private void onSaveDraftClick() {
        String htmlContent = richTextEditor.getHtmlContent();
        spHelper.saveContent(htmlContent);
        Toast.makeText(this,"保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void onPostClick() {

        String htmlContent = richTextEditor.getHtmlContent();
        if(YUtils.isEmpty(htmlContent)){
            Toast.makeText(this,"内容不能为空～", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isPosting){
            return;
        }

        isPosting = true;
        showLoadingDialog();

        tryAndPostContent();
    }

    private void tryAndPostContent() {
        int uploadStatus = richTextEditor.tryIfSuccessAndReUpload();
        if (IUploadEngine.STATUS_UPLOAD_SUCCESS == uploadStatus) {
            String htmlContent = richTextEditor.getHtmlContent();
            //上传到服务器

            //这里我们简单退出
            Toast.makeText(this,"提交成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (tryPostTime == MAX_TRY_UPLOAD_TIME) {
                Toast.makeText(this,"上传图片超时，请重新提交。", Toast.LENGTH_SHORT).show();
                resetPosting();
                return;
            }
            tryPostTime++;
            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tryAndPostContent();
                }
            }, 1000);
        }
    }

    private void resetPosting() {
        hideLoadingDialog();
        tryPostTime = 0;
        isPosting = false;
    }

    private void startSelectImageIntent(){
        Matisse.from(this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(9)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_SELECT_IMG);
    }

    private void showLoadingDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("提交中");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void hideLoadingDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**
     * 模拟上传图片流程
     */
    private class UploadImageTask extends AsyncTask<Object, Integer, String>{

        private Uri uploadUri;
        private IUploadEngine.UploadProgressListener listener;

        public UploadImageTask(Uri uploadUri, IUploadEngine.UploadProgressListener listener) {
            this.uploadUri = uploadUri;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Object[] params) {
            int progress = 0;
            try{

                //压缩， 处理 ，上传

                while(progress ++ != 100){
                    publishProgress(progress);
                    Thread.sleep(100);
                }
            }catch (InterruptedException ex){
                cancel(true);
            }
            //这里返回你上传后的地址，为了简单处理，我们原本地址
            return uploadUri.toString();
        }

        @Override
        protected void onPostExecute(String url) {
            int[] wh = YUtils.getImageSize(EditorActivity.this, uploadUri);
            listener.onUploadSuccess(uploadUri, url, wh[0], wh[1]);
        }

        @Override
        protected void onCancelled() {
            listener.onUploadFail(uploadUri,"upload fail");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            listener.onUploadProgress(uploadUri, values[0]);
        }
    }
}
