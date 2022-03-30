package com.example.wanandroid.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.wanandroid.custom.FloatButtonEvents;
import com.example.wanandroid.R;
import com.example.wanandroid.adapter.ArticleAdapter;
import com.example.wanandroid.bean.Article;
import com.example.wanandroid.bean.Advertise;
import com.example.wanandroid.common.UrlConstainer;
import com.example.wanandroid.utils.HttpUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    String responseData = "";
    private List<Article> articleData;
    private SwipeRefreshLayout swipeRefresh;
    private ArticleAdapter articleAdapter;
    private RecyclerView mRecyclerView;
    private FloatingActionButton scrollBtnTop;
    private int page = 0;
    private ViewPager mViewPager;
    private List<AdvertiseFragment> advertiseFragmentList;
    private List<Advertise> advertiseData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fargment_home,container,false);
        if(articleData == null){
            articleData = new ArrayList<>();
        }
        mViewPager =  view.findViewById(R.id.viewPager);
        mRecyclerView =  view.findViewById(R.id.recycler_view);
        queryFromServer(UrlConstainer.baseUrl + UrlConstainer.HOME_LIST.replace("{page}", String.valueOf(page)));
        page++;
        queryFromBannerServer(UrlConstainer.baseUrl + UrlConstainer.MAIN_BANNER);
        advertiseFragmentList = new ArrayList<>();
        scrollBtnTop = view.findViewById(R.id.btn_scroll_top);
        //刷新recyclerview
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary);
        swipeRefresh.setOnRefreshListener(() -> {
            refreshArticle(UrlConstainer.baseUrl + UrlConstainer.HOME_LIST.replace("{page}", String.valueOf(page)));
            page++;
        });
        FloatButtonEvents.listenerFlow(mRecyclerView, scrollBtnTop,getActivity());
        return view;
    }

    private void queryFromBannerServer(String address) {
        HttpUtils.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "首页广告加载失败", Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("ResourceType")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                responseData = response.body().string();
                advertiseData = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        String imagePath = jsonObject.getString("imagePath");
                        String text = jsonObject.getString("title");
                        String link = jsonObject.getString("url");
                        Advertise advertise = new Advertise(imagePath, text, link);
                        advertiseData.add(advertise);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 3; i++) {
                                AdvertiseFragment viewpager_fragment = new AdvertiseFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("Url", advertiseData.get(i).getImagePath());
                                bundle.putString("title", advertiseData.get(i).getTitle());
                                bundle.putString("link", advertiseData.get(i).getUrl());
                                viewpager_fragment.setArguments(bundle);
                                advertiseFragmentList.add(viewpager_fragment);
                            }
                            mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
                                @NonNull
                                @Override
                                public Fragment getItem(int position) {
                                    return advertiseFragmentList.get(position);
                                }

                                @Override
                                public int getCount() {
                                    return advertiseFragmentList.size();
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * @param address 刷新文章列表
     */
    public void refreshArticle(String address) {
        HttpUtils.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "获取文章信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                responseData = response.body().string();
                parseJSON(responseData);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!articleData.isEmpty()) {
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            mRecyclerView.setLayoutManager(layoutManager);
                            articleAdapter = new ArticleAdapter(articleData);
                            articleAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "获取文章信息失败", Toast.LENGTH_SHORT).show();
                        }
                        //表示刷新事件结束
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(getActivity(), "刷新成功，您又获取了一页文章", Toast.LENGTH_SHORT).show();

                    }
                });
            }

        });
    }

    private void queryFromServer(String address) {
        HttpUtils.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "获取文章信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                responseData = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseJSON(responseData);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        mRecyclerView.setLayoutManager(layoutManager);
                        articleAdapter = new ArticleAdapter(articleData);
                        mRecyclerView.setAdapter(articleAdapter);
                    }
                });
            }
        });
    }
    private void parseJSON(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = jsonObject.getJSONArray("datas");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String author = jsonObject.getString("author");
                String link = jsonObject.getString("link");
                long publishTime = jsonObject.getLong("publishTime");
                Article article = new Article(title, author, link, publishTime);
                articleData.add(article);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
