package com.cattsoft.phone.quality.ui.fragments.pseudo;

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
 * Created by Xiaohong on 2014/4/10.
 */
public class PseudoBaseFragment extends RoboFragment {
    @InjectView(R.id.tabs)
    private PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    private ViewPager pager;
    private FragmentAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        adapter = new FragmentAdapter(getFragmentManager(),
                new String[]{"基站信息", "伪基站短信"},
                new Class[]{BaseInfoFragment.class, PseudoSmsFragment.class});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_viewpager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
    }
}