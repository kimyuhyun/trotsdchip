package net.hongslab.trotsdchip.Flament;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import net.hongslab.trotsdchip.Libs.Dlog;
import net.hongslab.trotsdchip.MainActivity;
import net.hongslab.trotsdchip.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class InfoFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.tv_app_version)
    TextView tv_app_version;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
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

        try {
            PackageInfo i = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            tv_app_version.setText("앱 버전 Ver " + i.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Dlog.d("onStop()");
    }

    @OnClick(R.id.btn_share)
    public void btnShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Intent.EXTRA_SUBJECT, "'"+getString(R.string.app_name)+"' 에\n초대받으셨습니다.");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " 다운로드 바로 고우!!\nhttps://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "공유하기"));
    }

    @OnClick(R.id.btn_review)
    public void btnReview() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName()));
        startActivity(intent);
    }

    @OnClick(R.id.btn_request)
    public void btnRequest() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        String[] address = {"hongslab01@gmail.com"};
        email.putExtra(Intent.EXTRA_EMAIL, address);
        email.putExtra(Intent.EXTRA_SUBJECT,getActivity().getString(R.string.app_name) + " 문의사항");
        email.putExtra(Intent.EXTRA_TEXT,"");
        startActivity(email);
    }

    @OnClick(R.id.btn_timer)
    public void btnTimer() {
        final String items[] = {"5분","10분","20분","30분","1시간","2시간","3시간"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int exitSec = 0;
                if(which == 0) {
                    exitSec = 300 * 1000;
                } else if (which == 1) {
                    exitSec = 600 * 1000;
                } else if (which == 2) {
                    exitSec = 1200 * 1000;
                } else if (which == 3) {
                    exitSec = 1800 * 1000;
                } else if (which == 4) {    //1시간
                    exitSec = 3600 * 1000;
                } else if (which == 5) {    //2시간
                    exitSec = 7200 * 1000;
                } else if (which == 6) {    //3시간
                    exitSec = 10800 * 1000;
                }

                Toast.makeText(getActivity(),items[which] + "후 종료 설정 되었습니다.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, exitSec);
            }
        });
        builder.show();
    }
}
