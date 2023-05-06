package com.example.wanandroid.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.wanandroid.fragment.ChapterDetailsFragment;

import java.util.List;

public class ChapterViewPagerAdapter extends FragmentPagerAdapter {
    private final List<String> mTitleList;//标题头的数据
    private final List<ChapterDetailsFragment> mFragmentList;//ViewPager显示的Fragment

    public ChapterViewPagerAdapter(FragmentManager fm, List<String> titleList, List<ChapterDetailsFragment> fragmentList) {
        super(fm);
        this.mTitleList = titleList;
        this.mFragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
}
