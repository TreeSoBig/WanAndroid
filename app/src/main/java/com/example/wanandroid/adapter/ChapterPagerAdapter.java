package com.example.wanandroid.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.wanandroid.fragment.ChapterDetailsFragment;

import java.util.List;

public class ChapterPagerAdapter extends FragmentPagerAdapter {
    private List<String> titleList;//标题头的数据
    private List<ChapterDetailsFragment> fragmentList;//ViewPager显示的Fragment

    public ChapterPagerAdapter(FragmentManager fm, List<String> titleList, List<ChapterDetailsFragment> fragmentList) {
        super(fm);
        this.titleList = titleList;
        this.fragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
}
