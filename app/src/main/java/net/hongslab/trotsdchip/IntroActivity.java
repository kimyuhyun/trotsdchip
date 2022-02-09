package net.hongslab.trotsdchip;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import net.hongslab.trotsdchip.Libs.AppInfo;
import net.hongslab.trotsdchip.Libs.AsyncThread;
import net.hongslab.trotsdchip.Libs.Dlog;
import net.hongslab.trotsdchip.Libs.URLMaker;
import net.hongslab.trotsdchip.VO.CategoryVO;
import net.hongslab.trotsdchip.VO.MovieVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class IntroActivity extends AppCompatActivity {
    private AsyncThread mAsyncThread;
    private InterstitialAd mInterstitialAd;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().setStatusBarColor(Color.parseColor("#1B5E20"));
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //매니페스트에서 세로 고정하면 에러남.. 여기서 해줌
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //

        setContentView(R.layout.activity_intro);

        MobileAds.initialize(this, getString(R.string.ad_id));

        AppInfo._MY_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        AppInfo._CATEGORY.clear();
        AppInfo._TOP100.clear();
        AppInfo._RECOMMEND.clear();
        AppInfo._TODAY_RECOMMEND.clear();
        AppInfo._NEW_SONG.clear();

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (msg != null) {

                        if(mAsyncThread.getResult() != null) {
                            JSONObject jsonObject = new JSONObject(mAsyncThread.getResult());

                            JSONArray category = new JSONArray(jsonObject.getString("CATEGORY"));
                            for (int i = 0; i < category.length(); i++) {
                                JSONObject obj = category.getJSONObject(i);
                                CategoryVO vo = new CategoryVO();
                                vo.setCtNm(obj.getString("CT_NM"));
                                vo.setCtCode(obj.getString("CT_CODE"));
                                vo.setMovieId(obj.getString("MOVIE_ID"));
                                AppInfo._CATEGORY.add(vo);
                            }

                            JSONArray top100 = new JSONArray(jsonObject.getString("TOP100"));
                            for (int i = 0; i < top100.length(); i++) {
                                JSONObject obj = top100.getJSONObject(i);
                                MovieVO vo = new MovieVO();
                                vo.setIdx(obj.getString("IDX"));
                                vo.setSeq(i);
                                vo.setTitle(obj.getString("TITLE"));
                                vo.setPlayTime(obj.getString("PLAY_TIME"));
                                vo.setMovieId(obj.getString("MOVIE_ID"));
                                vo.setCtNm(obj.getString("CT_NM"));
                                if (obj.getString("IS_FAVORITE").equals("1")) {
                                    vo.setFavorite(true);
                                } else {
                                    vo.setFavorite(false);
                                }
                                AppInfo._TOP100.add(vo);
                            }

                            JSONArray recommend = new JSONArray(jsonObject.getString("RECOMMEND"));
                            for (int i = 0; i < recommend.length(); i++) {
                                JSONObject obj = recommend.getJSONObject(i);
                                MovieVO vo = new MovieVO();
                                vo.setSeq(i);
                                vo.setIdx(obj.getString("IDX"));
                                vo.setTitle(obj.getString("TITLE"));
                                vo.setPlayTime(obj.getString("PLAY_TIME"));
                                vo.setMovieId(obj.getString("MOVIE_ID"));
                                vo.setCtNm(obj.getString("CT_NM"));
                                if (obj.getString("IS_FAVORITE").equals("1")) {
                                    vo.setFavorite(true);
                                } else {
                                    vo.setFavorite(false);
                                }
                                AppInfo._RECOMMEND.add(vo);
                            }

                            JSONArray today_recommend = new JSONArray(jsonObject.getString("TODAY_RECOMMEND"));
                            for (int i = 0; i < today_recommend.length(); i++) {
                                JSONObject obj = today_recommend.getJSONObject(i);
                                MovieVO vo = new MovieVO();
                                vo.setSeq(i);
                                vo.setIdx(obj.getString("IDX"));
                                vo.setTitle(obj.getString("TITLE"));
                                vo.setPlayTime(obj.getString("PLAY_TIME"));
                                vo.setMovieId(obj.getString("MOVIE_ID"));
                                vo.setCtNm(obj.getString("CT_NM"));
                                if (obj.getString("IS_FAVORITE").equals("1")) {
                                    vo.setFavorite(true);
                                } else {
                                    vo.setFavorite(false);
                                }
                                AppInfo._TODAY_RECOMMEND.add(vo);
                            }

                            JSONArray new_song = new JSONArray(jsonObject.getString("NEW_SONG"));
                            for (int i = 0; i < new_song.length(); i++) {
                                JSONObject obj = new_song.getJSONObject(i);
                                MovieVO vo = new MovieVO();
                                vo.setSeq(i);
                                vo.setIdx(obj.getString("IDX"));
                                vo.setTitle(obj.getString("TITLE"));
                                vo.setPlayTime(obj.getString("PLAY_TIME"));
                                vo.setMovieId(obj.getString("MOVIE_ID"));
                                vo.setCtNm(obj.getString("CT_NM"));
                                if (obj.getString("IS_FAVORITE").equals("1")) {
                                    vo.setFavorite(true);
                                } else {
                                    vo.setFavorite(false);
                                }
                                AppInfo._NEW_SONG.add(vo);
                            }


                            Dlog.d(AppInfo._CATEGORY.size()+" _CATEGORY");
                            Dlog.d(AppInfo._TOP100.size()+" _TOP100");
                            Dlog.d(AppInfo._RECOMMEND.size()+" _RECOMMEND");
                            Dlog.d(AppInfo._TODAY_RECOMMEND.size()+" _TODAY_RECOMMEND");
                            Dlog.d(AppInfo._NEW_SONG.size()+" _NEW_SONG");

                            /*new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }
                            }, 2000);*/
                            loadInterstitialAd();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mAsyncThread = null;
                    super.handleMessage(msg);
                }
            }
        };

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("USR_ID", AppInfo._MY_ID);
        String param = "";
        try {
            param = URLMaker.hashToUrl(hashMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        mAsyncThread = new AsyncThread(getApplicationContext(), handler, "/model/get_json?"+param);
        mAsyncThread.execute();
    }


    private void loadInterstitialAd() {
        //전면배너
        mInterstitialAd = new InterstitialAd(this);
        if (AppInfo.DEBUG) {
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/8691691433");  //테스트
        } else {
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.banner_intro));
        }
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Dlog.d("onAdLoaded");
                if (AppInfo.getTopActivity(getApplicationContext()).equals(getPackageName() + ".IntroActivity")) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Dlog.d("onAdFailedToLoad: " + errorCode);
                startActivity(new Intent(IntroActivity.this, MainActivity.class));
                overridePendingTransition(0,0);

                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO
                        finish();
                    }
                }, 2000);
            }

            @Override
            public void onAdOpened() {
                Dlog.d("onAdOpened");
            }

            @Override
            public void onAdClicked() {
                Dlog.d("onAdClicked");
            }

            @Override
            public void onAdLeftApplication() {
                Dlog.d("onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                Dlog.d("onAdClosed");
                startActivity(new Intent(IntroActivity.this, MainActivity.class));
                overridePendingTransition(0,0);
                finish();
            }
        });
    }

}
