package com.cattsoft.phone.quality.ui.fragments.pseudo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cattsoft.phone.quality.BuildConfig;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.service.handler.PhoneStateHandler;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.google.inject.Inject;
import roboguice.inject.InjectView;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * 基站信息界面.
 * Created by Xiaohong on 2014/4/10.
 */
public class BaseInfoFragment extends RoboLazyFragment {
    @InjectView(R.id.pb_mcc)
    TextView tv_mcc;
    @InjectView(R.id.pb_mnc)
    TextView tv_mnc;
    @InjectView(R.id.pb_lac)
    TextView tv_lac;
    @InjectView(R.id.pb_cid)
    TextView tv_cid;
    @Inject
    TelephonyManager telephonyManager;

    private InfoReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_pseudo_base, container, false);
    }

    @Override
    protected void doStuffInBackground(Application app) {
        super.doStuffInBackground(app);
        receiver = new InfoReceiver();
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        setInfo();
        registerReceiver(receiver, new IntentFilter(PhoneStateHandler.BASE_CELL_ACTION));
    }

    private void setInfo() {
        int lac = -1, cid = -1;
        String mcc = null, mnc = null;
        try {
            CellLocation location = telephonyManager.getCellLocation();
            if (location instanceof GsmCellLocation) {
                // GSM
                lac = ((GsmCellLocation) location).getLac();
                cid = ((GsmCellLocation) location).getCid();
                if (!TextUtils.isEmpty(telephonyManager.getNetworkOperator())) {
                    mcc = telephonyManager.getNetworkOperator().substring(0, 3);
                    mnc = telephonyManager.getNetworkOperator().substring(3, 5);
                }
            } else if (location instanceof CdmaCellLocation) {
                // CDMA
                lac = ((CdmaCellLocation) location).getNetworkId();
                cid = ((CdmaCellLocation) location).getBaseStationId();
                if (!TextUtils.isEmpty(telephonyManager.getNetworkOperator()))
                    mcc = telephonyManager.getNetworkOperator().substring(0, 3);
                int mnc_val = ((CdmaCellLocation) location).getSystemId();
                if (mnc_val == -1)
                    mnc_val = ((CdmaCellLocation) location).getBaseStationId();
                mnc = Integer.toString(mnc_val);
            } else {
                //
                if (BuildConfig.DEBUG)
                    Log.i("cell", "未识别当前基站信息:" + location);
                return;
            }
            tv_mcc.setText(mcc);
            tv_mnc.setText(mnc);
            tv_lac.setText(Integer.toString(lac & 0xffff));
            tv_cid.setText(Integer.toString(cid & 0xffff));
        } catch (Exception e) {

        }
    }

    private class InfoReceiver extends RoboBroadcastReceiver {
        @Override
        protected void handleReceive(Context context, Intent intent) {
            super.handleReceive(context, intent);
            if (!getUserVisibleHint())
                return;
            setInfo();
        }
    }
}
