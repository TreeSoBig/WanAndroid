package com.example.wanandroid.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.wanandroid.R;
import com.example.wanandroid.adapter.TagAdapter;
import com.example.wanandroid.adapter.TagFlowLayout;
import com.example.wanandroid.bean.CommonWeb;
import com.example.wanandroid.common.UrlConstainer;
import com.example.wanandroid.custom.FlowLayout;
import com.example.wanandroid.utils.HttpUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class SearchActivity extends AppCompatActivity {
    private String mResponseWebData;
    private CommonWeb mCommonWeb;
    private TagFlowLayout mCommonWebFlowLayout;
    private ArrayList<String> mComWebUrls;
    private ArrayList<String> mComWebsites;
    private TextView mTv;
    public static final String TITLE = "title";
    public static final String URL_DATA="url_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mComWebUrls = new ArrayList<>();
        mComWebsites = new ArrayList<>();
        mCommonWebFlowLayout = findViewById(R.id.friend_tagLayout);
        searchWebSiteFromServer(UrlConstainer.baseUrl+UrlConstainer.FRIEND);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    /**
     *从服务器请求常用网站标签
     */
    private void searchWebSiteFromServer(String address) {
        HttpUtils.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(SearchActivity.this,getString(R.string.request_commonWeb_failure),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                mResponseWebData = response.body().string();
                Gson gson = new Gson();
                mCommonWeb = gson.fromJson(mResponseWebData, CommonWeb.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCommonWeb != null && mCommonWeb.getErrorCode() == 0) {
                            for (int i = 0; i < mCommonWeb.getData().size(); i++) {
                                mComWebsites.add(mCommonWeb.getData().get(i).getName());
                                mComWebUrls.add(mCommonWeb.getData().get(i).getLink());
                            }
                            mCommonWebFlowLayout.setAdapter(new TagAdapter<String>(mComWebsites){
                                @Override
                                public View getView(FlowLayout parent, int position, String s)
                                {
                                    mTv = new TextView(SearchActivity.this);
                                    mTv.setTextSize(15);
                                    mTv.setPadding(34,18,34,18);
                                    mTv.setBackgroundResource(R.drawable.tag);
                                    mTv.setText(s);
                                    return mTv;
                                }
                            });
                            mCommonWebFlowLayout.setOnTagClickListener((view, position, parent) -> {
                                String name = mComWebsites.get(position);
                                String Url = mComWebUrls.get(position);
                                Intent intent = new Intent(SearchActivity.this, ArticleDetailsActivity.class);
                                intent.putExtra(TITLE,name);
                                intent.putExtra(URL_DATA,Url);
                                startActivity(intent);
                                return true;
                            });

                        }

                    }
                });
            }
        });
    }
}