package com.example.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import indi.fmy.roottouch.touch.RootTouch;

public class MainActivity extends AppCompatActivity {

    private RootTouch rootTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootTouch = new RootTouch(this);
        rootTouch.init();
        View viewById = findViewById(R.id.root);
        viewById.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e(TAG, "onTouch: " );
                return true;

            }
        });
    }

    private static final String TAG = "MainActivity";

    public void onclick(final View view) {

        new Thread(){
            @Override
            public void run() {
              rootTouch.touchSwip(400,400,500,799,1,5000);
            }
        }.start();
//    rootTouch.touchDown(200,200,2);
//    rootTouch.touchUp(2);
    }
}
