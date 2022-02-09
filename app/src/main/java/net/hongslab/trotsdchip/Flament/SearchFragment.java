package net.hongslab.trotsdchip.Flament;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import net.hongslab.trotsdchip.Adapter.RecyclerAdapter;
import net.hongslab.trotsdchip.Libs.AppInfo;
import net.hongslab.trotsdchip.Libs.AsyncThread;
import net.hongslab.trotsdchip.Libs.Dlog;
import net.hongslab.trotsdchip.Libs.FlowLayout.FlowLayout;
import net.hongslab.trotsdchip.Libs.GridSpacingItemDecoration;
import net.hongslab.trotsdchip.Libs.URLMaker;
import net.hongslab.trotsdchip.MainActivity;
import net.hongslab.trotsdchip.R;
import net.hongslab.trotsdchip.VO.MovieVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class SearchFragment extends Fragment {
    private Unbinder unbinder;
    private AsyncThread mAsyncThread;
    private AsyncThread mAsyncThread2;
    private AsyncThread mAsyncThread3;


    private RecyclerAdapter mVer2RecyclerAdapter;
    private ArrayList<Object> mList = new ArrayList<>();

    @BindView(R.id.nest_scroll_view)
    NestedScrollView nest_scroll_view;

    @BindView(R.id.app_bar)
    LinearLayout app_bar;

    @BindView(R.id.my_query_box)
    FlowLayout my_query_box;

    @BindView(R.id.et_query)
    EditText et_query;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @BindView(R.id.ll_ctrl_box)
    LinearLayout ll_ctrl_box;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Dlog.d("_SEQ : " + AppInfo._SEQ + ", " + AppInfo._CURRENT_MOVIE_ID);

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("NOW_PLAYING");
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);

        ((MainActivity) getActivity()).setNaviAndAppbar(false);

        getSearchQuery();

        et_query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    findSong();
                }
                return false;
            }
        });

        recycler_view.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_view.addItemDecoration(new GridSpacingItemDecoration(2, 30, false));
        mVer2RecyclerAdapter = new RecyclerAdapter(getActivity(), this, mList);
        recycler_view.setAdapter(mVer2RecyclerAdapter);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private void getSearchQuery() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (msg != null) {
                        if (mAsyncThread.getResult() != null) {
                            JSONArray array = new JSONArray(mAsyncThread.getResult());
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                final String query = obj.getString("KEYWORD");

                                // Inflater View 만들기
                                View view = (View) getLayoutInflater().inflate(R.layout.recommend_row, null);
                                TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
                                tv_title.setText("#" + obj.getString("KEYWORD"));
                                tv_title.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        et_query.setText(query);
                                        findSong();
                                    }
                                });
                                my_query_box.addView(view);
                            }
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
        mAsyncThread = new AsyncThread(getActivity(), handler, "/model/get_search_query?"+param);
        mAsyncThread.execute();
    }


    private void findSong() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (msg != null) {
                        if (mAsyncThread3.getResult() != null) {
                            mList.clear();

                            JSONArray array = new JSONArray(mAsyncThread3.getResult());
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                MovieVO vo = new MovieVO();
                                vo.setSeq(i);
                                vo.setIdx(obj.getString("IDX"));
                                vo.setTitle(obj.getString("TITLE"));
                                vo.setPlayTime(obj.getString("PLAY_TIME"));
                                vo.setMovieId(obj.getString("MOVIE_ID"));
                                vo.setCtNm(obj.getString("CT_NM"));
                                mList.add(vo);
                            }


                            //여기서 네이티브 배너 넣어준다..
                            //insertNativeAds();
                            //


                            if (array.length() == 0) {
                                Toast.makeText(getActivity(), "검색된 노래가 없습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                ll_ctrl_box.setVisibility(View.VISIBLE);
                                mVer2RecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mAsyncThread3 = null;
                    super.handleMessage(msg);

                    //키보드내리기
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_query.getWindowToken(), 0);
                    //
                }
            }
        };

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("USR_ID", AppInfo._MY_ID);
        hashMap.put("QUERY", et_query.getText().toString().trim());
        String param = "";
        try {
            param = URLMaker.hashToUrl(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAsyncThread3 = new AsyncThread(getActivity(), handler, "/model/find_song?" + param);
        mAsyncThread3.execute();
    }

    @OnClick(R.id.btn_back)
    public void back() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.btn_find)
    public void search() {
        if (et_query.getText().toString().trim().equals("")) {
            Toast.makeText(getActivity(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        findSong();
    }

    public void reloadPlayList() {
        if (AppInfo.checkOverlays(getActivity())) {
            AppInfo._PLAY_LIST.clear();

            for (int i = 0; i < mList.size(); i++) {
                Object item = mList.get(i);
                if (item instanceof MovieVO) {
                    AppInfo._PLAY_LIST.add((MovieVO) item);
                }
            }
            AppInfo._PLAY_MODE = 0;
        }
    }

    @OnClick(R.id.btn_play)
    public void btnPlay() {
        if (AppInfo.checkOverlays(getActivity())) {
            AppInfo._PLAY_LIST.clear();

            for (int i = 0; i < mList.size(); i++) {
                Object item = mList.get(i);
                if (item instanceof MovieVO) {
                    AppInfo._PLAY_LIST.add((MovieVO) item);
                }
            }
            AppInfo._PLAYING_LIST.clear();
            AppInfo._PLAYING_LIST.addAll(AppInfo._PLAY_LIST);
            AppInfo._PLAY_MODE = 0;
            AppInfo._SEQ = 0;
            AppInfo.getInstance().getServiceInterface().fullScreenPlayer();
            AppInfo.getInstance().getServiceInterface().play();
        }
    }

    @OnClick(R.id.btn_shuffle)
    public void btnShuffle() {
        if (AppInfo.checkOverlays(getActivity())) {
            AppInfo._PLAY_LIST.clear();

            for (int i = 0; i < mList.size(); i++) {
                Object item = mList.get(i);
                if (item instanceof MovieVO) {
                    AppInfo._PLAY_LIST.add((MovieVO) item);
                }
            }
            AppInfo._PLAYING_LIST.clear();
            AppInfo._PLAYING_LIST.addAll(AppInfo._PLAY_LIST);
            AppInfo._PLAY_MODE = 1;
            Random random = new Random();
            AppInfo._SEQ = random.nextInt(AppInfo._PLAY_LIST.size());
            AppInfo.getInstance().getServiceInterface().fullScreenPlayer();
            AppInfo.getInstance().getServiceInterface().play();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Dlog.d("onActivityResult");
        if (requestCode == 1) {
            if (!Settings.canDrawOverlays(getActivity())) {
                // TODO 동의를 얻지 못했을 경우의 처리
                Toast.makeText(getActivity(), "'다른 앱 위에 표시' 권한 허용 해주셔야 원할한 이용이 가능합니다.", Toast.LENGTH_LONG).show();
            } else {
                //앱 재시작
                PackageManager packageManager = getActivity().getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(getActivity().getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                startActivity(mainIntent);
                System.exit(0);
            }
        }
    }
}