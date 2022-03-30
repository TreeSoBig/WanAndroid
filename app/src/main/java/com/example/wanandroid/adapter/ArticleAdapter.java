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

import com.example.wanandroid.activity.ArticleDetailsActivity;
import com.example.wanandroid.R;
import com.example.wanandroid.bean.Article;

import java.util.List;

import com.example.wanandroid.utils.DateUtils;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private List<Article> ArticleList;
    private Context context;
    static class ViewHolder extends RecyclerView.ViewHolder {
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

    public ArticleAdapter(List<Article> articleList) {
        this.ArticleList = articleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_article_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = ArticleList.get(position);
        context = holder.itemView.getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvTitle.setText(Html.fromHtml(article.getTitle(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.tvTitle.setText(Html.fromHtml(article.getTitle()));
        }
        holder.tvTime.setText(DateUtils.parseTime(article.getPublishTime()));
        holder.tvAuthor.setText(article.getAuthor());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ArticleDetailsActivity.class);
                intent.putExtra("url_data", article.getLink());
                intent.putExtra("title", article.getTitle());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return ArticleList.size();
    }

}