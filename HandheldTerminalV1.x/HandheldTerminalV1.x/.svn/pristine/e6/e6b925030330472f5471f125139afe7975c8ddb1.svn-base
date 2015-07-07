package com.cattsoft.phone.quality.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.*;

/**
 * Created by Xiaohong on 14-1-16.
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {
    private final String tag = "FragmentAdapter";

    private List<String> titles = new ArrayList<String>();
    private List<Class> clss = new ArrayList<Class>();

    private Map<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>();

    public FragmentAdapter(FragmentManager fm, String[] titles, Class[] clss) {
        super(fm);
        this.titles.addAll(Arrays.asList(titles));
        this.clss.addAll(Arrays.asList(clss));
    }

    public int indexOf(Class cls) {
        return clss.indexOf(cls);
    }

    @Override
    public Fragment getItem(int i) {
        try {
            if (null != fragmentMap.get(i))
                return fragmentMap.get(i);
            return (Fragment) clss.get(i % getCount()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position % getCount());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragmentMap.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup viewPager, int position, Object object) {
        super.destroyItem(viewPager, position, object);
        fragmentMap.remove(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    public Fragment getFragment(int position) {
        return fragmentMap.get(position);
    }

    public void clean() {
        fragmentMap.clear();
    }

    //-----------------------------------------------------------------------------
    // Add "view" at "position" to "views".
    // Returns position of new view.
    // The app should call this to add pages; not used by ViewPager.
    public int addView(String title, Class v) {
        titles.add(title);
        clss.add(v);
        return clss.size() - 1;
    }

    //-----------------------------------------------------------------------------
    // Removes "view" from "views".
    // Retuns position of removed view.
    // The app should call this to remove pages; not used by ViewPager.
    public int removeView(ViewPager pager, Class cls) {
        int position = clss.indexOf(cls);

        pager.setAdapter(null);
        titles.remove(position);
        clss.remove(position);
        pager.setAdapter(this);

        return position;
    }
}
