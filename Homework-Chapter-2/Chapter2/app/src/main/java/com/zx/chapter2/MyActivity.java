package com.zx.chapter2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        String extra = getIntent().getStringExtra("extra");
        TextView textView = findViewById(R.id.text);
        textView.setText(extra);
    }
}
