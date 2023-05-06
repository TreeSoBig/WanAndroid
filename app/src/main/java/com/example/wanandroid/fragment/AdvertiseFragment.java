package com.example.wanandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.wanandroid.activity.AdvertiseDetailsActivity;
import com.example.wanandroid.R;
import com.example.wanandroid.custom.UrlImageView;
import com.example.wanandroid.utils.ThreadPoolManager;

public class AdvertiseFragment extends Fragment implements View.OnClickListener{
    private String mTitle;
    private String mLink;
    public static final String AD_DATA = "ad_data";
    public static final String AD_TITLE = "ad_title";

    public AdvertiseFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advertise, container, false);
        UrlImageView advertiseImg = view.findViewById(R.id.img_advertise);
        TextView advertiseTitle = view.findViewById(R.id.tv_advertise);
        Bundle arguments = getArguments();
        String Url = arguments.getString(HomeFragment.URL);
        mTitle = arguments.getString(HomeFragment.TITLE);
        mLink = arguments.getString(HomeFragment.LINK);
        advertiseImg.setImageURL(Url);
        advertiseTitle.setText(mTitle);
        advertiseImg.setOnClickListener(this);
        return view;
    }

    //广告图片的点击事件
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), AdvertiseDetailsActivity.class);
        intent.putExtra(AD_DATA,mLink);
        intent.putExtra(AD_TITLE,mTitle);
        startActivity(intent);
    }
}
