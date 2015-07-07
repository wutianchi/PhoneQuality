package com.cattsoft.phone.quality.service.handler;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.*;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.BaseCell;
import com.cattsoft.phone.quality.model.GeoLocation;
import com.cattsoft.phone.quality.model.NeighborBaseCell;
import com.cattsoft.phone.quality.service.NetStateService;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.cattsoft.phone.quality.utils.Signal;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import org.joda.time.DateTime;
import roboguice.RoboGuice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Xiaohong on 2014/5/1.
 */
public class PhoneStateHandler extends PhoneStateListener {
    public static final String SIGNAL_ACTION = "com.cattsoft.phone.quality.signal";
    public static final String SERVICE_STATE_ACTION = "com.cattsoft.phone.quality.service";
    public static final String BASE_CELL_ACTION = "com.cattsoft.phone.quality.basecell";
    private static final String TAG = "state_handler";
    @Inject
    TelephonyManager telephonyManager;
    private Context context;
    private Bundle bundle;

    /** */
    private ServiceStateHandler mobileStateHandler;
    private SignalStrengthHandler signalStrengthHandler;
    private PseudoBaseHandler pseudoBaseHandler;

    public PhoneStateHandler(Context context) {
        this.context = context;
        RoboGuice.getBaseApplicationInjector((Application) context.getApplicationContext()).injectMembers(this);
        bundle = new Bundle();
        mobileStateHandler = new ServiceStateHandler(context);
        signalStrengthHandler = new SignalStrengthHandler(context);
        pseudoBaseHandler = new PseudoBaseHandler(context);
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        try {
            mobileStateHandler.handle(serviceState);    // 移动网络时长统计
        } catch (Exception e) {
            Log.w(TAG, "统计移动网络时长异常", e);
        }
        try {
            signalStrengthHandler.handle(serviceState); // 网络信号
        } catch (Exception e) {
            Log.w(TAG, "统计无网络信号指标异常", e);
        }
        bundle.putParcelable("serviceState", serviceState);
        Intent intent = new Intent(SERVICE_STATE_ACTION);
        intent.putExtra("serviceState", serviceState);
        context.sendBroadcast(intent);
        pseudoBaseHandler.handleServiceState(serviceState);
    }

