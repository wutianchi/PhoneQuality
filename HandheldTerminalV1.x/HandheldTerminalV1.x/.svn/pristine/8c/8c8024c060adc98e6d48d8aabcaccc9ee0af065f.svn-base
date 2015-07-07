package com.cattsoft.phone.quality.ui.fragments.stats;

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
 * Created by Xiaohong on 2014/5/15.
 */
public class StatisticsFragment extends RoboFragment {
    @InjectView(R.id.tabs)
    private PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    private ViewPager pager;
    private FragmentAdapter adapter;

    private int position = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        adapter = new FragmentAdapter(getFragmentManager(),
                new String[]{"语音通话", "短信", "流量统计"},
                new Class[]{VoiceFragment.class,
                        SmsFragment.class, TrafficFragment.class});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_viewpager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
    }
}
