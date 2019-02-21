package ru.linkos.tcp_async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;

import static android.support.v4.content.ContextCompat.getSystemService;

public class WiFiOperations extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private final String targetIp;
    String error = "No error";
    int i = 0;
    public static final String EXTRA_MESSAGE_ERR_NO = "Error_number";
    public WiFiOperations(final Context context, final String ip) {
        mContext = context;
        targetIp = ip;
    }



    @Override
    protected Void doInBackground(Void... voids) {
        WifiManager wfm = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        assert wfm != null;
        Log.i("wifi ", String.valueOf(wfm.isWifiEnabled()));
        while (error.equals("No error") || error.equals("Station reachable")) {
            if (wfm.isWifiEnabled()) {

                Log.i("WiFi ", "enabled");
                if (wfm.getConnectionInfo().getSSID().equals("\"215\"")) {
                    Log.i("Wifi is: ", "OK");
                    error = "No error";
                    try {
                        pingStation(targetIp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    error = "Wrong network";
                    Log.i("err: ", error);
                    startError(mContext, error);
                    return null;
                }
            } else {


                error = "turn WiFi on";
                Log.i("err: ", error);
                startError(mContext, error);
                return null;
            }


        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

            Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();


    }
    public void pingStation(String ip) throws IOException {

        Log.i("IP is: ", String.valueOf(InetAddress.getByName(ip)));
        if (InetAddress.getByName(ip) != null) {
                InetAddress stationIP = InetAddress.getByName(ip);
            if (stationIP.isReachable(1000)) {
                Log.i("station is: ", "reachable");
                error = "Station reachable";
            } else {
                Log.i("station is: ", "unreachable");
                error = "Station unreachable";
                startError(mContext, error);
                            }
        } else {
            Log.i("station is: ", "unreachable vasche");
            error = "Station unreachable sovsem, stranno kak-to";
            startError(mContext, error);

        }


    }

    public void startError(Context curContext, String error) {
        Intent errorIntent = new Intent(curContext, startErrorActivity.class);
        errorIntent.putExtra(EXTRA_MESSAGE_ERR_NO, error);
        curContext.startActivity(errorIntent);
    }

}

