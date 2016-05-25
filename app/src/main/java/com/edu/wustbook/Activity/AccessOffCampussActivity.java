package com.edu.wustbook.Activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.edu.wustbook.R;
import com.edu.wustbook.Tool.BindView;
import com.edu.wustbook.Tool.ViewUtils;

public class AccessOffCampussActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(values = R.id.webview)
    private WebView webView;
    @BindView(values = {R.id.web_back, R.id.web_forward, R.id.web_close})
    private AppCompatButton web_back, web_forward, web_close;

    private WebChromeClient webChromeClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_off_campuss);

        ViewUtils.autoInjectAllFiled(this);
        web_back= (AppCompatButton) findViewById(R.id.web_back);
        web_back.setOnClickListener(this);
        web_forward= (AppCompatButton) findViewById(R.id.web_forward);
        web_forward.setOnClickListener(this);
        web_close= (AppCompatButton) findViewById(R.id.web_close);
        web_close.setOnClickListener(this);
        WebSettings wSet = webView.getSettings();
        wSet.setJavaScriptEnabled(true);
        wSet.setSupportZoom(true);
        wSet.setBuiltInZoomControls(true);
        wSet.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wSet.setLoadWithOverviewMode(true);
        wSet.setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                web_back.setClickable(true);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!webView.getSettings().getLoadsImagesAutomatically()) {
                    webView.getSettings().setLoadsImagesAutomatically(true);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }

        webChromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

            }
        };
        webView.setWebChromeClient(webChromeClient);
        webView.loadUrl(getString(R.string.AccessOffCampuss_webBaseUrl));

        web_back.setClickable(false);
        web_forward.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.web_back:
                if (webView.canGoBack()) {
                    web_forward.setClickable(true);
                    webView.goBack();
                    web_back.setClickable(webView.canGoBack());
                }
                break;
            case R.id.web_forward:
                if (webView.canGoForward()) {
                    web_back.setClickable(true);
                    webView.goForward();
                    web_forward.setClickable(webView.canGoForward());
                }
                break;
            case R.id.web_close:
                webChromeClient.onCloseWindow(webView);
                AccessOffCampussActivity.this.finish();
                break;
        }
    }
}
