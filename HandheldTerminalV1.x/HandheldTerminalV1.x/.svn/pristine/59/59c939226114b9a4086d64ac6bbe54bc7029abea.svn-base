package com.cattsoft.phone.quality.ui.fragments.location;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.service.BaiduLocationService;
import com.cattsoft.phone.quality.task.GeoImageTask;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import roboguice.inject.InjectView;
import roboguice.receiver.RoboBroadcastReceiver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xiaohong on 2014/5/14.
 */
public class LocationFragment extends RoboLazyFragment {
    @InjectView(R.id.rootView)
    LinearLayout rootView;
    @InjectView(R.id.geoImage)
    ImageView geoImage;
    @InjectView(R.id.geoMask)
    FrameLayout geoMask;
    @InjectView(R.id.address)
    TextView address;
    @InjectView(R.id.longitude)
    TextView longitude;
    @InjectView(R.id.latitude)
    TextView latitude;
    @InjectView(R.id.date)
    TextView date;

    private LocationReceiver receiver;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            geoMask.setVisibility(View.GONE);
            if (message.what != 0)
                geoImage.setImageResource(R.drawable.location_err);
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_location, container, false);
    }

    @Override
    protected void doResourceInBackground(Application app) {
        super.doResourceInBackground(app);
        receiver = new LocationReceiver();
    }

    @Override
    protected void doStuffInBackground(Application app) {
        super.doStuffInBackground(app);
        Intent intent = new Intent(getActivity().getApplicationContext(), BaiduLocationService.class);
        intent.putExtra("local", true);
        getActivity().startService(intent);

        registerReceiver(receiver, new IntentFilter(BaiduLocationService.LOCATION_ACTION));
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
    }

    private class LocationReceiver extends RoboBroadcastReceiver {
        @Override
        protected void handleReceive(Context context, Intent intent) {
            super.handleReceive(context, intent);
            int state = intent.getIntExtra("state", 0);
            if (1 == state) {
                // 定位失败
                handler.obtainMessage(1).sendToTarget();
                return;
            }
            latitude.setText(Double.toString(intent.getDoubleExtra("latitude", -1)));
            longitude.setText(Double.toString(intent.getDoubleExtra("longitude", -1)));
            address.setText(intent.getStringExtra("address"));
            date.setText(intent.getStringExtra("date"));
            double def = 4.9E-324;

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("center", intent.getDoubleExtra("longitude", def) + "," + intent.getDoubleExtra("latitude", def));
            params.put("markers", intent.getDoubleExtra("longitude", def) + "," + intent.getDoubleExtra("latitude", def));
            params.put("width", geoImage.getWidth());
            params.put("height", geoImage.getHeight());
            params.put("zoom", 15);
            if (geoMask.getVisibility() == View.GONE)
                geoMask.setVisibility(View.VISIBLE);
            new GeoImageTask(getActivity(), handler, getString(R.string.location_geo_staticimage), params, geoImage).execute();
        }
    }
}
