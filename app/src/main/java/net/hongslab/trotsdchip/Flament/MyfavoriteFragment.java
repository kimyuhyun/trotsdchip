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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import net.hongslab.trotsdchip.Adapter.RecyclerAdapter;
import net.hongslab.trotsdchip.Libs.AppInfo;
import net.hongslab.trotsdchip.Libs.AsyncThread;
import net.hongslab.trotsdchip.Libs.Dlog;
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
import jp.wasabeef.glide.transformations.BlurTransformation;


public class MyfavoriteFragment extends Fragment {
    private Unbinder unbinder;

    private AsyncThread mAsyncThread;
    private RecyclerAdapter mVer2RecyclerAdapter;
    private ArrayList<Object> mList = new ArrayList<>();

    @BindView(R.id.nest_scroll_view)
    NestedScrollView nest_scroll_view;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @BindView(R.id.iv_title_bg)
    ImageView iv_title_bg;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Dlog.d("_SEQ : " + AppInfo._SEQ + ", " + AppInfo._CURRENT_MOVIE_ID);
            mVer2RecyclerAdapter.notifyDataSetChanged();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_favorite, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("NOW_PLAYING");
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);

        ((MainActivity) getActivity()).setNaviAndAppbar(true);

        nest_scroll_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY > scrollY) {
                    ((MainActivity) getActivity()).setNaviAndAppbar(true);
                } else {
                    ((MainActivity) getActivity()).setNaviAndAppbar(false);
                }
            }
        });

        recycler_view.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_view.addItemDecoration(new GridSpacingItemDecoration(2, 30, false));
        mVer2RecyclerAdapter = new RecyclerAdapter(getActivity(), this, mList);
        recycler_view.setAdapter(mVer2RecyclerAdapter);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        getFavorite();

    }

    @Override
    public void onStop() {
        super.onStop();
        Dlog.d("onStop()");
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }


    private void getFavorite() {
        mList.clear();

        mVer2RecyclerAdapter.notifyDataSetChanged();

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (msg != null) {

                        if (mAsyncThread.getResult() != null) {
                            JSONArray array = new JSONArray(mAsyncThread.getResult());

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);

                                MovieVO vo = new MovieVO();

                                vo.setSeq(i);
                                vo.setIdx(obj.getString("IDX"));
                                vo.setTitle(obj.getString("TITLE"));
                                vo.setMovieId(obj.getString("MOVIE_ID"));
                                vo.setPlayTime(obj.getString("PLAY_TIME"));
                                vo.setCtNm(obj.getString("CT_NM"));
                                vo.setFavorite(true);

                                mList.add(vo);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mAsyncThread = null;
                    mVer2RecyclerAdapter.notifyDataSetChanged();
                    super.handleMessage(msg);

                    if (mList.size() > 0) {
                        MovieVO item = (MovieVO) mList.get(0);

                        Glide.with(getActivity())
                                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new BlurTransformation(50)))
                                .load("https://i.ytimg.com/vi/" + item.getMovieId() + "/maxresdefault.jpg")
                                .error(Glide.with(getActivity()).load(R.drawable.app_icon))
                                .transition(new DrawableTransitionOptions().crossFade())
                                .into(iv_title_bg);
                    }
                }
            }
        };

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("USR_ID", "" + AppInfo._MY_ID);
        String param = "";
        try {
            param = URLMaker.hashToUrl(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAsyncThread = new AsyncThread(getActivity(), handler, "/model/get_favorite?" + param);
        mAsyncThread.execute();
    }

    public void reloadPlayList() {
        Dlog.d("Myfavorite::reloadPlayList");
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

}