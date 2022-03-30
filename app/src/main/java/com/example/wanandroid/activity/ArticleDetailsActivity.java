package com.example.wanandroid.activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.wanandroid.R;

import java.lang.reflect.Method;

public class ArticleDetailsActivity extends BaseActivity {
    private String Url = "";
    private WebSettings mWebSettings;
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_article_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebSettings = mWebView.getSettings();
        //让WebView支持JavaScript脚本
        mWebSettings.setJavaScriptEnabled(true);
        //设置是否支持缩放模式
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(true);
        // 是否显示+ -
        mWebSettings.setDisplayZoomControls(false);
        //判断是否存储
        mWebSettings.setDomStorageEnabled(true);
        Url = getIntent().getStringExtra("url_data");
        String title = getIntent().getStringExtra("title");
        //设置标题
        setTitle(title);
        mWebView.loadUrl(Url);
    }

   /* @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }*/
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
                sendIntent.putExtra(Intent.EXTRA_TEXT, Url);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
            break;
            case R.id.toolbar_open:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Url));
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