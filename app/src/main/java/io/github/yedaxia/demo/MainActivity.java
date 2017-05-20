package io.github.yedaxia.demo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.github.yedaxia.demo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    SpHelper spHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spHelper = new SpHelper(this);

        findViewById(R.id.tv_raw_code).setOnClickListener(this);
        findViewById(R.id.tv_rich_list).setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditorActivity.launch(MainActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if("".equals(spHelper.getContent())){
            findViewById(R.id.tv_raw_code).setVisibility(View.GONE);
            findViewById(R.id.tv_rich_list).setVisibility(View.GONE);
        }else{
            findViewById(R.id.tv_raw_code).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_rich_list).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_raw_code:
                RawCodeActivity.launch(this);
                break;
            case R.id.tv_rich_list:
                ShowHtmlActivity.launch(this);
                break;
        }
    }
}
