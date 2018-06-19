package io.wherevere.know.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author wherevere
 * @version 1.0
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;
    private String[] mTitle;

    public PagerAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] title) {
        super(fm);
        this.mFragmentList = fragmentList;
        this.mTitle = title;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }
}
