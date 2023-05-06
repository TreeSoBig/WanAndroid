package com.example.wanandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanandroid.R;
import com.example.wanandroid.activity.ArticleDetailsActivity;
import com.example.wanandroid.activity.SearchActivity;
import com.example.wanandroid.utils.DateUtils;

import java.util.List;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {
        private final List<String> mArticleTitleList;
        private final List<String> mArticleAuthorList;
        private final List<String> mArticleLinkList;
        private final List<Long> mArticleTimeList;
        private Context mContext;
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvAuthor;
            TextView tvTitle;
            TextView tvTime;
            public ViewHolder(View view) {
                super(view);
                tvAuthor = view.findViewById(R.id.tv_author);
                tvTitle =  view.findViewById(R.id.tv_title);
                tvTime =  view.findViewById(R.id.tv_time);
            }
        }
        public ArticleListAdapter(List<String> articleTitleList, List<String> articleAuthorList, List<String>articleLinkList, List<Long>articleTimeList) {
            this.mArticleTitleList = articleTitleList;
            this.mArticleAuthorList = articleAuthorList;
            this.mArticleLinkList = articleLinkList;
            this.mArticleTimeList = articleTimeList;
        }
        @NonNull
        @Override
        public ArticleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_article_list, parent, false);
            ArticleListAdapter.ViewHolder holder = new ArticleListAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ArticleListAdapter.ViewHolder holder, int position) {
            String title = mArticleTitleList.get(position);
            String author = mArticleAuthorList.get(position);
            String link = mArticleLinkList.get(position);
            Long time = mArticleTimeList.get(position);
            mContext = holder.itemView.getContext();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.tvTitle.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.tvTitle.setText(Html.fromHtml(title));
            }
            holder.tvTime.setText(DateUtils.parseTime(time));
            holder.tvAuthor.setText(author);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ArticleDetailsActivity.class);
                    intent.putExtra(SearchActivity.URL_DATA, link);
                    intent.putExtra(SearchActivity.TITLE,title);
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mArticleLinkList.size();
        }
    }

