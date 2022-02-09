package net.hongslab.trotsdchip.Flament;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import net.hongslab.trotsdchip.Libs.AppInfo;
import net.hongslab.trotsdchip.Libs.AsyncThread;
import net.hongslab.trotsdchip.Libs.Dlog;
import net.hongslab.trotsdchip.Libs.URLMaker;
import net.hongslab.trotsdchip.MainActivity;
import net.hongslab.trotsdchip.R;
import net.hongslab.trotsdchip.VO.CategoryVO;
import net.hongslab.trotsdchip.VO.MovieVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class HomeFragment extends Fragment {
    private Unbinder unbinder;
    private RecyclerAdapter mRecyclerAdapter;

    @BindView(R.id.nest_scroll_view)
    NestedScrollView nest_scroll_view;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @BindView(R.id.iv_top100)
    ImageView iv_top100;

    @BindView(R.id.iv_recommend)
    ImageView iv_recommend;

    @BindView(R.id.iv_today_recommend)
    ImageView iv_today_recommend;

    @BindView(R.id.iv_new_song)
    ImageView iv_new_song;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recycler_view.setLayoutManager(linearLayoutManager);
        mRecyclerAdapter = new RecyclerAdapter();
        recycler_view.setAdapter(mRecyclerAdapter);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        Glide.with(getActivity())
                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new BlurTransformation(1)))
                .load("https://i.ytimg.com/vi/" + AppInfo._TOP100.get(0).getMovieId() + "/maxresdefault.jpg")
                .error(Glide.with(getActivity()).load(R.drawable.app_icon))
                .transition(new DrawableTransitionOptions().crossFade())
                .into(iv_top100);

        Glide.with(getActivity())
                .load("https://i.ytimg.com/vi/" + AppInfo._RECOMMEND.get(0).getMovieId() + "/maxresdefault.jpg")
                .error(Glide.with(getActivity()).load(R.drawable.app_icon))
                .transition(new DrawableTransitionOptions().crossFade())
                .into(iv_recommend);

        Glide.with(getActivity())
                .load("https://i.ytimg.com/vi/" + AppInfo._TODAY_RECOMMEND.get(0).getMovieId() + "/maxresdefault.jpg")
                .error(Glide.with(getActivity()).load(R.drawable.app_icon))
                .transition(new DrawableTransitionOptions().crossFade())
                .into(iv_today_recommend);


        Glide.with(getActivity())
                .load("https://i.ytimg.com/vi/" + AppInfo._NEW_SONG.get(0).getMovieId() + "/maxresdefault.jpg")
                .error(Glide.with(getActivity()).load(R.drawable.app_icon))
                .transition(new DrawableTransitionOptions().crossFade())
                .into(iv_new_song);

    }



    @Override
    public void onStop() {
        super.onStop();
        Dlog.d("onStop()");
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            CategoryVO item = AppInfo._CATEGORY.get(position);


            Glide.with(getActivity())
                    .load("https://i.ytimg.com/vi/" + item.getMovieId() + "/mqdefault.jpg")
                    .error(Glide.with(getActivity()).load(R.drawable.app_icon))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(holder.iv_ct_thumb);


            holder.tv_ct_name.setText(item.getCtNm());

            holder.iv_ct_thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCatePlayList(item.getCtCode(), item.getCtNm());
                }
            });
        }

        @Override
        public int getItemCount() {
            return AppInfo._CATEGORY.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_ct_thumb)
            ImageView iv_ct_thumb;

            @BindView(R.id.tv_ct_name)
            TextView tv_ct_name;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

        }
    }


    private AsyncThread mAsyncThread;
    private void getCatePlayList(String code, String categoryNm) {

        AppInfo._PLAY_LIST_TITLE = categoryNm;
        AppInfo._PLAY_LIST.clear();

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (msg != null) {
                        if(mAsyncThread.getResult() != null) {

                            JSONArray array = new JSONArray(mAsyncThread.getResult());

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                MovieVO vo = new MovieVO();
                                vo.setSeq(i);
                                vo.setIdx(obj.getString("IDX"));
                                vo.setTitle(obj.getString("TITLE"));
                                vo.setPlayTime(obj.getString("PLAY_TIME"));
                                vo.setMovieId(obj.getString("MOVIE_ID"));
                                vo.setCtNm(obj.getString("CT_NM"));

                                AppInfo._PLAY_LIST.add(vo);
                            }

                            ((MainActivity) getActivity()).gotoPlaylist();
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
        hashMap.put("CT_CODE", code);
        String param = "";
        try {
            param = URLMaker.hashToUrl(hashMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        mAsyncThread = new AsyncThread(getActivity(), handler, "/model/get_category_list?" + param);
        mAsyncThread.execute();
    }



    @OnClick(R.id.iv_top100)
    public void playTop100() {
        Dlog.d("top100");
        AppInfo._PLAY_LIST_TITLE = "인기 트로트 TOP100";
        AppInfo._PLAY_LIST.clear();
        AppInfo._PLAY_LIST.addAll(AppInfo._TOP100);
        ((MainActivity) getActivity()).gotoPlaylist();
    }

    @OnClick(R.id.iv_recommend)
    public void playRecommend() {
        Dlog.d("playRecommend");
        AppInfo._PLAY_LIST_TITLE = "당신을 위한 추천곡";
        AppInfo._PLAY_LIST.clear();
        AppInfo._PLAY_LIST.addAll(AppInfo._RECOMMEND);
        ((MainActivity) getActivity()).gotoPlaylist();
    }

    @OnClick(R.id.iv_today_recommend)
    public void playTodayRecommend() {
        Dlog.d("playTodayRecommend");
        AppInfo._PLAY_LIST_TITLE = "오늘의 추천곡";
        AppInfo._PLAY_LIST.clear();
        AppInfo._PLAY_LIST.addAll(AppInfo._TODAY_RECOMMEND);
        ((MainActivity) getActivity()).gotoPlaylist();
    }

    @OnClick(R.id.iv_new_song)
    public void playNewSong() {
        Dlog.d("playNewSong");
        AppInfo._PLAY_LIST_TITLE = "새로 추가된 노래";
        AppInfo._PLAY_LIST.clear();
        AppInfo._PLAY_LIST.addAll(AppInfo._NEW_SONG);
        ((MainActivity) getActivity()).gotoPlaylist();
    }



}