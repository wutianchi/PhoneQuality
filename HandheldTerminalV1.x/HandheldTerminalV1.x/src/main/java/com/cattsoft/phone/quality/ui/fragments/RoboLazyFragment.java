package com.cattsoft.phone.quality.ui.fragments;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.cattsoft.phone.quality.BuildConfig;
import com.cattsoft.phone.quality.PhoneQualityActivity;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.service.aidl.IPhoneStateService;
import com.cattsoft.phone.quality.utils.DatabaseHelper;
import com.j256.ormlite.dao.GenericRawResults;
import roboguice.fragment.RoboFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaohong on 2014/5/12.
 */
public class RoboLazyFragment extends RoboFragment {
    protected String TAG = RoboLazyFragment.class.getSimpleName();

    private boolean viewCreated = false;
    private boolean visibleToUser = false;

    private List<BroadcastReceiver> receivers;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                final Application app = getActivity().getApplication();
                switch (msg.what) {
                    case 0:
                        try {
                            doStuffInBackground(app);
                        } catch (Throwable t) {
                            Log.e("fragment", "界面设置出现异常", t);
                        }
                        break;
                    case 1:
                        try {
                            doDataInBackground(app);
                        } catch (Throwable t) {
                            Log.e("fragment", "数据加载失败", t);
                        }
                        break;
                    case 2:
                        try {
                            doWhenEverVisable(app);
                        } catch (Throwable t) {
                            Log.e("fragment", "界面刷新异常", t);
                        }
                        break;
                    case 11: {
                        try {
                            doWhenEverInVisable(app);
                        } catch (Throwable t) {
                            Log.e("fragment", "界面切换隐藏，操作异常", t);
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("fragment", "界面加载出现异常", e);
            }
            return true;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewCreated = false;
        visibleToUser = false;
        getApplication().runInApplication(new Runnable() {
            @Override
            public void run() {
                try {
                    final Application app = getActivity().getApplication();
                    doResourceInBackground(app);
                } catch (Throwable t) {
                    Log.d("fragment", "资源加载出现异常，不允许UI操作", t);
                }
            }
        }, getActivity());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && visibleToUser) {
            // 展示完成后的再次切换显示
            getApplication().runInApplication(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(2);
                }
            }, getActivity());
        }
        if (!visibleToUser && isVisibleToUser) {
            // 片段创建后首次界面显示
            getApplication().runInApplication(new Runnable() {
                @Override
                public void run() {
                    visibleToUser = true;
                    handler.sendEmptyMessage(1);
                }
            }, getActivity());
        }
        if (!isVisibleToUser && visibleToUser) {
            // 片段显示完成后的切换隐藏操作
            getApplication().runInApplication(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(11);
                }
            }, getActivity());
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!viewCreated) {
            getApplication().runInApplication(new Runnable() {
                @Override
                public void run() {
                    viewCreated = true;
                    handler.sendEmptyMessage(0);
                }
            }, getActivity());
        }
    }

    public QualityApplication getApplication() {
        return (QualityApplication) getActivity().getApplication();
    }

    public IPhoneStateService getBinder() {
        return ((PhoneQualityActivity) getActivity()).getBinder();
    }

    /**
     * 片段创建时资源加载，不允许更新UI
     *
     * @param app
     */
    protected void doResourceInBackground(Application app) {
//        Log.d("fragment", "doResourceInBackground：" + toString());
    }

    /**
     * 片段界面创建完成后更新界面.
     * 此时片段尚未显示.
     * Is there anything you want to do in the background? Add it here.
     *
     * @param app
     */
    @SuppressWarnings({"UnusedParameters"})
    protected void doStuffInBackground(Application app) {
//        Log.d("fragment", "doStuffInBackground：" + toString());
    }

    /**
     * 界面显示后的数据加载.
     *
     * @param app
     */
    @SuppressWarnings({"UnusedParameters"})
    protected void doDataInBackground(Application app) {
//        Log.d("fragment", "doDataInBackground：" + toString());
    }

    protected void doWhenEverVisable(Application app) {
        //
    }

    /**
     * 每次界面切换隐藏时执行
     *
     * @param app
     */
    protected void doWhenEverInVisable(Application app) {
        //
    }

    /**
     * 界面销毁
     *
     * @param app
     */
    protected void doDestroyView(Application app) {
        //
    }

    public Intent registerReceiver(BroadcastReceiver receiver,
                                   IntentFilter filter) {
        if (null == receivers)
            receivers = new ArrayList<BroadcastReceiver>();
        receivers.add(receiver);
        return getActivity().registerReceiver(receiver, filter);
    }

    protected void unregisterReceiver(BroadcastReceiver receiver) {
        try {
            if (null != receiver)
                receivers.remove(receiver);
            getActivity().unregisterReceiver(receiver);
        } catch (Exception e) {
        }
    }

    protected void closeQuietly(GenericRawResults<?> genericRawResults) {
        try {
            if (null != genericRawResults)
                genericRawResults.close();
        } catch (Exception e) {
        }
    }

    protected void showToast(Toast toast) {
        try {
            toast.show();
        } catch (Throwable t) {
        }
    }

    protected DatabaseHelper getDatabaseHelper() {
        return getApplication().getDatabaseHelper();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != receivers) {
            if (BuildConfig.DEBUG)
                Log.d("fragment", "clean registered receiver");
            for (BroadcastReceiver receiver : receivers) {
                try {
                    getActivity().unregisterReceiver(receiver);
                } catch (Exception e) {
                }
            }
            receivers = null;
        }
        getApplication().runInApplication(new Runnable() {
            @Override
            public void run() {
                try {
                    doDestroyView(getApplication());
                } catch (Throwable t) {
                }
            }
        }, getActivity());
    }
}
