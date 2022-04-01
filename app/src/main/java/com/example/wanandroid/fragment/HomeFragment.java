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

import com.example.wanandroid.adapter.ArticleListAdapter;
import com.example.wanandroid.custom.FloatButtonEvents;
import com.example.wanandroid.R;
import com.example.wanandroid.bean.Advertise;
import com.example.wanandroid.common.UrlConstainer;
import com.example.wanandroid.utils.HttpUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private String mResponseData;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private int page = 0;
    private ViewPager mViewPager;
    private List<AdvertiseFragment> mAdvertiseFragmentList;
    private List<Advertise> mAdvertiseData;
    private final List<String> mArticleTitleList = new ArrayList<>();
    private final List<String> mArticleLinkList = new ArrayList<>();
    private final List<Long> mArticleTimeList = new ArrayList<>();
    private final List<String> mArticleAuthorList = new ArrayList<>();
    private ArticleListAdapter mArticleListAdapter;
    public final static String URL="Url";
    public final static String TITLE="title";
    public final static String LINK="link";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fargment_home,container,false);
        mViewPager =  view.findViewById(R.id.viewPager);
        mRecyclerView =  view.findViewById(R.id.recycler_view);
        queryFromServer(UrlConstainer.baseUrl + UrlConstainer.HOME_LIST.replace("{page}", String.valueOf(page)));
        page++;
        queryFromBannerServer(UrlConstainer.baseUrl + UrlConstainer.MAIN_BANNER);
        mAdvertiseFragmentList = new ArrayList<>();
        FloatingActionButton scrollBtnTop = view.findViewById(R.id.btn_scroll_top);
        mSwipeRefresh = view.findViewById(R.id.swipeRefresh);
        mSwipeRefresh.setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary);

        //刷新recyclerList
        mSwipeRefresh.setOnRefreshListener(() -> {
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
                Toast.makeText(getActivity(), getString(R.string.get_advertise_failure), Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("ResourceType")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                mResponseData = response.body().string();
                mAdvertiseData = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(mResponseData);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        String imagePath = jsonObject.getString("imagePath");
                        String text = jsonObject.getString("title");
                        String link = jsonObject.getString("url");
                        Advertise advertise = new Advertise(imagePath, text, link);
                        mAdvertiseData.add(advertise);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 3; i++) {
                                AdvertiseFragment viewpager_fragment = new AdvertiseFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString(URL, mAdvertiseData.get(i).getImagePath());
                                bundle.putString(TITLE, mAdvertiseData.get(i).getTitle());
                                bundle.putString(LINK, mAdvertiseData.get(i).getUrl());
                                viewpager_fragment.setArguments(bundle);
                                mAdvertiseFragmentList.add(viewpager_fragment);
                            }
                            mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
                                @NonNull
                                @Override
                                public Fragment getItem(int position) {
                                    return mAdvertiseFragmentList.get(position);
                                }

                                @Override
                                public int getCount() {
                                    return mAdvertiseFragmentList.size();
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
                        Toast.makeText(getActivity(), getString(R.string.get_page_failure), Toast.LENGTH_SHORT).show();
                        mSwipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                mResponseData = response.body().string();
                JSONObject jsonObject;
                try{
                    jsonObject = new JSONObject(mResponseData);
                    jsonObject = jsonObject.getJSONObject("data");
                    JSONArray jsonArray = jsonObject.getJSONArray("datas");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        mArticleTitleList.add(jsonObject.getString("title"));
                        mArticleAuthorList.add(jsonObject.getString("author"));
                        mArticleLinkList.add(jsonObject.getString("link"));
                        mArticleTimeList.add(jsonObject.getLong("publishTime"));
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mArticleTitleList.isEmpty()) {
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            mRecyclerView.setLayoutManager(layoutManager);
                            mArticleListAdapter = new ArticleListAdapter(mArticleTitleList, mArticleAuthorList, mArticleLinkList, mArticleTimeList);
                            mArticleListAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.get_page_failure), Toast.LENGTH_SHORT).show();
                        }
                        //表示刷新事件结束
                        mSwipeRefresh.setRefreshing(false);
                        Toast.makeText(getActivity(), getString(R.string.refresh_success), Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getActivity(), getString(R.string.get_page_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                mResponseData = response.body().string();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(mResponseData);
                    jsonObject = jsonObject.getJSONObject("data");
                    JSONArray jsonArray = jsonObject.getJSONArray("datas");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        mArticleTitleList.add(jsonObject.getString("title"));
                        mArticleAuthorList.add(jsonObject.getString("author"));
                        mArticleLinkList.add(jsonObject.getString("link"));
                        mArticleTimeList.add(jsonObject.getLong("publishTime"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        mRecyclerView.setLayoutManager(layoutManager);
                        mArticleListAdapter = new ArticleListAdapter(mArticleTitleList, mArticleAuthorList, mArticleLinkList, mArticleTimeList);
                        mRecyclerView.setAdapter(mArticleListAdapter);
                    }
                });
            }
        });
    }
}
