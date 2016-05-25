package com.edu.wustbook.Activity;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.edu.wustbook.R;
import com.edu.wustbook.Tool.BindView;
import com.edu.wustbook.Tool.ViewUtils;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(values = {R.id.libary, R.id.bookstore, R.id.collection, R.id.setting})
    private CardView libary, bookstore, collection, setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ViewUtils.autoInjectAllFiled(this);
        addListener();
    }

    private void addListener() {
        libary.setOnClickListener(this);
        bookstore.setOnClickListener(this);
        collection.setOnClickListener(this);
        setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.libary:
                intent.setClass(HomeActivity.this, LibaryActivity.class);
                break;
            case R.id.bookstore:
                intent.setClass(HomeActivity.this, BaseActivity.class);
                break;
            case R.id.collection:
                intent.setClass(HomeActivity.this, CollectionActivity.class);
                break;
            case R.id.setting:

                break;
            default:
        }
        startActivity(intent);
    }
}
