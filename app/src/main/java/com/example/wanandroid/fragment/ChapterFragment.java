package com.example.wanandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.wanandroid.R;
import com.example.wanandroid.adapter.ChapterPagerAdapter;
import com.example.wanandroid.common.UrlConstainer;
import com.example.wanandroid.utils.HttpUtils;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChapterFragment extends Fragment {
    //显示标题
    private TabLayout title;
    private ViewPager mViewPager;
    private List<String> titleList;//标题头的数据
    private List<String> idList;//公众号id的数据
    private String address;
    private List<ChapterDetailsFragment> fragmentList;//ViewPager显示的Fragment
    private String responseData;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter,container,false);
        title = (TabLayout) view.findViewById(R.id.chapter_title);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        address = (UrlConstainer.baseUrl + UrlConstainer.CHAPTERS);
        titleList = new ArrayList<>();
        idList = new ArrayList<>();
        fragmentList = new ArrayList<>();
        requestTitle(address);
    }
    private void requestTitle(String address) {
        HttpUtils.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getActivity(), "获取信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                responseData = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        titleList.add(name);
                        idList.add(String.valueOf(id));
                    }
                    for (int i = 0; i < titleList.size(); i++) {
                        fragmentList.add(new ChapterDetailsFragment(idList.get(i)));
                    }
                    title.setupWithViewPager(mViewPager);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setAdapter(new ChapterPagerAdapter(getChildFragmentManager(), titleList, fragmentList));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
