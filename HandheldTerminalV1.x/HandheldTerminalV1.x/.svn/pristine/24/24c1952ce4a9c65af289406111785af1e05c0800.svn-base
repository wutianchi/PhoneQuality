package com.cattsoft.phone.quality.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.SignalStrength;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Xiaohong on 2014/5/13.
 */
public class Signal implements Parcelable {
    public final static Parcelable.Creator<Signal> CREATOR = new Parcelable.Creator<Signal>() {
        @Override
        public Signal createFromParcel(Parcel source) {
            return new Signal(source);
        }

        @Override
        public Signal[] newArray(int size) {
            return new Signal[size];
        }
    };
    int mGsmSignalStrength = 99;
    int mGsmDbm = -113;
    int mCdmaDbm = -1;
    int mLteSignalStrength = -99;
    int level = 0;

    public Signal(Parcel parcel) {
        this.mGsmSignalStrength = parcel.readInt();
        this.mGsmDbm = parcel.readInt();
        this.mCdmaDbm = parcel.readInt();
        this.mLteSignalStrength = parcel.readInt();
        this.level = parcel.readInt();
    }

    public Signal(SignalStrength signalStrength) {
        if (signalStrength.isGsm()) {
            // GSM
            mGsmSignalStrength = signalStrength.getGsmSignalStrength();
            mGsmDbm = -113 + 2 * mGsmSignalStrength;
        } else {
            // CDMA
            mCdmaDbm = signalStrength.getCdmaDbm();
        }
        try {
            Method[] methods = android.telephony.SignalStrength.class.getMethods();
            for (Method mthd : methods) {
                if (mthd.getName().equals("getLteSignalStrength") || mthd.getName().equals("getLteSignalStrength"))
                    mLteSignalStrength = (Integer) mthd.invoke(signalStrength, new Object[]{});
                if (mthd.getName().equals("getLevel"))
                    level = (Integer) mthd.invoke(signalStrength, new Object[]{});
                if (mLteSignalStrength != -99 && level != 0)
                    break;
            }
        } catch (Exception e) {
            Log.d("Signal", "无法获取LTE网络信号", e);
        }
    }

    public int getmGsmSignalStrength() {
        return mGsmSignalStrength;
    }

    public void setmGsmSignalStrength(int mGsmSignalStrength) {
        this.mGsmSignalStrength = mGsmSignalStrength;
    }

    public int getmGsmDbm() {
        return mGsmDbm;
    }

    public void setmGsmDbm(int mGsmDbm) {
        this.mGsmDbm = mGsmDbm;
    }

    public int getmCdmaDbm() {
        return mCdmaDbm;
    }

    public void setmCdmaDbm(int mCdmaDbm) {
        this.mCdmaDbm = mCdmaDbm;
    }

    public int getmLteSignalStrength() {
        return mLteSignalStrength;
    }

    public void setmLteSignalStrength(int mLteSignalStrength) {
        this.mLteSignalStrength = mLteSignalStrength;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mGsmSignalStrength);
        dest.writeInt(mGsmDbm);
        dest.writeInt(mCdmaDbm);
        dest.writeInt(mLteSignalStrength);
        dest.writeInt(level);
    }
}
