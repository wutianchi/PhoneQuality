package com.cattsoft.phone.quality.ui.fragments.wifi;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.astuetz.PagerSlidingTabStrip;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.ui.adapter.FragmentAdapter;
import com.cattsoft.phone.quality.ui.adapter.WiFiItemAdapter;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.cattsoft.phone.quality.ui.widget.OverScrollView;
import com.google.inject.Inject;
import roboguice.inject.InjectView;
import roboguice.receiver.RoboBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaohong on 2014/5/15.
 */
public class ListFragment extends RoboLazyFragment {
    @InjectView(R.id.scrollView)
    OverScrollView scrollView;
    @InjectView(R.id.rootView)
    LinearLayout rootView;
    @InjectView(R.id.listview)
    ListView listView;
    @InjectView(R.id.progress_tip)
    RelativeLayout progress_tip;
    @Inject
    WifiManager wifiManager;
    List<ScanResult> results = new ArrayList<ScanResult>();
    private WifiReceiver receiver = null;
    private WiFiItemAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_wifi_list, container, false);
    }

    @Override
    protected void doStuffInBackground(Application app) {
        super.doStuffInBackground(app);
        adapter = new WiFiItemAdapter(getActivity().getApplicationContext(), results);
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ViewPager mPager = (ViewPager) getActivity().findViewById(R.id.pager);
                PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
                FragmentAdapter fadapter = (FragmentAdapter) mPager.getAdapter();
                if (fadapter.indexOf(IndicatorFragment.class) == -1) {
                    fadapter.addView("指示器", IndicatorFragment.class);
                    mPager.getAdapter().notifyDataSetChanged();
                    mPager.invalidate();
                    tabStrip.notifyDataSetChanged();
                }
                mPager.setCurrentItem(2);

                Fragment fragment = ((FragmentAdapter) mPager.getAdapter()).getFragment(2);
                if (fragment instanceof IndicatorFragment) {
                    ((IndicatorFragment) fragment).setBSSID(adapter.getItem(i).SSID, adapter.getItem(i).BSSID);
                }
            }
        });
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        receiver = new WifiReceiver();
        registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        sendIntervalAction(10);
    }

    private void sendIntervalAction(int interval) {
        Intent intent = new Intent(WifiFragment.WIFI_SCAN_INTERVAL_ACTION);
        intent.putExtra("interval", interval);
        getActivity().sendBroadcast(intent);
    }

    class WifiReceiver extends RoboBroadcastReceiver {
        @Override
        protected void handleReceive(Context context, Intent intent) {
            if (!getUserVisibleHint())
                return;
            results = wifiManager.getScanResults();
            adapter.clear();
            adapter.addAll(results);
            int totalHeight = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                View listItem = adapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
            listView.setLayoutParams(params);
            adapter.notifyDataSetChanged();
            progress_tip.setVisibility(View.GONE);
        }
    }
}
