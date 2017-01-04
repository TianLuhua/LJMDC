package com.han.dlnaplayercontroller.act;

import android.os.Bundle;
import android.os.HandlerThread;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.han.dlnaplayercontroller.center.PlayActionManager;
import com.han.dlnaplayercontroller.rxbus.RxBus;

/**
 * Created by tlh on 2017/1/3.
 */

public class BaseActivity extends AppCompatActivity {

    protected PlayActionManager playActionManager;
    protected RxBus rxBus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playActionManager = PlayActionManager.getSingleTon();
        rxBus = RxBus.getInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rxBus.unsubscribe();
    }
}
