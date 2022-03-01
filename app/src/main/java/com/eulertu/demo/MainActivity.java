package com.eulertu.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.eulertu.opensdk.EulertuManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EulertuManager.getInstance().setContext(this);
        findViewById(R.id.showFloat).setOnClickListener(view -> EulertuManager.getInstance().showFloat());
    }
}