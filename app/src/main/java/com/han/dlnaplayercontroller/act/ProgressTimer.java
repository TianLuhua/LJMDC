package com.han.dlnaplayercontroller.act;

import android.content.Context;

import com.han.dlnaplayercontroller.utils.AbstractTimer;

/**
 * Created by Administrator on 2017/1/4 0004.
 */

public class ProgressTimer extends AbstractTimer {

    public ProgressTimer(Context context) {
        super(context);
        setTimeInterval(1000);
    }
}
