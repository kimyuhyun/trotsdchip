package net.hongslab.trotsdchip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import net.hongslab.trotsdchip.Flament.HomeFragment;
import net.hongslab.trotsdchip.Flament.InfoFragment;
import net.hongslab.trotsdchip.Flament.MyfavoriteFragment;
import net.hongslab.trotsdchip.Flament.PlaylistFragment;
import net.hongslab.trotsdchip.Flament.SearchFragment;
import net.hongslab.trotsdchip.Libs.AppInfo;
import net.hongslab.trotsdchip.Libs.Dlog;
import net.hongslab.trotsdchip.Libs.LoginSharedPreference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    public Fragment mFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @BindView(R.id.app_bar)
    LinearLayout app_bar;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottom_navigation;

    @BindView(R.id.close_popup)
    LinearLayout close_popup;


    @BindView(R.id.bottom_navigation_box)
    LinearLayout bottom_navigation_box;

    @BindView(R.id.ad_exit_container)
    LinearLayout ad_exit_container;

//    @BindView(R.id.ad_view_start)
//    BannerAdView ad_view_start;
//
//    @BindView(R.id.ad_view_exit)
//    BannerAdView ad_view_exit;


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Dlog.d("Main2Activity::onReceive: " + intent.getAction().toString());

            if (intent.getAction().toString().equals("SET_TOP_ACTIVITY")) {
                //앱 재실행 안하고 top로 올리는 코드.... 대박임..
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) { // 11
                    final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    final List<ActivityManager.RecentTaskInfo> recentTasks = activityManager.getRecentTasks(Integer.MAX_VALUE, ActivityManager.RECENT_IGNORE_UNAVAILABLE);

                    ActivityManager.RecentTaskInfo recentTaskInfo = null;

                    for (int i = 0; i < recentTasks.size(); i++) {
                        if (recentTasks.get(i).baseIntent.getComponent().getPackageName().equals(getPackageName())) {
                            recentTaskInfo = recentTasks.get(i);
                            break;
                        }
                    }
                    if (recentTaskInfo != null && recentTaskInfo.id > -1) {
                        activityManager.moveTaskToFront(recentTaskInfo.persistentId, ActivityManager.MOVE_TASK_WITH_HOME);
                    }
                }
                //
            } else if (intent.getAction().toString().equals("SETTING_TIMER")) {
                AppInfo.getInstance().getServiceInterface().smallPlayer();

                try {
                    mFragment = new InfoFragment();
                    mFragmentManager = getSupportFragmentManager();
                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.container, mFragment);
                    mFragmentTransaction.addToBackStack("info");
                    mFragmentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().toString().equals("MOVO_TO_SEARCH")) {
                AppInfo.getInstance().getServiceInterface().smallPlayer();

                try {
                    mFragment = new SearchFragment();
                    mFragmentManager = getSupportFragmentManager();
                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.container, mFragment);
                    mFragmentTransaction.addToBackStack("search");
                    mFragmentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        //adFit
//        ad_view_start.setClientId("DAN-vbccpvlovkty");
//        ad_view_start.loadAd();
//
//        ad_view_exit.setClientId("DAN-1h7zb8xxyt7p2");
//        ad_view_exit.loadAd();
//        //

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SETTING_TIMER");
        intentFilter.addAction("SET_TOP_ACTIVITY");
        intentFilter.addAction("MOVO_TO_SEARCH");
        registerReceiver(mBroadcastReceiver, intentFilter);

        mFragment = new HomeFragment();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.container, mFragment);
        mFragmentTransaction.commit();

        if (AppInfo.DEBUG) {
            //테스트
            AdRequest adRequest = new AdRequest.Builder().build();
            AdView adView = new AdView(this);
            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
            ad_exit_container.addView(adView);
            adView.loadAd(adRequest);
        } else {
            AdRequest adRequest = new AdRequest.Builder().build();
            AdView adView = new AdView(this);
            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            adView.setAdUnitId(getResources().getString(R.string.banner_exit));
            ad_exit_container.addView(adView);
            adView.loadAd(adRequest);
        }

        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.action_home) {
                    mFragment = new HomeFragment();
                    mFragmentManager = getSupportFragmentManager();
                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.container, mFragment);
                    mFragmentTransaction.commit();
                } else if (menuItem.getItemId() == R.id.action_favorite) {
                    mFragment = new MyfavoriteFragment();
                    mFragmentManager = getSupportFragmentManager();
                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.container, mFragment);
                    mFragmentTransaction.addToBackStack("favorite");
                    mFragmentTransaction.commit();
                } else if (menuItem.getItemId() == R.id.action_more) {
                    mFragment = new InfoFragment();
                    mFragmentManager = getSupportFragmentManager();
                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.container, mFragment);
                    mFragmentTransaction.addToBackStack("info");
                    mFragmentTransaction.commit();
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setNaviAndAppbar(boolean isShow) {
        if (isShow) {
            app_bar.setVisibility(View.VISIBLE);
            bottom_navigation_box.setVisibility(View.VISIBLE);
        } else {
            app_bar.setVisibility(View.GONE);
            bottom_navigation_box.setVisibility(View.GONE);
        }

    }

    public void gotoPlaylist() {
        mFragment = new PlaylistFragment();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.container, mFragment);
        mFragmentTransaction.addToBackStack("playlist");
        mFragmentTransaction.commit();

    }

    @OnClick(R.id.btn_search)
    public void search() {
//        startActivity(new Intent(getApplicationContext(), Search2Activity.class));
        mFragment = new SearchFragment();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.container, mFragment);
        mFragmentTransaction.addToBackStack("search");
        mFragmentTransaction.commit();
    }

    @OnClick(R.id.btn_cancel)
    public void btnCancel() {
        close_popup.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_exit)
    public void btnExit() {
        System.exit(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        Dlog.d("onPause");
        if (AppInfo.getInstance().getServiceInterface().getPlayerState() == 1) {
            AppInfo.getInstance().getServiceInterface().smallPlayer();
        }
    }

    @Override
    public void onBackPressed() {
        if (AppInfo.getInstance().getServiceInterface().getPlayerState() == 1) {
            AppInfo.getInstance().getServiceInterface().smallPlayer();
        } else {
            Dlog.d("onBackPressed:" + mFragmentManager.getBackStackEntryCount());
            if (mFragmentManager.getBackStackEntryCount() > 0) {
                super.onBackPressed();
            } else {
                //플레이어 죽이고..
                AppInfo.getInstance().getServiceInterface().hidePlayer();

                //여기서 종료 팝업
                if (!LoginSharedPreference.getUserInfo(getApplicationContext(), "RATING").equals("1")) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(getString(R.string.app_name) + " 리뷰와 평점 부탁드립니다.(별5개^^)");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            close_popup.setVisibility(View.VISIBLE);
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();

                    LoginSharedPreference.setUserInfo(getApplicationContext(), "RATING", "1");
                } else {
                    close_popup.setVisibility(View.VISIBLE);
                }
                //
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Dlog.d("onActivityResult");
        if (requestCode == 1) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                // TODO 동의를 얻지 못했을 경우의 처리
                Toast.makeText(getApplicationContext(), "'다른 앱 위에 표시' 권한 허용 해주셔야 원할한 이용이 가능합니다.", Toast.LENGTH_LONG).show();
            } else {
                //앱 재시작
                PackageManager packageManager = getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                startActivity(mainIntent);
                System.exit(0);
            }
        }
    }

}
