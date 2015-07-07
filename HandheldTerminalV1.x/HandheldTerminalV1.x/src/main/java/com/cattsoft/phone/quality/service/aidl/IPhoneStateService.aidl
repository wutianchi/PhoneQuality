package com.cattsoft.phone.quality.service.aidl;

import android.os.Bundle;
import android.telephony.ServiceState;
import com.cattsoft.phone.quality.utils.Signal;

/**
 * Created by Xiaohong on 2014/5/13.
 */
interface IPhoneStateService {
    String getValue(in String name);

    Bundle getBundle();

    ServiceState getServiceState();

    Signal getSignal();

    void sendEmptyMessage(int what);

    void sendMessage(int what, int arg1);
}
