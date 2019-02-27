package ru.linkos.tcp_async;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class start_window extends AppCompatActivity {

    WiFiOperations wfo;
    TCPoperations tcpo;
    basicTCP btcp;
    protoOperations po;
    Boolean closeConnection = false;
    String stationIP;
    String AXSport = "55555";
    ConstraintLayout layer1;
    ConstraintLayout layer2;
    Socket targetSocket;
    TextView tw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_window);
        layer1 = findViewById(R.id.startingLayer);
        layer2 = findViewById(R.id.layer2);

        TextInputLayout ipInput = findViewById(R.id.ipInput);
        stationIP = ipInput.getEditText().getText().toString();

        tw = findViewById(R.id.textView);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }

    public void startConnection (View view) throws IOException, NoSuchAlgorithmException {
        wfo = new WiFiOperations(getApplicationContext(), stationIP);
        executeAsyncTask(wfo);
        //wfo.execute();
        layer1.setVisibility(View.INVISIBLE);
        layer2.setVisibility(View.VISIBLE);
        tcpo = new TCPoperations(getApplicationContext(),stationIP,AXSport);
        executeAsyncTask(tcpo);

        final Thread mthr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    control();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mthr.setDaemon(true);
        mthr.start();


      /*  final Handler switchHandler = new Handler();
        final Runnable runnable = new Runnable() {

            public void run() {
                try {
                    tw.setText(po.parseSrep(btcp.recieveTCP(targetSocket)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                switchHandler.postDelayed(this, 1000);}

        };
        switchHandler.removeCallbacks(runnable);
        switchHandler.postDelayed(runnable, 1000);*/


        //tcpo.execute();
    }

    public void control () throws IOException, NoSuchAlgorithmException, InterruptedException {
        byte [] data = po.makeCreq();
        btcp.sendTCP(targetSocket, data);
        po.parseCrep(btcp.recieveTCP(targetSocket));
        btcp.sendTCP(targetSocket, po.makeSreq());
        po.parseSrep(btcp.recieveTCP(targetSocket));
        btcp.sendTCP(targetSocket, po.makeMreq(2, true, false, false));
        po.parseSrep(btcp.recieveTCP(targetSocket));

        Thread.sleep(1000);
    }
}



