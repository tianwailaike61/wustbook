package com.edu.wustbook.Activity;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.edu.wustbook.R;
import com.edu.wustbook.Tool.BindView;
import com.edu.wustbook.Tool.HttpConnection;
import com.edu.wustbook.Tool.UploadTask;

public class UploadBookActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(values = {R.id.submit, R.id.cancel})
    private AppCompatButton submit, cancel;
    @BindView(values = {R.id.bookname, R.id.author, R.id.bookprice, R.id.username, R.id.phoneNumber, R.id.qqnumber})
    private TextInputEditText bookname, author, bookprice, username, phonenumber, qqnumber;
    @BindView(values = R.id.progressbar)
    private ProgressBar progressBar;
    @BindView(values = R.id.booksubmitcontent)
    private View content;

    private UploadTask ut;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HttpConnection.RESPONSE:

                    break;
                case HttpConnection.Exception:
                    progressBar.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_excetption), Toast.LENGTH_LONG).show();
                    break;
                case HttpConnection.NORESPONSE:
                    progressBar.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_no_response), Toast.LENGTH_LONG).show();
                    break;
                default:
            }
        }

    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (ut != null && ut.getStatus() == AsyncTask.Status.RUNNING) {
                ut.cancel(true);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                break;
            case R.id.cancel:
                this.finish();
                break;
        }
    }
}

