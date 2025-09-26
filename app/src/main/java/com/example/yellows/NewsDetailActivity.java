package com.example.yellows;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class NewsDetailActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "news_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        setContentView(webView);

        String url = getIntent().getStringExtra(EXTRA_URL);
        webView.setWebViewClient(new WebViewClient()); // keeps it in-app
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }
}
