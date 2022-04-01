package com.example.wanandroid.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.wanandroid.R;
import com.example.wanandroid.fragment.AdvertiseFragment;

public class AdvertiseDetailsActivity extends AppCompatActivity {
    private String mUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        mUrl = "";
        WebView webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        mUrl = getIntent().getStringExtra(AdvertiseFragment.AD_DATA);
        String title = getIntent().getStringExtra(AdvertiseFragment.AD_TITLE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView articleName = findViewById(R.id.bar_title);
        articleName.setText(title);
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