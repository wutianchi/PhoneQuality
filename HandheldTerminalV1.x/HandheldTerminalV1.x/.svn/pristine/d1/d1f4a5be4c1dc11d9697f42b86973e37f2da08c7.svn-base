package com.cattsoft.phone.quality.ui.fragments.speed;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.SpeedTimeLine;
import com.cattsoft.phone.quality.task.SpeedTimeLineTask;
import com.cattsoft.phone.quality.ui.adapter.SpeedShareAdapter;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaohong on 2014/5/14.
 */
public class StreamFragment extends RoboLazyFragment {
    @InjectView(R.id.list)
    PullToRefreshListView mPullRefreshListView;
    SpeedShareAdapter adapter;
    ArrayList<SpeedTimeLine> records;

    private Handler handler = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_speed_stream, container, false);
    }

    @Override
    protected void doStuffInBackground(Application app) {
        super.doStuffInBackground(app);
        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.
                try {
                    requestUpdate();
                } catch (Exception e) {
                    Log.e("stream", "无法发起数据请求", e);
                }
            }
        });
        /**
         * Add Event Listener
         */
        mPullRefreshListView.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ListView>() {
            @Override
            public void onPullEvent(PullToRefreshBase<ListView> refreshView, PullToRefreshBase.State state,
                                    PullToRefreshBase.Mode direction) {
                if (state.equals(PullToRefreshBase.State.PULL_TO_REFRESH)) {
                    String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                    // Update the LastUpdatedLabel
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                }
            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (null != msg.obj) {
                    SpeedTimeLine[] lines = (SpeedTimeLine[]) msg.obj;
                    if (lines.length > 0) {
                        for (SpeedTimeLine line : lines) {
                            try {
                                int scrolledToIndex = mPullRefreshListView.getRefreshableView().getFirstVisiblePosition();
                                View v = mPullRefreshListView.getRefreshableView().getChildAt(0);
                                int topOffset = (v == null) ? 0 : v.getTop();
                                adapter.addTop(line, topOffset);
                            } catch (Exception e) {
                            }
                        }
                    } else {
                        // 当前没有数据
                        showToast(Toast.makeText(getActivity(), "当前没有数据更新", Toast.LENGTH_SHORT));
                    }
                } else {
                    // 数据请求失败
                    showToast(Toast.makeText(getActivity(), "目前无法获取数据", Toast.LENGTH_SHORT));
                }
                mPullRefreshListView.onRefreshComplete();
                return true;
            }
        });
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        records = new ArrayList<SpeedTimeLine>();
        try {
            RuntimeExceptionDao<SpeedTimeLine, Long> dao = getApplication().getDatabaseHelper().getSpeedTimeLineDAO();
            List<SpeedTimeLine> data = dao.query(dao.queryBuilder().orderBy("ddate", false).limit(10l).prepare());
            if (null != data)
                records.addAll(data);
        } catch (Exception e) {
        }
        adapter = new SpeedShareAdapter(getActivity(), records);
        mPullRefreshListView.getRefreshableView().setAdapter(adapter);
        mPullRefreshListView.setRefreshing();
//        mPullRefreshListView.onRefreshComplete();
    }

    private void requestUpdate() {
        new SpeedTimeLineTask(getActivity(), handler).execute();
    }
}
