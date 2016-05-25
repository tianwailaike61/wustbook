package com.edu.wustbook.Activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.edu.wustbook.R;
import com.edu.wustbook.Tool.BindView;

public class UploadBookActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(values = {R.id.submit, R.id.cancel})
    private AppCompatButton submit, cancel;
    @BindView(values = {R.id.bookname, R.id.bookps, R.id.bookprice, R.id.username, R.id.phonenumber, R.id.qqnumber})
    private TextInputEditText bookname, bookps, bookprice, username, phonenumber, qqnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                break;
            case R.id.cancel:
                this.finish();
                break;
        }
    }
}
