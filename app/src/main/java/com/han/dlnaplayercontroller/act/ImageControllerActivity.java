package com.han.dlnaplayercontroller.act;

import java.util.List;

import com.example.administrator.testdmc.R;
import com.han.dlnaplayercontroller.center.MediaItemFactory;
import com.han.dlnaplayercontroller.center.PlayActionManager;
import com.han.dlnaplayercontroller.model.MediaItem;
import com.han.dlnaplayercontroller.proxy.AllShareProxy;
import com.han.dlnaplayercontroller.proxy.MediaManager;
import com.han.dlnaplayercontroller.utils.DlnaUtils;
import com.han.dlnaplayercontroller.utils.UpnpUtil;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageControllerActivity extends BaseActivity {

    public ImageView previewImg;
    public TextView imgTitle;
    private List<MediaItem> items;
    private int currPosition;
    private AllShareProxy proxy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_controller);
        proxy = AllShareProxy.getInstance(this);
        previewImg = (ImageView) findViewById(R.id.img_control_preview_img);
        imgTitle = (TextView) findViewById(R.id.img_control_title);
        initIntentData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        MediaItem item = MediaItemFactory.getItemFromIntent(intent);
        play(item);
        items = MediaManager.getInstance().getPictureList();
        currPosition = intent.getIntExtra(MainActivity.POSITION, 0);
    }

    private void play(MediaItem item) {
        if (item != null && item.getRes() != null) {
            String metaData = DlnaUtils.creatMetaData(item.getTitle(), UpnpUtil.DLNA_OBJECTCLASS_PHOTOID);
            playActionManager.play(item.getRes(), metaData);
            Picasso.with(this).load(item.getRes())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .fit().into(previewImg);
            imgTitle.setText(item.getTitle());
        }
    }

    public void goLeft(View v) {
        currPosition -= 1;
        if (currPosition >= 0) {
            MediaItem item = items.get(currPosition);
            play(item);
        } else {
            currPosition = 0;
        }
    }

    public void goRight(View v) {
        currPosition += 1;
        if (currPosition < items.size()) {
            MediaItem item = items.get(currPosition);
            play(item);
        } else {
            currPosition = items.size() - 1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
