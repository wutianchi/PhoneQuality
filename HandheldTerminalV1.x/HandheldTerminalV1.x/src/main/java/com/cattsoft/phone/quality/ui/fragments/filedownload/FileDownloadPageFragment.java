package com.cattsoft.phone.quality.ui.fragments.filedownload;

import android.app.Application;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.ui.fragments.PagedFragment;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * Created by yushiwei on 15-5-24.
 */
public class FileDownloadPageFragment extends PagedFragment {
   public FileDownloadPageFragment() {
       super();
       this.tabArrayId = R.array.filedownload_tab;
       this.classArrayId = R.array.filedownload_tab_classname;
   }
}