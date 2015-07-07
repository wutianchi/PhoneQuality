package com.cattsoft.phone.quality.ui.fragments.pseudo;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.PseudoSms;
import com.cattsoft.phone.quality.ui.adapter.PseudoSmsAdapter;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.cattsoft.phone.quality.ui.widget.OverScrollView;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaohong on 2014/4/10.
 */
public class PseudoSmsFragment extends RoboLazyFragment {
    @InjectView(R.id.scrollView)
    OverScrollView scrollView;
    @InjectView(R.id.listview)
    ListView listView;
    @InjectView(R.id.progress_tip)
    View progress_tip;
    @InjectView(R.id.rootView)
    View rootView;
    @InjectView(R.id.progressTip)
    TextView progressTip;
    List<PseudoSms> results = new ArrayList<PseudoSms>();
    private PseudoSmsAdapter adapter;
    private boolean loaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_pseudo_sms, container, false);
    }

    @Override
    protected void doStuffInBackground(Application app) {
        super.doStuffInBackground(app);
        adapter = new PseudoSmsAdapter(getActivity().getApplicationContext(), results);
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        progressTip.setText("正在加载短信记录..");
        progress_tip.setVisibility(View.VISIBLE);
        try {
            results = getDatabaseHelper().getPseudoSmsDAO().queryBuilder().orderBy("ddate", false).query();
        } catch (Exception e) {
            Log.e("speed", "查询伪基站短信记录失败", e);
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

        if (results.size() == 0) {
            // 提示没有记录
            progressTip.setText("当前没有检测到伪基站短信");
        } else if (results.size() > 0) {
            progress_tip.setVisibility(View.GONE);
        }
    }
}