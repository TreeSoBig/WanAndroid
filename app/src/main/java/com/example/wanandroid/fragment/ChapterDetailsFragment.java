package com.example.wanandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wanandroid.R;
import com.example.wanandroid.adapter.ArticleListAdapter;
import com.example.wanandroid.application.MyApplication;
import com.example.wanandroid.common.UrlConstainer;
import com.example.wanandroid.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChapterDetailsFragment extends Fragment {
    public static RecyclerView mRecyclerView;
    private String mResponseData;
    private ArticleListAdapter mArticleListAdapter;
    private final String mId;
    private SwipeRefreshLayout mRefresh;
    private int page = 0;
    private final List<String> mArticleTitleList = new ArrayList<>();
    private final List<String> mArticleLinkList = new ArrayList<>();
    private final List<Long> mArticleTimeList = new ArrayList<>();
    private final List<String> mArticleAuthorList = new ArrayList<>();

    public ChapterDetailsFragment(String Id) {
        mId = Id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter_details, container, false);
        mRecyclerView =  view.findViewById(R.id.recycler_view);
        mRefresh =  view.findViewById(R.id.swipeRefresh);
        mRefresh.setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary);
        mRefresh.setOnRefreshListener(() -> {
            page++;
            refresh();
            mRefresh.setRefreshing(false);
            Toast.makeText(getActivity(), getString(R.string.refresh_success), Toast.LENGTH_SHORT).show();
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refresh();
    }

    private void refresh() {
        HttpUtils.sendOKHttpRequest(UrlConstainer.baseUrl + "wxarticle/list/" + mId + "/" + page + "/json", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MyApplication.getContext());
                        mRecyclerView.setLayoutManager(layoutManager);
                        mArticleListAdapter = new ArticleListAdapter(mArticleTitleList, mArticleAuthorList, mArticleLinkList, mArticleTimeList);
                        mRecyclerView.setAdapter(mArticleListAdapter);
                    }
                });
            }
        });
    }
}
