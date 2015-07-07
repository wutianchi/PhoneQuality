package com.cattsoft.phone.quality.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.ui.adapter.FragmentAdapter;
import roboguice.inject.InjectView;

/**
 * Created by yushiwei on 15-5-24.
 */
public class PagedFragment extends RoboLazyFragment implements ViewPager.OnPageChangeListener {
    @InjectView(R.id.tabs)
    protected PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    protected ViewPager pager;
    protected String[] labels;
    protected String[] classNames;
    protected int tabArrayId;
    protected int classArrayId;
    private FragmentAdapter adapter = null;

    public PagedFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_viewpager, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        labels = getActivity().getResources().getStringArray(tabArrayId);
        classNames = getActivity().getResources().getStringArray(classArrayId);

        adapter = new FragmentAdapter(getFragmentManager(), labels, toClasses(classNames));
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(this);
        tabs.setViewPager(pager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private Class[] toClasses(String[] classNames) {
        Class[] returN = new Class[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            try {
                returN[i] = Class.forName(classNames[i]);
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "", e);
            }
        }
        return returN;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        innerFragmentDestoryView();
    }

    @Override
    public void onPause() {
        super.onPause();
        innerFragmentPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        innerFragmentStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        innerFragmentDestory();
        adapter = null;
    }

    private void innerFragmentPause() {
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                Fragment fragment = adapter.getFragment(i);
                fragment.onPause();
            }
        }
    }

    private void innerFragmentStop() {
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                Fragment fragment = adapter.getFragment(i);
                fragment.onStop();
            }
        }
    }

    private void innerFragmentDestoryView() {
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                Fragment fragment = adapter.getFragment(i);
                fragment.onDestroyView();
            }
        }
    }

    private void innerFragmentDestory() {
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                Fragment fragment = adapter.getFragment(i);
                fragment.onDestroy();
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}