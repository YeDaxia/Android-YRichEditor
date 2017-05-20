package io.github.yedaxia.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.List;

import io.github.yedaxia.demo.adapter.HtmlAdapter;
import io.github.yedaxia.demo.html.HtmlParser;
import io.github.yedaxia.demo.html.IHtmlElement;


/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/5/20.
 */

public class ShowHtmlActivity extends AppCompatActivity {

    public static void launch(Context context){
        Intent intent = new Intent(context,ShowHtmlActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_html);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SpHelper spHelper = new SpHelper(this);
        List<IHtmlElement> htmlElementList =  HtmlParser.parse(spHelper.getContent());
        recyclerView.setAdapter(new HtmlAdapter(this, htmlElementList));
    }
}
