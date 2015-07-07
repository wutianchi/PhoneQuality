package com.cattsoft.phone.quality.ui.fragments.signal;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.ui.adapter.FragmentAdapter;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by Xiaohong on 2014/5/12.
 */
public class SignalFragment extends RoboFragment {
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager pager;
    private FragmentAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_viewpager, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FragmentAdapter(getFragmentManager(),
                new String[]{
                        "实时",
                        "小时",
                        "今天",
                        "统计"
                },
                new Class[]{
                        RealtimeFragment.class,
                        HourFragment.class,
                        DayFragment.class,
                        StatsFragment.class
                });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
    }

    @Override
    public void onDestroy() {
        adapter = null;

        super.onDestroy();
    }
}
