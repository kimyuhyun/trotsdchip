package net.hongslab.trotsdchip.Libs;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;


import net.hongslab.trotsdchip.Service.PlayerServiceInterface;
import net.hongslab.trotsdchip.VO.CategoryVO;
import net.hongslab.trotsdchip.VO.MovieVO;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppInfo extends MultiDexApplication {

//    public static ArrayList<UnifiedNativeAd> _NATIVE_ADS = new ArrayList<>();
    public static String _MY_ID = "";
    public static String _CURRENT_MOVIE_ID = "";
    public static String _PLAY_LIST_TITLE = "";
    public static int _SEQ = 0;

    /**
     * 0: 순차재생
     * 1: 랜덤재생
     * 2: 한곡재생
     */
    public static int _PLAY_MODE = 0;

    public static ArrayList<MovieVO> _PLAY_LIST = new ArrayList<>();
    public static ArrayList<MovieVO> _PLAYING_LIST = new ArrayList<>();
    public static ArrayList<MovieVO> _TOP100 = new ArrayList<>();
    public static ArrayList<CategoryVO> _CATEGORY = new ArrayList<>();
    public static ArrayList<MovieVO> _RECOMMEND = new ArrayList<>();
    public static ArrayList<MovieVO> _TODAY_RECOMMEND = new ArrayList<>();
    public static ArrayList<MovieVO> _NEW_SONG = new ArrayList<>();





    public static Activity _ACTIVITY;


    public static boolean DEBUG = true;
    private static AppInfo mInstance;
    private PlayerServiceInterface mInterface;


    @Override
    protected void attachBaseContext(Context context){
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate(){
        super.onCreate();

        DEBUG = isDebuggable(this);
        mInstance = this;
        mInterface = new PlayerServiceInterface(getApplicationContext());

    }

    public static AppInfo getInstance() {
        return mInstance;
    }

//    public PlayerServiceInterface getServiceInterface() {
//        return mInterface;
//    }

    public PlayerServiceInterface getServiceInterface() {
        return mInterface;
    }

    public static boolean isDebuggable(Context context) {
        boolean debuggable = false;

        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            /* debuggable variable will remain false */
        }

        return debuggable;
    }


    public static String getMoney(String math) {
        try {
            double tmp = Double.parseDouble(math);
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(20);
            return nf.format(tmp);
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static String getDateFormat(String fullDate) {

        try {
            String tmp[] = fullDate.split("-");
            int year = Integer.parseInt(tmp[0]);
            int month = Integer.parseInt(tmp[1]);
            int day = Integer.parseInt(tmp[02]);

            Log.d("####", year + " - " + month + " - " + day);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.DATE, day);

            String dayOfWeek = "";

            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    dayOfWeek = "일";
                    break;
                case 2:
                    dayOfWeek = "월";
                    break;
                case 3:
                    dayOfWeek = "화";
                    break;
                case 4:
                    dayOfWeek = "수";
                    break;
                case 5:
                    dayOfWeek = "목";
                    break;
                case 6:
                    dayOfWeek = "금";
                    break;
                case 7:
                    dayOfWeek = "토";
                    break;
            }

            String tmp2 = month + "/" + day + "(" + dayOfWeek + ")";

            return tmp2;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    //최상위 액티비티
    public static String getTopActivity(Context context){
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> info;
            info = activityManager.getRunningTasks(1);
            return info.get(0).topActivity.getClassName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 자바용 dateDiff
     * @param time
     * @return
     */
    public static String util_convert_to_millis(long time) {

        //time 값은 substring(0,10)
        long currentTime = new Date().getTime() / 1000;
        long inputTime = time;
        long diffTime = currentTime - inputTime;
        String postTime = "";
        long tmp;

        Dlog.d(currentTime + " - " + inputTime + " = " + diffTime);

        if (diffTime < 60) {
            postTime = "방금";
        } else if(diffTime < 3600) {
            tmp = diffTime / 60;
            postTime = (int) tmp + "분 전";
        } else if(diffTime < 86400) {
            tmp = diffTime / 3600;
            postTime = (int) tmp + "시간 전";
        } else if(diffTime < 604800) {
            tmp = diffTime / 86400;
            postTime = (int) tmp + "일 전";
        } else if(diffTime > 604800) {
            Date date = new Date(time*1000);
            postTime = (date.getYear() - 100) + "/" + date.getMonth() + "/" + date.getDate();
        }

        return postTime;
    }

    public static boolean checkOverlays(Activity activity) {
        if (!Settings.canDrawOverlays(activity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("'다른앱 위에 그리기 권한' 을 허용해주셔야 정상적인 음악감상이 가능 합니다.");
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivityForResult(intent, 1);
                }
            });
            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();
            return false;
        } else {
            return true;
        }
    }
}

