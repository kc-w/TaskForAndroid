package com.example.taskforandroid.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.List;

//片段适配器
public class FragmentAdapter extends FragmentPagerAdapter {



    List<Fragment> fragmentlist;
    FragmentManager fm;
    private String[] titles = {"进行中", "待批准", "已完成","延期中","我的事件"};

    public FragmentAdapter(FragmentManager fm,List<Fragment> fragmentlist) {
        super(fm);
        this.fm=fm;
        this.fragmentlist=fragmentlist;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentlist.get(position);
    }

    @Override
    public int getCount() {
        return fragmentlist.size();
    }

    //重写这个方法，将设置每个Tab的标题
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
