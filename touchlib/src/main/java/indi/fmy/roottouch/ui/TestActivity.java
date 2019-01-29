package indi.fmy.roottouch.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import indi.fmy.roottouch.R;
import indi.fmy.roottouch.touch.RootTouch;

public class TestActivity extends AppCompatActivity {

    private RootTouch rootTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        RootTouch  rootTouch = new RootTouch(this);
        boolean init = rootTouch.init();
    }

    public void onClickDown(View view) throws InterruptedException {
        new Thread(){
            @Override
            public void run() {
                super.run();
                rootTouch.touchSwip(100,300,500,300,0,5000);

            }
        }.start();
    }

    public void onClickUp(View view) throws InterruptedException {
            rootTouch.exit();
    }

    public void onClickMove(View view) throws InterruptedException {
        rootTouch.touchDown(300, 400, 1);
        Thread.sleep(1000);
        rootTouch.touchDown(300, 600, 2);

    }
}
