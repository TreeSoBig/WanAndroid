package com.example.wanandroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wanandroid.activity.ArticleDetailsActivity;
import com.example.wanandroid.R;
import com.example.wanandroid.application.MyApplication;
import com.example.wanandroid.bean.Article;
import com.example.wanandroid.common.UrlConstainer;
import com.example.wanandroid.utils.DateUtils;
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
    private String responseData;
    private List<Article> chapterList = new ArrayList<>();
    private pubNumAdapter mpubNumAdapter;
    private String mId;
    private SwipeRefreshLayout mrefresh;
    private int page = 0;
    public ChapterDetailsFragment(String Id) {
        mId = Id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter_details, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mrefresh = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefresh);
        mrefresh.setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary);
        mrefresh.setOnRefreshListener(() -> {
            page++;
            refresh();
            mrefresh.setRefreshing(false);
            Toast.makeText(getActivity(), "刷新成功，您又获取了一页文章", Toast.LENGTH_SHORT).show();
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refresh();
    }
private void refresh(){
    HttpUtils.sendOKHttpRequest(UrlConstainer.baseUrl + "wxarticle/list/" +mId+ "/"+page+"/json", new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Toast.makeText(getActivity(), "获取页面失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            responseData = response.body().string();
            parseJSON(responseData);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MyApplication.getContext());
                    mRecyclerView.setLayoutManager(layoutManager);
                    mpubNumAdapter = new pubNumAdapter(chapterList);
                    mRecyclerView.setAdapter(mpubNumAdapter);
                }
            });
        }
    });
}
    class pubNumAdapter extends RecyclerView.Adapter<pubNumAdapter.ViewHolder> {
        private List<Article> ChapterList;
        private Context context;
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvAuthor;
            TextView tvTitle;
            TextView tvTime;
            public ViewHolder(View view) {
                super(view);
                tvAuthor = (TextView) view.findViewById(R.id.tv_author);
                tvTitle = (TextView) view.findViewById(R.id.tv_title);
                tvTime = (TextView) view.findViewById(R.id.tv_time);
            }
        }
        public pubNumAdapter(List<Article> ChapterList) {
            this.ChapterList = ChapterList;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_article_list, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull pubNumAdapter.ViewHolder holder, int position) {
            Article chapter = ChapterList.get(position);
            context = holder.itemView.getContext();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.tvTitle.setText(Html.fromHtml(chapter.getTitle(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.tvTitle.setText(Html.fromHtml(chapter.getTitle()));
            }
            holder.tvTime.setText(DateUtils.parseTime(chapter.getPublishTime()));
            holder.tvAuthor.setText(chapter.getAuthor());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ArticleDetailsActivity.class);
                    intent.putExtra("url_data", chapter.getLink());
                    intent.putExtra("title", chapter.getTitle());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ChapterList.size();
        }
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
                chapterList.add(article);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
