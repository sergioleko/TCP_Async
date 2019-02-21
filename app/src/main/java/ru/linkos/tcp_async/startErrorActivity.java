package ru.linkos.tcp_async;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class startErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_error);
        Intent intent = getIntent();
        String error = intent.getStringExtra(WiFiOperations.EXTRA_MESSAGE_ERR_NO);
        TextView errorTW = findViewById(R.id.textViewError);
        errorTW.setText(error);
    }

    public void toStart(View view){
        Intent restartIntent = new Intent(this, start_window.class);
        startActivity(restartIntent);
    }
}
