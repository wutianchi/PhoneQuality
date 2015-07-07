package com.cattsoft.phone.quality.ui.fragments.speed;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.SpeedTestResult;
import com.cattsoft.phone.quality.service.receiver.NetActivityReceiver;
import com.cattsoft.phone.quality.task.BandWidthAsyncTask;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.cattsoft.phone.quality.ui.widget.DismissablePopupWindow;
import com.cattsoft.phone.quality.utils.*;
import com.cattsoft.phone.quality.utils.speed.NetSpeedTest;
import com.google.inject.Inject;

import org.joda.time.DateTime;

import java.util.Random;

import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * Created by Xiaohong on 2014/5/14.
 */
public class TesterFragment extends RoboLazyFragment {

    public static final String CURR_SERVER = "bandwidth_curr_server";

    public static final String CURR_IP = "bandwidth_curr_ip";
    public static final String FORMAT = "%.1f";

    @Inject
    private WifiManager wifiManager;
    @Inject
    private SharedPreferences sharedPreferences;
    @InjectView(R.id.rootView)
    private View rootView;
    @InjectView(R.id.speed_action)
    private BootstrapButton action;
    @InjectView(R.id.dial_pointer)
    private ImageView dial_pointer;
    @InjectView(R.id.speed_ping)
    private TextView speed_ping;
    @InjectView(R.id.speed_ping_unit)
    private TextView speed_ping_unit;
    @InjectView(R.id.speed_ping_icon)
    private ImageView speed_ping_icon;
    @InjectView(R.id.speed_upload)
    private TextView speed_upload;
    @InjectView(R.id.speed_upload_unit)
    private TextView speed_upload_unit;
    @InjectView(R.id.speed_upload_icon)
    private ImageView speed_upload_icon;
    @InjectView(R.id.speed_download)
    private TextView speed_download;
    @InjectView(R.id.speed_download_unit)
    private TextView speed_download_unit;
    @InjectView(R.id.speed_download_icon)
    private ImageView speed_download_icon;
    @InjectView(R.id.net_speed_nettype)
    private TextView netType;
//    @InjectView(R.id.net_ssid_nettype)
//    private TextView netSsid;
    @InjectView(R.id.net_speed_avg)
    private TextView net_speed_avg;
    @InjectView(R.id.net_speed_avg_unit)
    private TextView net_speed_avg_unit;

    private RotateAnimation rotateAnimation;

    private AlphaAnimation flashAnimation;

    private NetSpeedTest.Type type;

    private BandWidthAsyncTask task;

    private boolean isStart;

    private BandWidthAsyncTask.ShowType state;

    private float degrees = 0;

    private Toast toast;

    private SpeedTestResult result;

    private String server;

    private String serverName;

    @InjectResource(R.integer.bandwidth_show_cycle)
    private int animationTime;

    @InjectResource(R.array.tester_degree)
    private int[] testerDegree;

    @InjectResource(R.array.tester_range)
    private int[] testerRange;

    private long band;
    private SpeedServerMatch speedServerMatch = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_speed_tester, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Log.d(TAG, "----4");
//        SpeedServer server = PrefUtils.getSpeedServer(getActivity());
//        Log.d(TAG, "----4---"+PrefUtils.isSpeedServerAutoSelect(getActivity()));
//        if(PrefUtils.isSpeedServerAutoSelect(getActivity())) {
            // 自动匹配服务器
//            Log.d(TAG, "----4---"+(null == speedServerMatch));
            if(null == speedServerMatch) {
                speedServerMatch = new SpeedServerMatch(getActivity(), new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if(null != getActivity() && msg.what == 1){
                            Toast.makeText(getActivity(), "自动匹配测速服务器失败", Toast.LENGTH_SHORT).show();}
                        else{
                            SpeedServer speedServer = (SpeedServer) msg.obj;
                            netType.setText(speedServer.getName());
//                            Log.d(TAG, "----4---"+speedServer.getName()+speedServer.getServer()+":"+speedServer.getPort());
                            SharedPreferences preferences = getActivity().getSharedPreferences("config", 0x8000);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(TesterFragment.CURR_IP, speedServer.getServer()+":"+speedServer.getPort());
                            editor.putString(TesterFragment.CURR_SERVER, speedServer.getName());
                            editor.apply();
                        }
                        return false;
                    }
                }));
                speedServerMatch.execute();
            }
