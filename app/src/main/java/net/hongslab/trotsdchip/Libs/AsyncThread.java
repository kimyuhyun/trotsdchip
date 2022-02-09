package net.hongslab.trotsdchip.Libs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;


import net.hongslab.trotsdchip.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 2018-01-16.
 */

public class AsyncThread extends AsyncTask<Void, Void, String> {


    private String mURL = "";
    private String mResult = "";
    private Handler mHandler;

    public AsyncThread(Context context, Handler handler, String param){
        this.mHandler = handler;
        mURL = context.getResources().getString(R.string.url);
        mURL += param;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(Void... params) {

        try {
            URL url = new URL(mURL);
            Dlog.d(mURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
            conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            String str = "";
            while ((line = reader.readLine()) != null){
                str += line;
            }
            return str;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            mResult = result;
            mHandler.sendEmptyMessage(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getResult(){
        return mResult;
    }
}
