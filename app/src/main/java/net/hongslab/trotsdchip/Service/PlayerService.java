package net.hongslab.trotsdchip.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import net.hongslab.trotsdchip.Libs.AppInfo;
import net.hongslab.trotsdchip.Libs.AsyncThread;
import net.hongslab.trotsdchip.Libs.Dlog;
import net.hongslab.trotsdchip.Libs.SetViewScale;
import net.hongslab.trotsdchip.Libs.URLMaker;
import net.hongslab.trotsdchip.R;
import net.hongslab.trotsdchip.VO.MovieVO;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class PlayerService extends Service {
    private final IBinder mBinder = new PlayerServiceBinder();
    private String mUrl;
    private Context mContext;
    private SetViewScale mSetViewScale;


    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private View mView;
    private CardView mCardView;
    private WebView mWebView;
    private LinearLayout ll_small_ctrl_box;
    private LinearLayout mWebViewBox;
    private LinearLayout mBtnSmallPlayerShow;
    private ImageView mSmallPlayToggle;
    private TextView mSmallSongName;


    private LinearLayout mAppBar;

    private LinearLayout mUtilBox;
    private LinearLayout mBtnTimer;
    private TextView mTvTimer;
    private LinearLayout mBtnSaveBattery;
    private LinearLayout mBtnPlayMode;
    private Button mBtnSaveModeCancel;
    private TextView mTvPlayMode;
    private LinearLayout mLLBlind;

    private RecyclerView mRecyclerView;
    private PlayListRecyclerAdapter mPlayListRecyclerAdapter;
    private boolean mIsPlay = true;


    /**
     * 0: 비활성화
     * 1: 풀스크린 플레이
     * 2: 스몰 플레이
     * 3: 스몰 플레이 컨트롤쇼
     */
    public int mPlayerState = 0;


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class PlayerServiceBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    private BroadcastReceiver mScreenOnOff = new BroadcastReceiver() {
        public static final String screenOff = "android.intent.action.SCREEN_OFF";
        public static final String screenOn = "android.intent.action.SCREEN_ON";

        @Override
        public void onReceive(Context contex, Intent intent) {
            try {
                Dlog.d(intent.getAction().toString());
                if (intent.getAction().equals(screenOff)) {
                    mWebView.loadUrl("JavaScript:pauseVideo();");

                    mIsPlay = false;

                    setPlayToggle(mIsPlay);

                } else if (intent.getAction().equals(screenOn)) {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Dlog.d("onCreate");

        mContext = this;

        /**
         * 화면 꺼짐/켜짐 체크
         */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenOnOff, intentFilter);
        /********************/

        mSetViewScale = new SetViewScale(mContext);

        try {
            final LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mParams = new WindowManager.LayoutParams(
                        MATCH_PARENT,
                        MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE/* | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE*/,
                        PixelFormat.TRANSLUCENT);
            } else {
                mParams = new WindowManager.LayoutParams(
                        MATCH_PARENT,
                        MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE/* | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE*/,
                        PixelFormat.TRANSLUCENT);
            }

            mView = inflate.inflate(R.layout.player, null);
            mView.setVisibility(View.GONE);


            mCardView = (CardView) mView.findViewById(R.id.card_view);

            mAppBar = (LinearLayout) mView.findViewById(R.id.app_bar);
            mWebViewBox = (LinearLayout) mView.findViewById(R.id.web_view_box);
            ll_small_ctrl_box = (LinearLayout) mView.findViewById(R.id.ll_small_ctrl_box);

            mUtilBox = (LinearLayout) mView.findViewById(R.id.util_box);
            mLLBlind = (LinearLayout) mView.findViewById(R.id.ll_blind);
            mBtnTimer = (LinearLayout) mView.findViewById(R.id.btn_timer);
            mTvTimer = (TextView) mView.findViewById(R.id.tv_timer);
            mBtnSaveBattery = (LinearLayout) mView.findViewById(R.id.btn_save_battery);
            mBtnSaveModeCancel = (Button) mView.findViewById(R.id.btn_save_mode_cancel);
            mBtnPlayMode = (LinearLayout) mView.findViewById(R.id.btn_play_mode);
            mTvPlayMode = (TextView) mView.findViewById(R.id.tv_play_mode);
            mSmallSongName = (TextView) mView.findViewById(R.id.small_song_name);

            //검색창 터치
            mView.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("MOVO_TO_SEARCH");
                    sendBroadcast(intent);
                }
            });

            //스몰플레이어 액션
            mView.findViewById(R.id.small_player_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int next = AppInfo._SEQ + 1;
                    if (next >= AppInfo._PLAY_LIST.size()) {
                        next = 0;
                    }
                    AppInfo._SEQ = next;
                    play();
                }
            });
            mSmallPlayToggle = (ImageView) mView.findViewById(R.id.small_player_toggle);
            mSmallPlayToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsPlay) {
                        mIsPlay = false;
                    } else {
                        mIsPlay = true;
                    }
                    setPlayToggle(mIsPlay);

                }
            });
            mView.findViewById(R.id.small_player_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hidePlayer();
                }
            });
            //


            //스몰플레이어 쇼
            mBtnSmallPlayerShow = (LinearLayout) mView.findViewById(R.id.btn_small_player_show);
            mBtnSmallPlayerShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    smallPlayer(0,0);
                }
            });
            //

            //유틸박스
            mBtnTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("SETTING_TIMER");
                    sendBroadcast(intent);

                }
            });
            mBtnPlayMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppInfo._PLAY_MODE++;

                    if (AppInfo._PLAY_MODE > 2) {
                        AppInfo._PLAY_MODE = 0;
                    }

                    if (AppInfo._PLAY_MODE == 0) {
                        mTvPlayMode.setText("순차재생");
                        Toast.makeText(getApplicationContext(),"순차재생 중입니다.", Toast.LENGTH_SHORT).show();
                    } else if (AppInfo._PLAY_MODE == 1) {
                        mTvPlayMode.setText("랜덤재생");
                        Toast.makeText(getApplicationContext(),"랜덤재생 중입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        mTvPlayMode.setText("한곡재생");
                        Toast.makeText(getApplicationContext(),"한곡재생 중입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mBtnSaveBattery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAppBar.setVisibility(View.GONE);
                    mUtilBox.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    mLLBlind.setVisibility(View.VISIBLE);

                    mSetViewScale.setViewScale(mWebViewBox,200,200);

                }
            });
            mBtnSaveModeCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fullScreenPlayer();
                }
            });
            //



            mWebView = (WebView) mView.findViewById(R.id.web_view);
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setMediaPlaybackRequiresUserGesture(false);
            webSettings.setSupportMultipleWindows(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setEnableSmoothTransition(true);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setSupportZoom(false);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mWebView.setWebChromeClient(new MyChromeClient());


            mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mPlayListRecyclerAdapter = new PlayListRecyclerAdapter();
            mRecyclerView.setAdapter(mPlayListRecyclerAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mWindowManager.addView(mView, mParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPlayToggle(boolean isPlay) {
        if (isPlay) {
            mWebView.loadUrl("JavaScript:playVideo();");
            mSmallPlayToggle.setImageResource(R.drawable.ic_pause_black_24dp);

        } else {
            mWebView.loadUrl("JavaScript:pauseVideo();");
            mSmallPlayToggle.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
    }

    public int getPlayerState() {
        return mPlayerState;
    }

    public void play() {
        Dlog.d("PlayerService::play" + AppInfo._SEQ);

        try {
            AppInfo._CURRENT_MOVIE_ID = AppInfo._PLAYING_LIST.get(AppInfo._SEQ).getMovieId();
            Intent intent = new Intent("NOW_PLAYING");
            sendBroadcast(intent);

            mView.setVisibility(View.VISIBLE);

            mUrl = getResources().getString(R.string.url) + "/yt_player" +
                    "?MOVIE_ID=" + AppInfo._PLAYING_LIST.get(AppInfo._SEQ).getMovieId() +
                    "&SEQ=" + AppInfo._SEQ +
                    "&USR_ID=" + AppInfo._MY_ID;
            mWebView.loadUrl(mUrl);

            mSmallSongName.setText(AppInfo._PLAYING_LIST.get(AppInfo._SEQ).getTitle());
            mSmallSongName.setSelected(true);

            Dlog.d(mUrl);

            for (int i = 0; i < AppInfo._PLAYING_LIST.size(); i++) {
                AppInfo._PLAYING_LIST.get(i).setPlaying(false);
            }

            AppInfo._PLAYING_LIST.get(AppInfo._SEQ).setPlaying(true);
            mPlayListRecyclerAdapter.notifyDataSetChanged();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mRecyclerView.smoothScrollToPosition(AppInfo._SEQ);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2000);

            mIsPlay = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class MyChromeClient extends WebChromeClient {
        public MyChromeClient() {
            Dlog.d("MyChromeClient");
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                //dispatchTouch(371.48407, 159.84543);
            }
        }

        String tmp[];

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            //Dlog.d("onConsoleMessage : " + '\n' + consoleMessage.message() + '\n' + consoleMessage.messageLevel() + '\n' + consoleMessage.sourceId());
            try {
                tmp = consoleMessage.message().split(":");

                if (tmp.length == 2) {
                    //영상종료
                    if (tmp[0].equals("0")) {
                        int next = Integer.parseInt(tmp[1]);

                        if (AppInfo._PLAY_MODE == 0) {   //순차재생
                            next = next + 1;
                            if (next >= AppInfo._PLAYING_LIST.size()) {
                                next = 0;
                            }
                        } else if (AppInfo._PLAY_MODE == 1) {    //랜덤재생
                            Random random = new Random();
                            next = random.nextInt(AppInfo._PLAYING_LIST.size());
                        }
                        AppInfo._SEQ = next;
                        play();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.onConsoleMessage(consoleMessage);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWindowManager != null) {
            if (mView != null) {
                mWindowManager.removeView(mView);
                mView = null;
                unregisterReceiver(mScreenOnOff);
                System.runFinalizersOnExit(true);
                System.exit(0);
            }
            mWindowManager = null;
        }
    }


    public class PlayListRecyclerAdapter extends RecyclerView.Adapter<PlayListRecyclerAdapter.ViewHolder> {
        private AsyncThread mAsyncThread;

        @Override
        public PlayListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_row, parent, false);
            return new PlayListRecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final PlayListRecyclerAdapter.ViewHolder holder, final int position) {
            final MovieVO item = AppInfo._PLAYING_LIST.get(position);

            Glide.with(mContext)
                    .load("https://img.youtube.com/vi/" + item.getMovieId().trim() + "/0.jpg")
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(holder.iv_thumb);

            if (item.isFavorite()) {
                holder.iv_favorite.setImageResource(R.drawable.ic_heart_duotone);
            } else {
                holder.iv_favorite.setImageResource(R.drawable.ic_heart_light);
            }

            if (item.isPlaying()) {
                Glide.with(mContext)
                        .load(R.drawable.playing2)
                        .transition(new DrawableTransitionOptions().crossFade())
                        .into(holder.iv_playing);
                holder.iv_playing.setVisibility(View.VISIBLE);
                holder.root.setBackgroundColor(Color.parseColor("#cccccc"));
            } else {
                holder.iv_playing.setVisibility(View.GONE);
                holder.root.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            holder.tv_title.setText(Html.fromHtml(item.getTitle()));
            holder.tv_ct_nm.setText(item.getCtNm());
            holder.tv_play_time.setText(item.getPlayTime());

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppInfo._SEQ = item.getSeq();
                    play();
                }
            });

            holder.iv_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFavorite(item.getIdx(), position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return AppInfo._PLAYING_LIST.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.root)
            LinearLayout root;
            @BindView(R.id.iv_thumb)
            ImageView iv_thumb;
            @BindView(R.id.tv_title)
            TextView tv_title;
            @BindView(R.id.tv_ct_nm)
            TextView tv_ct_nm;
            @BindView(R.id.tv_play_time)
            TextView tv_play_time;
            @BindView(R.id.iv_favorite)
            ImageView iv_favorite;

            @BindView(R.id.iv_playing)
            ImageView iv_playing;


            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }


        private void setFavorite(String idx, final int position) {
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        if (msg != null) {

                            if (mAsyncThread.getResult() != null) {
                                Dlog.d(mAsyncThread.getResult());

                                if (mAsyncThread.getResult().equals("1")) {
                                    AppInfo._PLAYING_LIST.get(position).setFavorite(true);
                                    Toast.makeText(getApplicationContext(), "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    AppInfo._PLAYING_LIST.get(position).setFavorite(false);
                                    Toast.makeText(getApplicationContext(), "즐겨찾기에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                                notifyItemChanged(position);
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
            hashMap.put("IDX", idx);
            hashMap.put("USR_ID", AppInfo._MY_ID);
            String param = "";
            try {
                param = URLMaker.hashToUrl(hashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mAsyncThread = new AsyncThread(getApplicationContext(), handler, "/model/set_favorite?" + param);
            mAsyncThread.execute();
        }
    }


    public void fullScreenPlayer() {
        if (mView == null) {
            Dlog.d("mView 널 임.....");
            onCreate();
        }

        try {
            //터치무브 리스너 걸기
            mWebView.setOnTouchListener(null);
            //

            ll_small_ctrl_box.setVisibility(View.GONE);
            mLLBlind.setVisibility(View.GONE);

            mAppBar.setVisibility(View.VISIBLE);
            mUtilBox.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);

            mCardView.setRadius(0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mParams = new WindowManager.LayoutParams(
                        MATCH_PARENT,
                        MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        /*WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,*/
                        PixelFormat.TRANSLUCENT);
            } else {
                mParams = new WindowManager.LayoutParams(
                        MATCH_PARENT,
                        MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        /*WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,*/
                        PixelFormat.TRANSLUCENT);
            }
            mWindowManager.updateViewLayout(mView, mParams);

            mSetViewScale.setViewScale(mWebViewBox,800,400);


            mPlayerState = 1;

        } catch (Exception e) {
            e.printStackTrace();
        }


        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
            }
        }, 2200);


        //브로드캐스트 액션으로 MainActivity 를 최상위로 올린다.
        Intent intent = new Intent("SET_TOP_ACTIVITY");
        sendBroadcast(intent);
        //

    }

    public void smallPlayer(int x, int y) {
        Dlog.d("smallPlayer");
        try {
            mLLBlind.setVisibility(View.GONE);
            mAppBar.setVisibility(View.GONE);
            mUtilBox.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            ll_small_ctrl_box.setVisibility(View.GONE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mParams = new WindowManager.LayoutParams(
                        200,
                        200,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE/* | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL*/,
                        PixelFormat.TRANSLUCENT);
            } else {
                mParams = new WindowManager.LayoutParams(
                        200,
                        200,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE/* | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL*/,
                        PixelFormat.TRANSLUCENT);
            }
            mParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;

            mCardView.setRadius(100);

            //터치무브 리스너 걸기
            mWebView.setOnTouchListener(mTouchListener);
            //

            mSetViewScale.setViewScale(mWebViewBox,300,300);

            if (y == 0) {
                y = 150;
            }

            mParams.x = x;
            mParams.y = y;
            mWindowManager.updateViewLayout(mView, mParams);

            mPlayerState = 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void smallPlayerControlShow(int x, int y) {
        Dlog.d("smallPlayerControlShow");
        mCardView.setRadius(10);
        ll_small_ctrl_box.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams = new WindowManager.LayoutParams(
                    MATCH_PARENT,
                    WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE/* | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL*/,
                    PixelFormat.TRANSLUCENT);
        } else {
            mParams = new WindowManager.LayoutParams(
                    MATCH_PARENT,
                    WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE/* | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL*/,
                    PixelFormat.TRANSLUCENT);
        }
        mParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;

        mParams.x = x;
        mParams.y = y;

        mSetViewScale.setViewScale(mWebViewBox, 800,150);

        mWindowManager.updateViewLayout(mView, mParams);

        mPlayerState = 3;


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPlayerState == 3) {
                    smallPlayer(mParams.x, mParams.y);
                }
            }
        }, 5000);
    }

    public void hidePlayer() {
        Dlog.d("hidePlayer");
        AppInfo._CURRENT_MOVIE_ID = "";
        Intent intent = new Intent("NOW_PLAYING");
        sendBroadcast(intent);

        mWebView.loadUrl("");
        mView.setVisibility(View.GONE);
        mPlayerState = 0;
    }

    Handler mHandler = new Handler();


    private float START_X, START_Y;                            //움직이기 위해 터치한 시작 점
    private int PREV_X, PREV_Y;                                //움직이기 이전에 뷰가 위치한 점
    private int MAX_X = -1, MAX_Y = -1;                        //뷰의 위치 최대 값

    /**
     * 뷰의 위치가 화면 안에 있게 최대값을 설정한다
     */
    private void setMaxPosition() {
        DisplayMetrics matrix = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(matrix);		//화면 정보를 가져와서

        MAX_X = matrix.widthPixels - mView.getWidth();			//x 최대값 설정
        MAX_Y = matrix.heightPixels - mView.getHeight();			//y 최대값 설정

    }

    /**
     * 뷰의 위치가 화면 안에 있게 하기 위해서 검사하고 수정한다.
     */
    private void optimizePosition() {
        //최대값 넘어가지 않게 설정
        if(mParams.x > MAX_X) mParams.x = MAX_X;
        if(mParams.y > MAX_Y) mParams.y = MAX_Y;
        if(mParams.x < 0) mParams.x = 0;
        if(mParams.y < 0) mParams.y = 0;
    }

    /**
     * 가로 / 세로 모드 변경 시 최대값 다시 설정해 주어야 함.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setMaxPosition();		//최대값 다시 설정
        optimizePosition();		//뷰 위치 최적화
    }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if ((MAX_X / 2) < mParams.x) {
                        mParams.x = MAX_X;
                    } else {
                        mParams.x = 0;
                    }
                    mWindowManager.updateViewLayout(mView, mParams);    //뷰 업데이트

                    int x1 = (int) (event.getRawX() - START_X);    //이동한 거리
                    int y1 = (int) (event.getRawY() - START_Y);    //이동한 거리
                    Dlog.d("ACTION_UP : " + x1 + ", " + y1);

                    if (x1 < 15 && x1 > -15 && y1 < 15 && y1 > -15) {
                        if (mPlayerState == 2) {
                            smallPlayerControlShow(mParams.x, mParams.y);
                        } else if (mPlayerState == 3) {
                            fullScreenPlayer();
                        }
                    }


                    break;
                case MotionEvent.ACTION_DOWN:                //사용자 터치 다운이면
//                    Dlog.d("ACTION_DOWN");
                    if (MAX_X == -1) {
                        setMaxPosition();
                    }
                    START_X = event.getRawX();                    //터치 시작 점
                    START_Y = event.getRawY();                    //터치 시작 점
                    PREV_X = mParams.x;                            //뷰의 시작 점
                    PREV_Y = mParams.y;                            //뷰의 시작 점
                    //Dlog.d("ACTION_DOWN : " + PREV_X + ", " + PREV_Y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = (int) (event.getRawX() - START_X);    //이동한 거리
                    int y = (int) (event.getRawY() - START_Y);    //이동한 거리
//                    Dlog.d("ACTION_MOVE : " + x + ", " + y);
                    if (x != 0 && y != 0) {
                    }
                    //터치해서 이동한 만큼 이동 시킨다
                    mParams.x = PREV_X - x;
                    mParams.y = PREV_Y - y;
                    optimizePosition();        //뷰의 위치 최적화
                    mWindowManager.updateViewLayout(mView, mParams);    //뷰 업데이트
                    break;
            }

            return true;
        }
    };
}
