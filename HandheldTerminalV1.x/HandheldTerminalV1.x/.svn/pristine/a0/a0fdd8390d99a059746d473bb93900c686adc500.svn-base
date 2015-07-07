package com.cattsoft.phone.quality.ui.fragments.filedownload;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.cattsoft.phone.quality.ui.fragments.menu.SpeedServerChoiceFragment;
import com.google.common.collect.Maps;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import java.util.Map;

/**
 * Created by yushiwei on 15-5-24.
 */
public class FileChoiceFragment extends RoboLazyFragment {

    private static final String TAG = FileChoiceFragment.class.getSimpleName();

    @InjectView(R.id.file_choice_list_view)
    ListView file_choice_list_view;

    @InjectResource(R.array.file_choice_urls)
    String[] fileChoiceUrls;

    Map<String, String> data;

    private CallBack callBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_file_choice, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof CallBack) {
            setCallBack((CallBack) getActivity());
        }
    }

    @Override
    protected void doStuffInBackground(Application app) {
        super.doStuffInBackground(app);
        try {
            init();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    private void init() throws Exception {
        initData();

        initAdapter();

        initOnClick();
    }

    private void initOnClick() {
        file_choice_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (callBack != null) {
                    CheckedTextView ctv = (CheckedTextView) view.findViewById(R.id.speed_choice_group_checked_text_view);
                    String clickText = String.valueOf(ctv.getText());
                    callBack.onClick(clickText, data.get(clickText));
                }
            }
        });
    }

    private void initAdapter() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity().getBaseContext(),
                R.layout.layout_expandable_list_group,
                R.id.speed_choice_group_checked_text_view,
                data.keySet().toArray(new String[0])
        );

        file_choice_list_view.setAdapter(arrayAdapter);
    }

    private void initData() {
        data = Maps.newLinkedHashMap();
        for (String url : fileChoiceUrls) {
            String[] split = url.split("@@@");
            data.put(split[0], split[1]);
        }
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        public void onClick(String name, String url);
    }
}