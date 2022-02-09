package net.hongslab.trotsdchip.Libs;

import android.app.Activity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

/**
 * Created by USER on 2016-04-14.
 */
public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            toast.cancel();

            // 프로세스 끝내기
            ActivityCompat.finishAffinity(activity);
            System.runFinalizersOnExit(true);
            System.exit(0);
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
