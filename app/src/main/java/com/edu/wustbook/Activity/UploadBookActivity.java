package com.edu.wustbook.Activity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.edu.wustbook.R;
import com.edu.wustbook.Tool.HttpConnection;
import com.edu.wustbook.Tool.UploadTask;

public class UploadBookActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatButton submit, cancel;
    private EditText bookname, author, bookprice, saler, phoneNumber, qqnumber;
    private ProgressBar progressBar;
    private View content;

    private int Uid;

    private UploadTask ut;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            submit.setClickable(true);
            progressBar.setVisibility(View.GONE);
            submit.setClickable(true);
            switch (msg.what) {
                case HttpConnection.RESPONSE:
                    boolean flag = (boolean) msg.obj;
                    if (flag) {
                        UploadBookActivity.this.finish();
                    }
                    content.setVisibility(View.VISIBLE);
                    break;
                case HttpConnection.Exception:
                    content.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_excetption), Toast.LENGTH_LONG).show();
                    break;
                case HttpConnection.NORESPONSE:
                    content.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_no_response), Toast.LENGTH_LONG).show();
                    break;
                default:
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);
        //ViewUtils.autoInjectAllFiled(this);
        init();
        Uid = getSharedPreferences("bookstore", 0).getInt("Uid", 0);
        ;

    }

    private void init() {
        submit = (AppCompatButton) findViewById(R.id.submit);
        cancel = (AppCompatButton) findViewById(R.id.cancel);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        content = findViewById(R.id.booksubmitcontent);

        bookname = (EditText) content.findViewById(R.id.bookname);
        author = (EditText) content.findViewById(R.id.author);
        bookprice = (EditText) content.findViewById(R.id.bookprice);
        saler = (EditText) content.findViewById(R.id.saler);
        phoneNumber = (EditText) content.findViewById(R.id.phoneNumber);
        qqnumber = (EditText) content.findViewById(R.id.qqnumber);

        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (ut != null && ut.getStatus() == AsyncTask.Status.RUNNING) {
                ut.cancel(true);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void maybeSubmit() {
        if (ut != null)
            return;
        boolean cancel = false;
        View focusView = null;

        bookname.setError(null);
        bookprice.setError(null);
        saler.setError(null);
        phoneNumber.setError(null);

        String name = bookname.getText().toString();
        String price = bookprice.getText().toString();
        String salername = saler.getText().toString();
        String phonenum = phoneNumber.getText().toString();
        String qqnum = qqnumber.getText().toString();
        if (TextUtils.isEmpty(name)) {
            bookname.setError(getString(R.string.notAllowedEmpty));
            focusView = bookname;
            cancel = true;
        }
        if (TextUtils.isEmpty(price)) {
            bookprice.setError(getString(R.string.notAllowedEmpty));
            focusView = bookprice;
            cancel = true;
        }
        if (TextUtils.isEmpty(salername)) {
            saler.setError(getString(R.string.notAllowedEmpty));
            focusView = saler;
            cancel = true;
        }
        if (TextUtils.isEmpty(phonenum) && TextUtils.isEmpty(qqnum)) {
            phoneNumber.setError(getString(R.string.notAllowedEmpty));
            focusView = phoneNumber;
            cancel = true;
        }
        if (cancel) {
            submit.setClickable(true);
            focusView.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            Sumbit();
        }

    }

    private void Sumbit() {
        ut = new UploadTask(getString(R.string.bookstoreUrl) + "UploadBookServlet", handler);
        ContentValues values = new ContentValues();
        values.put("Uid", Uid);
        values.put("Bname", bookname.getText().toString());
        values.put("author", author.getText().toString());
        values.put("price", bookprice.getText().toString());
        values.put("qq", qqnumber.getText().toString());
        values.put("phoneNumber", phoneNumber.getText().toString());
        values.put("saler", saler.getText().toString());
        ut.execute(values);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                submit.setClickable(false);
                maybeSubmit();
                break;
            case R.id.cancel:
                UploadBookActivity.this.finish();
                break;
        }
    }
}

