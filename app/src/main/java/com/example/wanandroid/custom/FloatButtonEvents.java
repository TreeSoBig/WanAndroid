package com.example.wanandroid.custom;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanandroid.application.MyApplication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class FloatButtonEvents {
    public static void listenerFlow(RecyclerView recyclerView, FloatingActionButton floatingActionButton, Context context){
        //向下滑动悬浮按钮消失  向上滑动悬浮按钮显示
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && floatingActionButton.getVisibility() == View.VISIBLE) {
                    floatingActionButton.hide();
                } else {
                    floatingActionButton.show();
                }
            }
        });
        //悬浮按钮点击事件
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "自动滑到最顶端", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //获得recyclerView的线性布局管理器
                        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        //获取到第一个item的显示的下标  不等于0表示第一个item处于不可见状态 说明列表没有滑动到顶部 显示回到顶部按钮
                        int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                        if(firstVisibleItemPosition==0){
                            Toast.makeText(context, "已经位于页面最顶端", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //自动划到最顶端
                            recyclerView.smoothScrollToPosition(0);
                            Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
            }
        });

    }
}
