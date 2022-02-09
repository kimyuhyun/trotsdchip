package net.hongslab.trotsdchip.Flament;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import net.hongslab.trotsdchip.Libs.Dlog;
import net.hongslab.trotsdchip.Libs.GridSpacingItemDecoration;
import net.hongslab.trotsdchip.MainActivity;
import net.hongslab.trotsdchip.R;
import net.hongslab.trotsdchip.VO.MovieVO;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.BlurTransformation;


public class PlaylistFragment extends Fragment {
    private Unbinder unbinder;
    private RecyclerAdapter mVer2RecyclerAdapter;
    private ArrayList<Object> mList = new ArrayList<>();

    @BindView(R.id.nest_scroll_view)
    NestedScrollView nest_scroll_view;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.iv_title_bg)
    ImageView iv_title_bg;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Dlog.d("_SEQ : " + AppInfo._SEQ + ", " + AppInfo._CURRENT_MOVIE_ID);
            mVer2RecyclerAdapter.notifyDataSetChanged();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getActivity().unregisterReceiver(mBroadcastReceiver);
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
                    Dlog.d("네비쇼");
                    ((MainActivity) getActivity()).setNaviAndAppbar(true);
                } else {
                    Dlog.d("네비하이드");
                    ((MainActivity) getActivity()).setNaviAndAppbar(false);
                }
            }
        });


        tv_title.setText(AppInfo._PLAY_LIST_TITLE);

        Glide.with(getActivity())
                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new BlurTransformation(50)))
                .load("https://i.ytimg.com/vi/" + AppInfo._PLAY_LIST.get(0).getMovieId() + "/maxresdefault.jpg")
                .error(Glide.with(getActivity()).load(R.drawable.app_icon))
                .transition(new DrawableTransitionOptions().crossFade())
                .into(iv_title_bg);


        recycler_view.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_view.addItemDecoration(new GridSpacingItemDecoration(2, 30, false));
        mVer2RecyclerAdapter = new RecyclerAdapter(getActivity(), this, mList);
        recycler_view.setAdapter(mVer2RecyclerAdapter);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        //여기서 네이티브 배너 넣어준다..
        mList.addAll(AppInfo._PLAY_LIST);
//        insertNativeAds();
        //

    }

    @Override
    public void onStop() {
        super.onStop();
        Dlog.d("onStop()");
    }

    private void insertNativeAds() {
        //네이티브배너뷰 넣기
//        Dlog.d("AppInfo._NATIVE_ADS.size(): " + AppInfo._NATIVE_ADS.size());
//        if (AppInfo._NATIVE_ADS.size() > 0) {
//            int offset = (mList.size() / AppInfo._NATIVE_ADS.size()) + 1;
//            int index = 5;
//            for (UnifiedNativeAd ad : AppInfo._NATIVE_ADS) {
//                try {
//                    mList.add(index, ad);
//                    index = index + offset;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        //
//
//        mVer2RecyclerAdapter.notifyDataSetChanged();
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
            AppInfo._PLAYING_LIST.clear();
            AppInfo._PLAYING_LIST.addAll(AppInfo._PLAY_LIST);
            AppInfo._PLAY_MODE = 1;
            Random random = new Random();
            AppInfo._SEQ = random.nextInt(AppInfo._PLAYING_LIST.size());
            AppInfo.getInstance().getServiceInterface().fullScreenPlayer();
            AppInfo.getInstance().getServiceInterface().play();
        }
    }
}
