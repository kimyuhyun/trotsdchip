package net.hongslab.trotsdchip.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import net.hongslab.trotsdchip.Flament.MyfavoriteFragment;
import net.hongslab.trotsdchip.Flament.PlaylistFragment;
import net.hongslab.trotsdchip.Flament.SearchFragment;
import net.hongslab.trotsdchip.Libs.AppInfo;
import net.hongslab.trotsdchip.Libs.AsyncThread;
import net.hongslab.trotsdchip.Libs.Dlog;
import net.hongslab.trotsdchip.Libs.SetViewScale;
import net.hongslab.trotsdchip.Libs.URLMaker;
import net.hongslab.trotsdchip.R;
import net.hongslab.trotsdchip.VO.MovieVO;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> mList = new ArrayList<>();
    private Activity mActivity;
    private Fragment mFragment;
    private AsyncThread mAsyncThread2;
    private SetViewScale mSetViewScale;

    public RecyclerAdapter(Activity activity, Fragment fragment, ArrayList<Object> list) {
        this.mList = list;
        this.mActivity = activity;
        this.mFragment = fragment;

        mSetViewScale = new SetViewScale(activity);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_row, parent, false);
            return new MyViewHolder(view);
        } else {
//            View unifiedNativeLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_ad_container, parent, false);
//            return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);

        if (viewType == 0) {
            final MovieVO item = (MovieVO) mList.get(position);
            MyViewHolder myViewHolder = (MyViewHolder) holder;

            Glide.with(mActivity)
                    .load("https://img.youtube.com/vi/" + item.getMovieId().trim() + "/0.jpg")
                    .error(Glide.with(mActivity).load(R.drawable.app_icon))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(myViewHolder.iv_thumb);

            if (item.isFavorite()) {
                myViewHolder.iv_favorite.setImageResource(R.drawable.ic_heart_duotone);
            } else {
                myViewHolder.iv_favorite.setImageResource(R.drawable.ic_heart_light);
            }

            if (item.getMovieId().equals(AppInfo._CURRENT_MOVIE_ID)) {
                Glide.with(mActivity)
                        .load(R.drawable.playing2)
                        .transition(new DrawableTransitionOptions().crossFade())
                        .into(myViewHolder.iv_playing);
                myViewHolder.iv_playing.setVisibility(View.VISIBLE);
                myViewHolder.root.setBackgroundColor(Color.parseColor("#CCCCCC"));
            } else {
                myViewHolder.iv_playing.setVisibility(View.GONE);
                myViewHolder.root.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

            myViewHolder.tv_title.setText(item.getTitle());
            myViewHolder.tv_ct_nm.setText(item.getCtNm());
            myViewHolder.tv_play_time.setText(item.getPlayTime());

            myViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFragment != null) {
                        try {
                            ((MyfavoriteFragment) mFragment).reloadPlayList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            ((PlaylistFragment) mFragment).reloadPlayList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            ((SearchFragment) mFragment).reloadPlayList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Dlog.d("item.getSeq(): "+item.getSeq());
                    AppInfo._PLAYING_LIST.clear();
                    AppInfo._PLAYING_LIST.addAll(AppInfo._PLAY_LIST);
                    AppInfo._SEQ = item.getSeq();
                    AppInfo.getInstance().getServiceInterface().fullScreenPlayer();
                    AppInfo.getInstance().getServiceInterface().play();

                }
            });

            myViewHolder.iv_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFavorite(item.getIdx(), position);
                }
            });
        } else {
//            UnifiedNativeAd nativeAd = (UnifiedNativeAd) mList.get(position);
//            populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = mList.get(position);
//        if (recyclerViewItem instanceof UnifiedNativeAd) {
//            return 1;
//        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
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

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

//    public class UnifiedNativeAdViewHolder extends RecyclerView.ViewHolder {
//        private UnifiedNativeAdView adView;
//
//        public UnifiedNativeAdView getAdView() {
//            return adView;
//        }
//
//        UnifiedNativeAdViewHolder(View view) {
//            super(view);
//            adView = (UnifiedNativeAdView) view.findViewById(R.id.ad_view);
//            adView.setIconView(adView.findViewById(R.id.ad_icon));
//            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
//            adView.setBodyView(adView.findViewById(R.id.ad_body));
//        }
//    }


    private void setFavorite(String idx, final int position) {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (msg != null) {

                        if (mAsyncThread2.getResult() != null) {
                            Dlog.d(mAsyncThread2.getResult());

                            if (mAsyncThread2.getResult().equals("1")) {
                                ((MovieVO) mList.get(position)).setFavorite(true);
                                Toast.makeText(mActivity, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                ((MovieVO) mList.get(position)).setFavorite(false);
                                Toast.makeText(mActivity, "즐겨찾기에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            }

                            notifyItemChanged(position);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mAsyncThread2 = null;
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
        mAsyncThread2 = new AsyncThread(mActivity, handler, "/model/set_favorite/?" + param);
        mAsyncThread2.execute();
    }

//    private void populateNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
//        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
//        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
////        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
//
//        NativeAd.Image icon = nativeAd.getIcon();
//
//        if (icon == null) {
//            adView.getIconView().setVisibility(View.INVISIBLE);
//        } else {
//            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
//            adView.getIconView().setVisibility(View.VISIBLE);
//        }
///*
//        if (nativeAd.getPrice() == null) {
//            adView.getPriceView().setVisibility(View.INVISIBLE);
//        } else {
//            adView.getPriceView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
//        }
//
//        if (nativeAd.getStore() == null) {
//            adView.getStoreView().setVisibility(View.INVISIBLE);
//        } else {
//            adView.getStoreView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
//        }
//
//        if (nativeAd.getStarRating() == null) {
//            adView.getStarRatingView().setVisibility(View.INVISIBLE);
//        } else {
//            ((RatingBar) adView.getStarRatingView())
//                    .setRating(nativeAd.getStarRating().floatValue());
//            adView.getStarRatingView().setVisibility(View.VISIBLE);
//        }
//
//        if (nativeAd.getAdvertiser() == null) {
//            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
//        } else {
//            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
//            adView.getAdvertiserView().setVisibility(View.VISIBLE);
//        }*/
//
//        // Assign native ad object to the native view.
//        adView.setNativeAd(nativeAd);
//    }
}