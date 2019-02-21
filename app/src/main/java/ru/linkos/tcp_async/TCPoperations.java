package ru.linkos.tcp_async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

public class TCPoperations extends AsyncTask<Void,Void,Void> {
    private final Context mContext;
    private final String targetIp;
    private final String targetPort;
    public TCPoperations(final Context context, final String ip,  final String port) {
        mContext = context;
        targetIp = ip;
        targetPort = port;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            connectTCP(targetIp, targetPort);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    public Socket connectTCP(String ip, String targetPort) throws IOException {
        Socket mySocket = new Socket(ip, Integer.decode(targetPort));
        Log.i("Socket ", String.valueOf(mySocket));
        return mySocket;
    }

}
