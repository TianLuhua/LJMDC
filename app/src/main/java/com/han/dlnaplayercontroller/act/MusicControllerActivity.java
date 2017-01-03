package com.han.dlnaplayercontroller.act;

import com.han.dlnaplayercontroller.center.PlayActionManager;
import com.han.dlnaplayercontroller.model.MediaItem;
import com.han.dlnaplayercontroller.proxy.MediaManager;
import com.han.dlnaplayercontroller.utils.DlnaUtils;
import com.han.dlnaplayercontroller.utils.UpnpUtil;

public class MusicControllerActivity extends BaseMediaControllerActivity {

    @Override
    protected void initIntentData() {
        super.initIntentData();
        items = MediaManager.getInstance().getMusicList();
    }

    public void play(MediaItem item) {
        if (item != null && item.getRes() != null) {
            playActionManager.stop();
            String metaData = DlnaUtils.creatMetaData(item.getTitle(), UpnpUtil.DLNA_OBJECTCLASS_MUSICID);
            playActionManager.play(item.getRes(), metaData);
            control_title.setText(item.getTitle());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
