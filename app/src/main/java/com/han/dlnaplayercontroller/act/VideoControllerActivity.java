package com.han.dlnaplayercontroller.act;

import com.example.administrator.testdmc.R;
import com.han.dlnaplayercontroller.center.PlayActionManager;
import com.han.dlnaplayercontroller.model.MediaItem;
import com.han.dlnaplayercontroller.proxy.MediaManager;
import com.han.dlnaplayercontroller.utils.DlnaUtils;
import com.han.dlnaplayercontroller.utils.UpnpUtil;

import android.app.Activity;
import android.os.Bundle;

public class VideoControllerActivity extends BaseMediaControllerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        control_bk.setImageResource(R.drawable.video_controller_bk);
    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        items = MediaManager.getInstance().getVideoList();
    }

    @Override
    public void play(MediaItem item) {
        if (item != null && item.getRes() != null) {
            String metaData = DlnaUtils.creatMetaData(item.getTitle(), UpnpUtil.DLNA_OBJECTCLASS_VIDEOID);
            playActionManager.play(item.getRes(), metaData);
            control_title.setText(item.getTitle());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
