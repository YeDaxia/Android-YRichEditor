package io.github.yedaxia.demo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import java.util.List;

import io.github.yedaxia.richeditor.IRichEditor;
import io.github.yedaxia.richeditor.RichTextEditor;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CODE_PERMISSION = 0x12;

    private static final int REQUEST_CODE_SELECT_IMG = 0x11;
    private RichTextEditor richTextEditor;

    private Dialog linkInputDialog;

    private SpHelper spHelper;


    public static void launch(Context context){
        Intent intent = new Intent(context,EditorActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        richTextEditor = (RichTextEditor)findViewById(R.id.rich_editor);
        findViewById(R.id.tv_add_img).setOnClickListener(this);
        findViewById(R.id.tv_bold).setOnClickListener(this);
        findViewById(R.id.tv_heading).setOnClickListener(this);
        findViewById(R.id.tv_paragraph).setOnClickListener(this);
        findViewById(R.id.tv_link).setOnClickListener(this);

        spHelper = new SpHelper(getApplication());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            case R.id.action_new_save:
                onSaveClick();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSaveClick() {
        spHelper.saveContent(richTextEditor.getHtmlContent());
        Toast.makeText(this,"保存成功", Toast.LENGTH_SHORT).show();
        finish();
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
}
