package com.example.wanandroid.activity;

import androidx.annotation.NonNull;

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


public class SearchActivity extends BaseActivity {
    private String responseWebData;
    private CommonWeb commonWeb;
    private TagFlowLayout commonWebFlowLayout;
    private ArrayList<String> comWebUrls;
    private ArrayList<String> comWebsites;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        if(comWebUrls==null){
            comWebUrls = new ArrayList<>();
        }
        if(comWebsites==null){
            comWebsites = new ArrayList<>();
        }
        commonWebFlowLayout = (TagFlowLayout)findViewById(R.id.friend_tagLayout);
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
                Toast.makeText(SearchActivity.this,"请求搜索热词服务失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                responseWebData = response.body().string();
                Log.d("TAG", "onResponse: "+responseWebData);
                Gson gson = new Gson();
                commonWeb = gson.fromJson(responseWebData, CommonWeb.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (commonWeb != null && commonWeb.getErrorCode() == 0) {
                            for (int i = 0; i < commonWeb.getData().size(); i++) {
                                comWebsites.add(commonWeb.getData().get(i).getName());
                                comWebUrls.add(commonWeb.getData().get(i).getLink());
                            }
                            commonWebFlowLayout.setAdapter(new TagAdapter<String>(comWebsites){
                                @Override
                                public View getView(FlowLayout parent, int position, String s)
                                {
                                    tv = new TextView(SearchActivity.this);
                                    tv.setTextSize(15);
                                    tv.setPadding(34,18,34,18);
                                    tv.setBackgroundResource(R.drawable.tag);
                                    tv.setText(s);
                                    return tv;
                                }
                            });
                            commonWebFlowLayout.setOnTagClickListener((view, position, parent) -> {
                                //Toast.makeText(SearchActivity.this, comWebsites.get(position), Toast.LENGTH_SHORT).show();
                                String name = comWebsites.get(position);
                                String Url = comWebUrls.get(position);
                                Intent intent = new Intent(SearchActivity.this, ArticleDetailsActivity.class);
                                intent.putExtra("title",name);
                                intent.putExtra("url_data",Url);
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