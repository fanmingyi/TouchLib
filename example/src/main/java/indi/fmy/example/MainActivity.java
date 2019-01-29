package indi.fmy.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import indi.fmy.roottouch.touch.RootTouch;

public class MainActivity extends AppCompatActivity {

    private RootTouch rootTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootTouch = new RootTouch(this);
        rootTouch.init();
    }

    public void onClickShowClick(View view) {
        rootTouch.click(100, 100, 0);
    }

    public void onClickShowSwipe(View view) {
        rootTouch.touchSwip(100, 100, 200, 200, 0, 3000);
    }


    public void onClickShowExit(View view) {
        rootTouch.exit();
    }

    public void onClickShowCustom(View view) throws InterruptedException {

        rootTouch.touchDown(100, 100, 0);
        rootTouch.touchDown(200, 200, 1);
        Thread.sleep(1000);
        rootTouch.touchUp(0);
        Thread.sleep(1000);
        rootTouch.touchUp(1);

    }
}
