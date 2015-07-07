package com.cattsoft.phone.quality.ui.fragments.speed;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.cattsoft.commons.digest.StringUtils;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.SpeedTestResult;
import com.cattsoft.phone.quality.task.SpeedShareTask;
import com.cattsoft.phone.quality.ui.adapter.SpeedResultAdapter;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.cattsoft.phone.quality.ui.widget.DismissablePopupWindow;
import roboguice.inject.InjectView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaohong on 2014/5/14.
 */
public class HistoryFragment extends RoboLazyFragment {
    @InjectView(R.id.listview)
    ListView listView;
    @InjectView(R.id.progress_tip)
    View progress_tip;
    @InjectView(R.id.rootView)
    View rootView;
    List<SpeedTestResult> results = new ArrayList<SpeedTestResult>();
    private SpeedResultAdapter adapter;
    private DecimalFormat format = new DecimalFormat("#.#");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_speed_history, container, false);
    }

    @Override
    protected void doStuffInBackground(Application app) {
        super.doStuffInBackground(app);
        adapter = new SpeedResultAdapter(getActivity().getApplicationContext(), results);
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 弹出界面框
                final DismissablePopupWindow popupWindow = new DismissablePopupWindow(getActivity().getApplicationContext(), R.layout.layout_speed_detail_popup);
                popupWindow.show(rootView, 0, 0);

                final SpeedTestResult result = adapter.getItem(i);
                if (null != result) {
                    try {
                        if (null != result.getLocation() && StringUtils.isNotEmpty(result.getLocation().getAddress()))
                            ((TextView) popupWindow.getContentView().findViewById(R.id.clientLocationValueText)).setText(result.getLocation().getAddress());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ((TextView) popupWindow.getContentView().findViewById(R.id.downloadValueText)).setText(format.format(result.getDownload()));
                    ((TextView) popupWindow.getContentView().findViewById(R.id.uploadValueText)).setText(format.format(result.getUpload()));
                    ((TextView) popupWindow.getContentView().findViewById(R.id.pingValueText)).setText(format.format(result.getPing()));
                    ((TextView) popupWindow.getContentView().findViewById(R.id.serverLocationValueText)).setText(result.getServer());
                    Button button = (Button) popupWindow.getContentView().findViewById(R.id.share);
                    if (result.isShared()) {
                        button.setText("已分享");
                        button.setEnabled(false);
                    } else {
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                                share(result);
                            }
                        });
                    }
                }
            }
        });
    }

    private void load() {
        try {
            results = getApplication().getDatabaseHelper().getSpeedTestResultDAO().queryBuilder().orderBy("ddate", false).query();
        } catch (Exception e) {
            showToast(Toast.makeText(getActivity(), "查询测速历史记录失败", Toast.LENGTH_SHORT));
            Log.e("speed", "查询测速历史记录失败", e);
        }
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

    @Override
    protected void doWhenEverVisable(Application app) {
        super.doWhenEverVisable(app);
        load();
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        load();
        if (results.size() == 0) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("测速记录")
                    .setMessage("当前好像还没有网络速率测试记录，要现在测试一下吗？")
                    .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    })
                    .setPositiveButton("在线测速", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                ViewPager mPager = (ViewPager) getActivity().findViewById(R.id.pager);
                                mPager.setCurrentItem(0);
                            } catch (Exception e) {
                            }
                        }
                    }).show();
        }
    }

    /**
     * 分享到网络
     */
    private void share(final SpeedTestResult result) {
        try {
            new SpeedShareTask(getActivity(), null, result).execute();
        } catch (Exception e) {
            Log.e(TAG, "数据分享失败：" + e);
        }
    }
}
