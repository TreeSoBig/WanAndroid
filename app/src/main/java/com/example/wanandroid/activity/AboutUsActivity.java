package com.example.wanandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.example.wanandroid.R;
import com.example.wanandroid.utils.AppUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class AboutUsActivity extends AppCompatActivity {
    private TextView mVersionView;
    private TextView mIntroduceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        CollapsingToolbarLayout mCollapsingToolbarLayout = findViewById(R.id.collapsingbarlayout);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.about_us);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置展开后的字体颜色
        mCollapsingToolbarLayout.setExpandedTitleTextColor(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white)));
        //设置收缩后的字体颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white)));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mVersionView =  findViewById(R.id.version);
        mIntroduceView =  findViewById(R.id.introduce);
        setVersion();
        setIntroduce();

    }

    private void setIntroduce() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mIntroduceView.setText(Html.fromHtml(getString(R.string.about_us_introduce), Html.FROM_HTML_MODE_LEGACY));
        } else {
            mIntroduceView.setText(Html.fromHtml(getString(R.string.about_us_introduce)));
        }
        //设置跳转
        mIntroduceView.setMovementMethod(LinkMovementMethod.getInstance());

    }
    //设置版本
    private void setVersion() {
        String mVersionFormat = getString(R.string.version_format);
        String mVersionName = AppUtils.getVersionName(this);
        String mAppName = getString(R.string.app_name);
        String mVersionStr = String.format(mVersionFormat, mAppName, mVersionName);
        mVersionView.setText(mVersionStr);
    }
}