package com.cattsoft.phone.quality;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.cattsoft.phone.quality.service.aidl.IPhoneStateService;
import com.cattsoft.phone.quality.ui.adapter.FragmentAdapter;
import com.cattsoft.phone.quality.ui.fragments.filedownload.FileChoiceFragment;
import com.cattsoft.phone.quality.ui.fragments.filedownload.FileDownloadFragment;
import com.cattsoft.phone.quality.ui.fragments.menu.SpeedServerChoiceFragment;
import com.cattsoft.phone.quality.ui.fragments.speed.TesterFragment;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneQualityActivity extends RoboActionBarActivity implements SpeedServerChoiceFragment.CallBack, FileChoiceFragment.CallBack {

    public static final String FLAG_KEY = "flag";

    public static final String TITLE_KEY = "title";

    public static final String NOTIFY_KEY = "notify";

    int mPosition = -1;

    private String TAG = "menu";

    @InjectView(R.id.drawer_layout)
    private DrawerLayout mDrawerLayout;

    @InjectView(R.id.drawer)
    private LinearLayout mDrawer;

    @InjectView(R.id.left_drawer)
    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;

    private CharSequence mTitle;

    @InjectResource(R.array.nav_menu)
    private String[] mMenuTitles;

    @InjectResource(R.array.nav_menu_fragment_classname)
    private String[] mMenuFragments;

    private List<HashMap<String, Object>> mList;

    private SimpleAdapter mAdapter;

    private Map<String, Class> fragmentMap = null;

    /** 程序是否退出标记 */
    private boolean exit = false;

    private Toast exitToast = null;

    private IPhoneStateService binder;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_activity_main);

        mTitle = mDrawerTitle = getTitle();

        initFragmentMap();

        initDrawerList();

        initLayout();

        showDefaultFragment(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void showDefaultFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mDrawerList.setItemChecked(getResources().getInteger(R.integer.default_fragment_index), true);
            showFragment(getResources().getInteger(R.integer.default_fragment_index));
            highlightSelectedItem();
        }
    }

    private void initDrawerList() {
        mDrawerList.setAdapter(new SimpleAdapter(
                this,
                initMList(),
                R.layout.drawer_item_layout,
                new String[]{FLAG_KEY, TITLE_KEY, NOTIFY_KEY},
                new int[]{R.id.flag, R.id.title, R.id.notify}
        ));
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (null != fragmentMap.get(mMenuTitles[i]))
                    showFragment(i);
                mDrawerLayout.requestFocus();
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void initLayout() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_navigation_drawer, R.string.nav_drawer_open, R.string.nav_drawer_close) {

            /** Called when drawer is closed */
            public void onDrawerClosed(View view) {
                highlightSelectedItem();
                supportInvalidateOptionsMenu();
            }

            /** Called when a drawer is opened */
            public void onDrawerOpened(View drawerView) {
                drawerView.requestFocus();
                supportInvalidateOptionsMenu();
            }
        };
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private List<HashMap<String, Object>> initMList() {
        mList = new ArrayList<HashMap<String, Object>>();

        TypedArray flags = getResources().obtainTypedArray(R.array.nav_flag);

        for (int i = 0; i < mMenuTitles.length; i++) {
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put(TITLE_KEY, mMenuTitles[i]);
            hm.put(NOTIFY_KEY, "");
            hm.put(FLAG_KEY, flags.getResourceId(i, -1));
            mList.add(hm);
        }
        flags.recycle();

        return mList;
    }

    private void initFragmentMap() {
        fragmentMap = new HashMap<String, Class>();
        for (int i = 0; i < mMenuTitles.length; i++) {
            try {
                fragmentMap.put(mMenuTitles[i], Class.forName(mMenuFragments[i]));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.menu_setting:
                // create intent to perform web search for this planet
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showFragment(final int position) {
        if (position == mPosition)
            return;
        // update the main content by replacing fragments
        Fragment fragment = null;
        String title = mMenuTitles[position];

        Class<?> cls = fragmentMap.get(title);
        if (null != cls) {
            try {
                fragment = (Fragment) cls.newInstance();

                replaceFragment(fragment, new Bundle());

                mPosition = position;
            } catch (Exception e) {
                Log.e("MenuActivity", "无法实例化界面片段!", e);
            }
        }
    }

    public void replaceFragment(Fragment fragment, Bundle args) {
        if (null != fragment) {
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, fragment).commit();
        }
    }

    private void highlightSelectedItem() {
        int selectedItem = mDrawerList.getCheckedItemPosition();
        if (selectedItem > -1) {
            // update selected item and title, then close the drawer
            mDrawerList.setSelection(mPosition != selectedItem ? mPosition : selectedItem);
            setTitle(mMenuTitles[selectedItem]);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        // mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        if (exit) {
            fragmentMap.clear();
            super.onBackPressed();
            exitToast.cancel();
            return;
        }
        exit = true;
        if (null == exitToast)
            exitToast = Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT);
        exitToast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                exit = false;
            }
        }, 2000);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        try {
            super.unregisterReceiver(receiver);
        } catch (Throwable e) {
            Log.d(TAG, "解除接收器注册时出现异常，当前接收器可能未注册：" + receiver, e);
        }
    }

    public IPhoneStateService getBinder() {
        return binder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragmentMap = null;
        mList = null;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, String.format("KeyDown KeyCode: %d, Event: %s", keyCode, event));
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                ViewPager pager = (ViewPager) findViewById(R.id.pager);
                if (pager != null) {
                    pager.requestFocus();
                    boolean success = pager.arrowScroll(ViewPager.FOCUS_LEFT);
                    mDrawerLayout.requestFocus();
                    if (success) {
                        return true;
                    }
                }
                if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    pager = (ViewPager) findViewById(R.id.pager);
                    if (pager != null) {
                        pager.requestFocus();
                        boolean success = pager.arrowScroll(ViewPager.FOCUS_RIGHT);
                        Log.i(TAG, success + "");
                        mDrawerLayout.requestFocus();
                    }
                }
                ExpandableListView listView = (ExpandableListView) findViewById(R.id.speed_server_expandable_list_view);
                if (listView != null) {
                    listView.requestFocus();
                }

                ListView lstView = (ListView) findViewById(R.id.file_choice_list_view);
                if (lstView != null) {
                    lstView.requestFocus();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:

                BootstrapButton button = null;
                if ((button = (BootstrapButton) findViewById(R.id.file_download_start)) != null) {
                    button.performClick();
                }
                Button closeBtn = (Button) findViewById(R.id.dismiss);
                if (closeBtn != null) {
                    closeBtn.performClick();
                } else if ((button = (BootstrapButton) findViewById(R.id.speed_action)) != null) {
                    button.performClick();
                }
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onChildClick(String showText, String ip) {
        showFragment(getResources().getInteger(R.integer.default_fragment_index));
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        String speedTestName = getString(R.string.nav_menu_netspeed_speed);
        for (int i = 0; i < pager.getAdapter().getCount(); i++) {
            String name = pager.getAdapter().getPageTitle(i).toString();
            if (speedTestName.equals(name)) {
                setTesterConfig(showText, ip, pager, i);
                break;
            }
        }
        mDrawerLayout.requestFocus();
        highlightSelectedItem();
    }

    private void setTesterConfig(String showText, String ip, ViewPager pager, int i) {
        pager.setCurrentItem(i, true);
        View view = pager.getChildAt(i);
        if (pager.getAdapter() instanceof FragmentAdapter) {
            FragmentAdapter adapter = (FragmentAdapter) pager.getAdapter();
            Fragment fragment = adapter.getItem(i);
            if (fragment instanceof TesterFragment) {
                ((TesterFragment) fragment).setServer(showText, ip);
                saveTesterConfig(showText, ip);
            }
        }
    }

    private void saveTesterConfig(String server, String ip) {
        SharedPreferences preferences = getBaseContext().getSharedPreferences("config", MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TesterFragment.CURR_IP, ip);
        editor.putString(TesterFragment.CURR_SERVER, server);
        editor.apply();
    }

    @Override
    public void onClick(String showText, String url) {
        showFragment(1);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        String speedTestName = getString(R.string.nav_menu_filedownload_download);
        for (int i = 0; i < pager.getAdapter().getCount(); i++) {
            String name = pager.getAdapter().getPageTitle(i).toString();
            if (speedTestName.equals(name)) {
                setFileDownloadConfig(showText, url, pager, i);
                break;
            }
        }
        mDrawerLayout.requestFocus();
        highlightSelectedItem();
    }

    private void setFileDownloadConfig(String showText, String url, ViewPager pager, int i) {
        pager.setCurrentItem(i, true);
        View view = pager.getChildAt(i);
        if (pager.getAdapter() instanceof FragmentAdapter) {
            Fragment fragment = ((FragmentAdapter) pager.getAdapter()).getItem(i);
            if (fragment instanceof FileDownloadFragment) {
                ((FileDownloadFragment) fragment).setConfig(showText, url);
                saveFileDownloadConfig(showText, url);
            }
        }
    }

    private void saveFileDownloadConfig(String showText, String url) {
        SharedPreferences preferences = getBaseContext().getSharedPreferences("config", MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FileDownloadFragment.FILEDOWNLOAD_CURR_URL, url);
        editor.putString(FileDownloadFragment.FILEDOWNLOAD_CURR_WEBSITE, showText);
        editor.apply();
    }

    class OnKey implements View.OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            return false;
        }
    }
}