    //   @Override
//    public void onCellInfoChanged(List<CellInfo> cellInfo) {
//        Log.d(TAG, "当前基站信息列表：" + cellInfo.size());

//        CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
//        CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
//
//        CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
//        CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
//        // or
//        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
//        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
//    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        super.onCellLocationChanged(location);
        if (null == location)
            return;
        GeoLocation geoLocation = null;
        try {
            RuntimeExceptionDao<GeoLocation, Long> dao = QualityApplication.getApplication(context).getDatabaseHelper().getGeoLocationDAO();
            geoLocation = dao.queryForFirst(dao.queryBuilder().orderBy("ddate", false).prepare());
        } catch (Exception e) {
        }
        int lac = -1, cid = -1;
        String mcc = null, mnc = null;
        try {
            if (location instanceof GsmCellLocation) {
                // GSM
                lac = ((GsmCellLocation) location).getLac();
                cid = ((GsmCellLocation) location).getCid();
                if (!Strings.isNullOrEmpty(telephonyManager.getNetworkOperator())) {
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
            }
        } catch (Exception e) {
        }
        if (lac == -1 || cid == -1)
            return;
        int rssi = -113;
        try {
            if (null != bundle.get("signalStrength"))
                rssi = ((Signal) bundle.get("signalStrength")).getmGsmDbm();
        } catch (Exception e) {
        }
        BaseCell cell = new BaseCell(mcc, mnc,
                MobileNetType.getPhoneType(context),
                MobileNetType.getMobileTypeValue(context), lac, cid, rssi,
                geoLocation, DateTime.now());
        // 查找基站是否已存在
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("lac", lac);
        params.put("cid", cid);
        try {
            RuntimeExceptionDao<BaseCell, Long> baseCellDAO = QualityApplication.getApplication(context).getDatabaseHelper().getBaseCellDAO();
            List<BaseCell> baseCellList = baseCellDAO.queryForFieldValues(params);
            if (null == baseCellList || baseCellList.size() == 0) {
                // 保存基站信息
                baseCellDAO.create(cell);
            } else {
                // 更新基站信息
                BaseCell _cell = baseCellList.get(0);
                if (null != geoLocation)
                    _cell.setLocation(geoLocation);
                _cell.setRssi(cell.getRssi());
                baseCellDAO.update(_cell);
                cell = _cell;
            }
        } catch (Exception e) {
            Log.w(TAG, "基站信息获取失败", e);
        }
        try {
            RuntimeExceptionDao<NeighborBaseCell, Long> neighborBaseCellDAO = QualityApplication.getApplication(context).getDatabaseHelper().getNeighborBaseCellDAO();
            List<NeighboringCellInfo> neighboringCellInfos = telephonyManager.getNeighboringCellInfo();
            if (null != neighboringCellInfos) {
                for (NeighboringCellInfo neighbor : neighboringCellInfos) {
                    NeighborBaseCell neighborBaseCell = new NeighborBaseCell(MobileNetType.getPhoneType(context),
                            MobileNetType.getMobileTypeValue(context),
                            neighbor.getLac(), neighbor.getCid(), neighbor.getRssi(), neighbor.getPsc(), neighbor.getNetworkType());
                    if (neighborBaseCell.getLac() == -1 || neighborBaseCell.getCid() == -1)
                        continue;
                    params = new HashMap<String, Object>();
                    params.put("lac", neighborBaseCell.getLac());
                    params.put("cid", neighborBaseCell.getCid());
                    params.put("baseCell_id", cell.getId());
                    List<NeighborBaseCell> neighborBaseCellList = neighborBaseCellDAO.queryForFieldValues(params);
                    if (null == neighborBaseCellList || neighborBaseCellList.size() == 0) {
                        // 保存基站信息
                        neighborBaseCell.setBaseCell(cell);
                        neighborBaseCellDAO.create(neighborBaseCell);
                    } else {
                        // 更新基站信息
                        NeighborBaseCell _cell = neighborBaseCellList.get(0);
                        _cell.setRssi(neighborBaseCell.getRssi());
                        neighborBaseCellDAO.update(_cell);
                    }
                }
            }
        } catch (Exception e) {
        }
        // 发送基站信息广播
        Intent intent = new Intent(BASE_CELL_ACTION);
        context.sendBroadcast(intent);
        // 监测基站位置变更频率，如果高频率变动，可能与伪基站有关
        pseudoBaseHandler.handleCellLocationChanged();
        pseudoBaseHandler.handleBaseCellChanged(mnc, mnc, lac & 0xffff, cid & 0xffff);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        Intent intent = new Intent(SIGNAL_ACTION);
        try {
            Signal signal = new Signal(signalStrength);
            bundle.putParcelable("signalStrength", signal);
            intent.putExtra("signalStrength", signal);

            signalStrengthHandler.handle(signalStrength);

            pseudoBaseHandler.handleSignalStrengths(signal.getmGsmSignalStrength());
        } catch (Exception e) {
            Log.w(TAG, "统计网络信号指标异常", e);
        }
        context.sendBroadcast(intent);
    }

    @Override
    public void onDataConnectionStateChanged(int state, int networkType) {
        super.onDataConnectionStateChanged(state, networkType);
        if (MobileNetType.NetWorkType.TYPE_INVALID != MobileNetType.getNetWorkType(context))
            context.startService(new Intent(context.getApplicationContext(), NetStateService.class));
        pseudoBaseHandler.handleDataConnectionState(state, networkType);
    }

    public Bundle getBundle() {
        return bundle;
    }
}
