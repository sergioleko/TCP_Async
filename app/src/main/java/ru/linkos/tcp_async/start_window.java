package ru.linkos.tcp_async;

import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;

public class start_window extends AppCompatActivity {
    WiFiOperations wfo;
    TCPoperations tcpo;
    Boolean closeConnection = false;
    String stationIP;
    String AXSport = "30050";
    ConstraintLayout layer1;
    ConstraintLayout layer2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_window);
        layer1 = findViewById(R.id.startingLayer);
        layer2 = findViewById(R.id.layer2);

        TextInputLayout ipInput = findViewById(R.id.ipInput);
        stationIP = ipInput.getEditText().getText().toString();



    }

    public void startConnection (View view){
        wfo = new WiFiOperations(getApplicationContext(), stationIP);
        wfo.execute();
        layer1.setVisibility(View.INVISIBLE);
        layer2.setVisibility(View.VISIBLE);
        tcpo = new TCPoperations(getApplicationContext(),stationIP,AXSport);
        tcpo.execute();
    }
}



