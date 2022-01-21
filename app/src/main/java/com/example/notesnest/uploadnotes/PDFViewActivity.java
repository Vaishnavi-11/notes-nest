package com.example.notesnest.uploadnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.notesnest.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PDFViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        WebView w = findViewById(R.id.web);
        w.getSettings().setJavaScriptEnabled(true);
        Uri url = getIntent().getParcelableExtra("url");
        try {
            w.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + URLEncoder.encode(String.valueOf(url),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        onBackPressed();
    }
}