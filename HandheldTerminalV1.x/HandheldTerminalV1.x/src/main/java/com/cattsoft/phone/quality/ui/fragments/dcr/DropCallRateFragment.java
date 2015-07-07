package com.cattsoft.phone.quality.ui.fragments.dcr;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.ui.adapter.FragmentAdapter;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import roboguice.inject.InjectView;

/**
 * Created by Xiaohong on 14-3-18.
 */
public class DropCallRateFragment extends RoboLazyFragment {
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager pager;
    private FragmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_viewpager, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FragmentAdapter(getFragmentManager(),
                new String[]{
                        "通话", "短信"
                },
                new Class[]{
                        DropCallRateStatsFragment.class, SmsFailureRateFragment.class
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