//        }
    }

    @Override
    protected void doResourceInBackground(Application app) {
        Log.d(TAG, "----3");
        super.doResourceInBackground(app);
        //闪烁
        flashAnimation = new AlphaAnimation(0.1f, 1.0f);
        flashAnimation.setDuration(200);
        flashAnimation.setFillBefore(true);
        flashAnimation.setRepeatCount(3);
        flashAnimation.setRepeatMode(Animation.REVERSE);
    }

    @Override
    protected void doStuffInBackground(Application app) {
        Log.d(TAG, "----1");
        action.setOnClickListener(new BottomClick());
        initConfig();
        Context context = getActivity().getBaseContext();
        result = new SpeedTestResult(MobileNetType.getPhoneType(context),
                MobileNetType.getMobileTypeValue(context),
                MobileNetType.getNetWorkType(context).type, server, serverName, DateTime.now());
        result.setLocation(getApplication().getDatabaseHelper().getLastLocation());
        try {
            result.setOperator(Integer.parseInt(Devices.getOperatorCode(context)));
        } catch (Exception e) {
            Log.e(TAG, "设置运营商信息失败", e);
        }
    }

    private void initConfig() {
        SharedPreferences preferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        this.serverName = preferences.getString(CURR_SERVER, getString(R.string.default_server));
        this.server = preferences.getString(CURR_IP, getString(R.string.default_ip));
        Log.d("ceshi","---"+serverName+server);
        this.netType.setText(serverName);
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        Log.d(TAG, "----2");
        MobileNetType.NetWorkType netWorkType = MobileNetType.getNetWorkType(app);
        netType.setText(netWorkType.nickname);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MobileNetType.NetWorkType netWorkType = MobileNetType.getNetWorkType(context);
//                netType.setText(netWorkType.nickname);
                if (netWorkType == MobileNetType.NetWorkType.TYPE_WIFI)
                    netType.setText(wifiManager.getConnectionInfo().getSSID().replaceAll("^\"|\"$", ""));
                else
                    netType.setText("");
            }
        }, new IntentFilter(NetActivityReceiver.NET_CHANGED_ACTION));
    }

    private BandWidthAsyncTask newBandWidth() {
        Log.d(TAG, "----5");
        initConfig();
        result.setServerName(serverName);
        result.setServer(server);
        result.setDdate(DateTime.now());
        result.setFailure(0);
        return new BandWidthAsyncTask(getActivity().getBaseContext(), server) {
            @Override
            public void onProgressUpdate(Info... values) {
                if (values != null && values.length > 0) {
                    Info info = values[0];
                    Log.d(TAG, info.oper + ": " + info.value);
                    try {
                        refreshUI(info);
                    } catch (Exception e) {
                        Log.e(TAG, "", e);
                    }
                }
            }
        };
    }

    private void onRangeDownloadAvgSpeed(float totalAvgSpeed) {
        this.onDownload(totalAvgSpeed);
        result.setDownload(totalAvgSpeed);
    }

    private void onRangeUploadAvgSpeed(float totalAvgSpeed) {
        this.onUpload(totalAvgSpeed);
        result.setUpload(totalAvgSpeed);
    }

    private void onBand(float bandSpeed) {
        band = Math.round(bandSpeed * 8D / 1024);
    }

    private void refreshUI(BandWidthAsyncTask.Info info) throws Exception {
//        Log.d(TAG, "----6");
        TesterFragment.this.setState(info.oper);
        switch (info.oper) {
            case SHOW_BAND:
                this.onBand((Float) info.value);
                break;
            case SHOW_RANGE_AVG_BAND:
                break;
            case SHOW_RANGE_AVG_DOWNLOAD:
                this.onRangeDownloadAvgSpeed((Float) info.value);
                break;
            case SHOW_RANGE_AVG_UPLOAD:
                this.onRangeUploadAvgSpeed((Float) info.value);
                break;
            case SHOW_DELAY:
                this.onDelay((Integer) info.value);
                break;
            case SHOW_CURR_DOWNLOAD:
                this.onDownload((Float) info.value);
                break;
            case SHOW_CURR_UPLOAD:
                this.onUpload((Float) info.value);
                break;
            case SWITCH:
                this.onSwitch((NetSpeedTest.Type) info.value);
                break;
            case END:
                this.onTestComplete();
                break;
            case RESET:
                this.onStartTest();
                break;
            case START:
                this.onReset();
                break;
            case ERROR:
                this.onError();
                break;
            case FATAL:
                this.onFatal();
                break;
        }
    }

    private void onSuccess() {
        // 测速成功
        if (0 != result.getFailure())
            return;
        try {
            final DismissablePopupWindow popupWindow = new DismissablePopupWindow(
                    getActivity().getApplicationContext(),
                    R.layout.layout_speed_result_popup
            );
            View contentView = popupWindow.getContentView();
            getTextViewById(contentView, R.id.speed_download).setText(String.format("%.1f %s", result.getDownload(), speed_download_unit.getText()));
            getTextViewById(contentView, R.id.speed_upload).setText(String.format("%.1f %s", result.getUpload(), speed_upload_unit.getText()));
            ((TextView) popupWindow.getContentView().findViewById(R.id.speed_server)).setText(serverName);
            ((TextView) popupWindow.getContentView().findViewById(R.id.speed_nettype)).setText("");
            ((TextView) popupWindow.getContentView().findViewById(R.id.speed_bandwidth)).setText(Math.round(result.getDownload() * 8D / 1024)+"");
            popupWindow.update();
            popupWindow.show(rootView, 0, 0);

            Button btn = (Button) popupWindow.getContentView().findViewById(R.id.dismiss);
            btn.requestFocus();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    private TextView getTextViewById(View view, int id) {
        View v = view.findViewById(id);
        if (v instanceof TextView) {
            return (TextView) v;
        } else {
            throw new IllegalArgumentException("错误的ID：" + id + ", 不是TextView的ID");
        }
    }

    private void onFatal() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getApplication().getApplicationContext(), "服务器连接质量过差，当前测试任务失败！请重新开始或更换服务器", Toast.LENGTH_LONG);
        toast.show();
        result.setDownload(0);
        result.setUpload(0);
        result.setFailure(1);
        onTestComplete();
        if (task != null) {
            task.cancel(true);
        }
    }

    private void onError() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getApplication().getApplicationContext(), "测试出错!\n测试可以继续,但结果可能不准.", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected void doWhenEverInVisable(Application app) {
        super.doWhenEverInVisable(app);
        if (null != task) {
            task.cancel(true);
            resetLabel();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        doDestroyView(getApplication());
    }

    @Override
    protected void doDestroyView(Application app) {
        super.doDestroyView(app);
        if (null != task) {
            task.cancel(true);
            isStart = false;
        }
    }

    private void onTestComplete() {
        Log.d(TAG, "----7");
        task = null;
        type = null;
        isStart = false;
        action.setBootstrapType("primary");
        action.setText(getString(R.string.netspeed_start_test));
        doDial(0);
        net_speed_avg.setText("0");
        action.setBootstrapButtonEnabled(true);
        resetIcon();

        onSuccess();

        // 保存到数据库
        try {
            getApplication().getDatabaseHelper().getSpeedTestResultDAO().create(result);
        } catch (Exception e) {
            Log.e(TAG, "保存测速数据时出现异常", e);
        }
    }

    private void resetIcon() {
        try {
            speed_ping_icon.setImageDrawable(getResources().getDrawable(R.drawable.net_speed_icon_ping_normal));
            speed_download_icon.setImageDrawable(getResources().getDrawable(R.drawable.net_speed_icon_download_normal));
            speed_upload_icon.setImageDrawable(getResources().getDrawable(R.drawable.net_speed_icon_upload_normal));
        } catch (Exception e) {
            // Activity 已关闭时会导致异常
        }
        try {
            flashAnimation.cancel();
        } catch (Exception e) {
        }
    }

    private void resetLabel() {
        speed_ping.setText("0");
        speed_download.setText("0.0");
        speed_upload.setText("0.0");
        net_speed_avg.setText("0");
    }

    private void onSwitch(NetSpeedTest.Type type) {
        try {
            flashAnimation.cancel();
        } catch (Exception e) {
        }
        this.type = type;
        resetIcon();
        net_speed_avg.setText("0");
        ImageView v = null;
        if (type == NetSpeedTest.Type.PING) {
            speed_ping_icon.setImageResource(R.drawable.net_speed_icon_ping_highlighted);
            v = speed_ping_icon;
        } else if (type == NetSpeedTest.Type.DOWNLOAD) {
            speed_download_icon.setImageResource(R.drawable.net_speed_icon_download_highlighted);
            v = speed_download_icon;
        } else if (type == NetSpeedTest.Type.UPLOAD) {
            speed_upload_icon.setImageResource(R.drawable.net_speed_icon_upload_highlighted);
            v = speed_upload_icon;
        }
        if (null != v) {
            v.setAnimation(flashAnimation);
            flashAnimation.start();
        }
        doDial(0);
    }

    private float countDial(float kbps, int[] dial, int[] range) {
        if (kbps == 0) {
            return 0F;
        }

        float d = 0;
        if (kbps > range[range.length - 1]) {
            d = new Random().nextInt(10) + range[range.length - 1];
        } else {
            for (int i = 1; i < range.length; i++) {
                if (i <= range.length - 1) {
                    if (kbps >= range[i - 1] && kbps < range[i]) {
                        d = (dial[i] - dial[i - 1] + 0.0F) / (range[i] - range[i - 1]) * (kbps - range[i - 1]) + dial[i - 1];
                        break;
                    }
                }
            }
        }
        return d;
    }

    private void doDial(float kbps) {
        float d = countDial(kbps, testerDegree, testerRange);
        float temp = d - degrees;
        try {
            // 指针旋转
            RotateAnimation rotateAnimation = new RotateAnimation(
                    degrees,
                    d,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
            );
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setDuration(getAnimationDuration(temp)); // 持续时间
            dial_pointer.startAnimation(rotateAnimation);
//            Log.d(TAG, "from" + degrees + "to" + d);
        } catch (Exception e) {
            Log.e(TAG, "", e);
            Log.d(TAG, "指针动画播放失败");
        }
        degrees = d;
    }

    private long getAnimationDuration(float temp) {
        return Double.valueOf(Math.abs(temp) / 360D * animationTime).longValue();
    }

    private void onDelay(int delay) {
        speed_ping.setText(delay + "");
        result.setPing(delay);
    }

    private void onRangeAvgBandWitdh(float avgSpeed) {
        net_speed_avg.setText(String.format(FORMAT, avgSpeed * 8 / 1024));
    }

    private void onUpload(float speed) {
        speed_upload.setText(String.format(FORMAT, speed));
        net_speed_avg.setText(String.format(FORMAT, speed * 8 / 1024));
        doDial(Double.valueOf(speed).floatValue() * 8);
    }

    private void onDownload(float speed) {
        speed_download.setText(String.format(FORMAT, speed));
        net_speed_avg.setText(String.format(FORMAT, speed * 8 / 1024));
        doDial(Double.valueOf(speed).floatValue() * 8);
    }

    private void onStartTest() {
        try {
            flashAnimation.cancel();
        } catch (Exception e) {
        }
        resetLabel();
        resetIcon();
        doDial(0.0F);
    }

    private void onReset() {
        try {
            flashAnimation.cancel();
        } catch (Exception e) {
        }
        resetLabel();
        resetIcon();
        action.setText(getString(R.string.netspeed_start_test));
        doDial(0.0F);

    }

    private void initResult() {
        result.setServer(server);
        result.setServerName(serverName);
    }

    public void setServer(String serverName, String serverIp) {
        this.server = serverIp;
        this.serverName = serverName;
        this.netType.setText(serverName);
    }

    public BandWidthAsyncTask.ShowType getState() {
        return this.state;

    }

    public void setState(BandWidthAsyncTask.ShowType state) {
        this.state = state;
    }

    private void showToast() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getActivity().getBaseContext(), "请等待上次任务完毕...", Toast.LENGTH_SHORT);
        toast.show();
    }

    class BottomClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if ((task != null && task.getState() == BandWidthAsyncTask.State.ERROR) || !isStart) {
                task = newBandWidth();
                task.execute();
                action.setText("请等待测试完毕..");
                isStart = true;
            } else {
                showToast();
            }
        }
    }
}