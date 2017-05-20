package io.github.yedaxia.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/5/20.
 */

public class RawCodeActivity extends AppCompatActivity {

    public static void launch(Context context){
        Intent intent = new Intent(context,RawCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpHelper spHelper = new SpHelper(this);
        TextView tvCode = new TextView(this);
        tvCode.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tvCode.setText(spHelper.getContent());
        setContentView(tvCode);
    }
}
