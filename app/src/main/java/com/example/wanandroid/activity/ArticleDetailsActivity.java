package com.example.wanandroid.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.wanandroid.R;

public class ArticleDetailsActivity extends AppCompatActivity {
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        mUrl = "";
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WebView webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        //让WebView支持JavaScript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置是否支持缩放模式
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        // 是否显示+ -
        webSettings.setDisplayZoomControls(false);
        //判断是否存储
        webSettings.setDomStorageEnabled(true);
        mUrl = getIntent().getStringExtra(SearchActivity.URL_DATA);
        String title = getIntent().getStringExtra(SearchActivity.TITLE);
        //设置标题
        TextView advertiseName = findViewById(R.id.bar_title);
        advertiseName.setText(title);
        webView.loadUrl(mUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_share: {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
            break;
            case R.id.toolbar_open:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mUrl));
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;

    }
}