package com.example.wanandroid.adapter;

import android.view.View;

import com.example.wanandroid.custom.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TagAdapter<T> {
    private final List<T> mTagDatas;
    private final HashSet<Integer> mCheckedPosList = new HashSet<>();
    public TagAdapter(List<T> datas)
    {
        mTagDatas = datas;
    }

    interface OnDataChangedListener {}

    void setOnDataChangedListener() {}


    HashSet<Integer> getPreCheckedList()
    {
        return mCheckedPosList;
    }

    public int getCount()
    {
        return mTagDatas == null ? 0 : mTagDatas.size();
    }

    public T getItem(int position)
    {
        return mTagDatas.get(position);
    }

    public abstract View getView(FlowLayout parent, int position, T t);

    public boolean setSelected(int position, T t)
    {
        return false;
    }
}
