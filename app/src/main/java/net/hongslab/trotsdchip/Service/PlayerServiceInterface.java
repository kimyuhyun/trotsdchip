package net.hongslab.trotsdchip.Service;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import net.hongslab.trotsdchip.Libs.Dlog;


public class PlayerServiceInterface {
    private ServiceConnection mServiceConnection;
    private PlayerService mService;

    public PlayerServiceInterface(Context context) {
        Dlog.d("PlayerServiceInterface");

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Dlog.d("onServiceConnected");
                mService = ((PlayerService.PlayerServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Dlog.d("onServiceDisconnected");
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, PlayerService.class).setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void play() {
        if (mService != null) {
            mService.play();
        }
    }

    public int getPlayerState() {
        if (mService != null) {
            return mService.getPlayerState();
        }

        return 0;
    }

    public void fullScreenPlayer() {
        if (mService != null) {
            Dlog.d("fullScreenPlayer");
            mService.fullScreenPlayer();
        }
    }

    public void smallPlayer() {
        if (mService != null) {
            Dlog.d("smallPlayer");
            mService.smallPlayer(0,0);
        }
    }

    public void hidePlayer() {
        if (mService != null) {
            Dlog.d("hidePlayer");
            mService.hidePlayer();
        }
    }


    public void bottomPlayer() {
        if (mService != null) {

        }
    }


}
