package com.han.dlnaplayercontroller.act;

import java.util.ArrayList;
import java.util.List;

import org.cybergarage.upnp.Device;

import com.example.administrator.testdmc.R;
import com.han.dlnaplayercontroller.adapter.DeviceAdapter;
import com.han.dlnaplayercontroller.adapter.DirectoryAdapter;
import com.han.dlnaplayercontroller.adapter.MBaseAdapter;
import com.han.dlnaplayercontroller.adapter.MBaseAdapter.OnItemClickListener;
import com.han.dlnaplayercontroller.center.DMSDeviceBrocastFactory;
import com.han.dlnaplayercontroller.center.IBaseEngine;
import com.han.dlnaplayercontroller.center.MediaItemFactory;
import com.han.dlnaplayercontroller.center.MediaServerProxy;
import com.han.dlnaplayercontroller.model.MediaItem;
import com.han.dlnaplayercontroller.proxy.AllShareProxy;
import com.han.dlnaplayercontroller.proxy.BrowseDMSProxy;
import com.han.dlnaplayercontroller.proxy.BrowseDMSProxy.BrowseRequestCallback;
import com.han.dlnaplayercontroller.proxy.IDeviceChangeListener;
import com.han.dlnaplayercontroller.proxy.MediaManager;
import com.han.dlnaplayercontroller.utils.CommonUtil;
import com.han.dlnaplayercontroller.utils.DlnaUtils;
import com.han.dlnaplayercontroller.utils.UpnpUtil;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements IDeviceChangeListener {

    public static final String POSITION = "position";
    private MulticastLock mMulticastLock;
    private ListView deviceFileView;
    private IBaseEngine serverProxy;
    private AllShareProxy playreProxy;
    private DeviceAdapter adapter;
    private DMSDeviceBrocastFactory deviceBrocastFactory;
    private DirectoryAdapter directoryAdapter;
    private DirectoryAdapter fileAdapter;
    private DMRSelectedWindow dmrSelectedWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mMulticastLock = CommonUtil.openWifiBrocast(this);
        deviceFileView = (ListView) findViewById(R.id.device_list);
        adapter = new DeviceAdapter(this, new ArrayList<Device>());
        directoryAdapter = new DirectoryAdapter(this,
                new ArrayList<MediaItem>());
        fileAdapter = new DirectoryAdapter(this, new ArrayList<MediaItem>());
        adapter.setOnItemClickListener(deviceItemClickListener);
        directoryAdapter.setOnItemClickListener(directoryItemClickListener);
        fileAdapter.setOnItemClickListener(fileItemClickListener);
        dmrSelectedWindow = new DMRSelectedWindow(this);
        initData();
    }

    private OnItemClickListener fileItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            View view = v.findViewById(R.id.device_item_img);
            MediaItem item = (MediaItem) view.getTag();
            if (item == null) {
                return;
            }
            if (UpnpUtil.isAudioItem(item)
                    && playreProxy.getDMRSelectedDevice() != null) {
                Log.e("info", "isAudioItem");
                Toast.makeText(MainActivity.this, "" + item.getRes(),
                        Toast.LENGTH_SHORT).show();
                goMusicControlActivity(position, item);
            } else if (UpnpUtil.isVideoItem(item)
                    && playreProxy.getDMRSelectedDevice() != null) {
                Log.e("info", "isVideoItem");
                Toast.makeText(MainActivity.this, "" + item.getRes(),
                        Toast.LENGTH_SHORT).show();
                goVideoControlActivity(position, item);
            } else if (UpnpUtil.isPictureItem(item)
                    && playreProxy.getDMRSelectedDevice() != null) {
                Log.e("info", "isPictureItem");
                Toast.makeText(MainActivity.this, "" + item.getRes(),
                        Toast.LENGTH_SHORT).show();
                goPictureControlActivity(position, item);
            } else if (UpnpUtil.isStorageFolder(item)) {
                Log.e("info", "isStorageFolder");
                Toast.makeText(MainActivity.this, "" + item.getStringid(),
                        Toast.LENGTH_SHORT).show();
                requestFile(item);
            }
            if (playreProxy.getDMRSelectedDevice() == null && !UpnpUtil.isStorageFolder(item)) {
                Toast.makeText(MainActivity.this,
                        "请选择播放器!-" + item.getObjectClass(), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };

    private OnItemClickListener deviceItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            View view = v.findViewById(R.id.device_item_img);
            Device device = (Device) view.getTag();
            if (device != null) {
                playreProxy.setDMSSelectedDevice(device);
                Toast.makeText(MainActivity.this,
                        "" + device.getFriendlyName(), Toast.LENGTH_SHORT)
                        .show();
                //请求DMS端的文件列表
                requestDirectory("0");
            }
        }
    };

    private OnItemClickListener directoryItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            View view = v.findViewById(R.id.device_item_img);
            MediaItem item = (MediaItem) view.getTag();
            if (item != null) {
                Toast.makeText(
                        MainActivity.this,
                        "" + item.getObjectClass() + ",Stringid"
                                + item.getStringid(), Toast.LENGTH_SHORT)
                        .show();
                Log.e("info", "" + item.getObjectClass());
                requestFile(item);
            }
        }
    };

    private void initData() {
        serverProxy = MediaServerProxy.getSingleton(getApplicationContext());
        playreProxy = AllShareProxy.getInstance(getApplicationContext());
        // deviceUpdateBrocastFactory=new DeviceUpdateBrocastFactory(this);
        // deviceUpdateBrocastFactory.register(this);
        deviceBrocastFactory = new DMSDeviceBrocastFactory(this);
        deviceBrocastFactory.registerListener(this);
        DlnaUtils.setDevName(this, "LJDlna");
        serverProxy.startEngine();
        playreProxy.startSearch();
    }

    private void goPictureControlActivity(int index, MediaItem item) {
        MediaManager.getInstance().setPictureList(fileAdapter.getDatas());
        Intent intent = new Intent();
        intent.setClass(this, ImageControllerActivity.class);
        intent.putExtra(POSITION, index);
        MediaItemFactory.putItemToIntent(item, intent);
        this.startActivity(intent);
    }

    private void goVideoControlActivity(int index, MediaItem item) {
        MediaManager.getInstance().setVideoList(fileAdapter.getDatas());
        Intent intent = new Intent();
        intent.setClass(this, VideoControllerActivity.class);
        intent.putExtra(POSITION, index);
        MediaItemFactory.putItemToIntent(item, intent);
        this.startActivity(intent);
    }

    private void goMusicControlActivity(int index, MediaItem item) {
        MediaManager.getInstance().setMusicList(fileAdapter.getDatas());
        Intent intent = new Intent();
        intent.setClass(this, MusicControllerActivity.class);
        intent.putExtra(POSITION, index);
        MediaItemFactory.putItemToIntent(item, intent);
        this.startActivity(intent);
    }

    protected void requestDirectory(String id) {
        //directoryCallback为跟新List的Callback,每次点击Item将结果回调回来
        BrowseDMSProxy.syncGetDirectory(this, id, directoryCallback);
    }

    private void requestFile(MediaItem item) {
        BrowseDMSProxy.syncGetItems(this, item.getStringid(), fileCallback);
    }

    private BrowseRequestCallback fileCallback = new BrowseRequestCallback() {
        @Override
        public void onGetItems(final List<MediaItem> list) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (list == null) {
                        Toast.makeText(MainActivity.this, "没有获取到数据!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    deviceFileView.setAdapter(fileAdapter);
                    curradaAdapter = fileAdapter;
                    fileAdapter.refreshData(list);
                }
            });
        }
    };

    private BrowseRequestCallback directoryCallback = new BrowseRequestCallback() {
        @Override
        public void onGetItems(final List<MediaItem> list) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (list != null) {
                        deviceFileView.setAdapter(directoryAdapter);
                        curradaAdapter = directoryAdapter;
                        directoryAdapter.refreshData(list);
                    }
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void refresh(View v) {
        serverProxy.restartEngine();
        playreProxy.resetSearch();
    }

    public void player(View v) {
        dmrSelectedWindow.showPopupWindow(v);
    }

    public void exit(View v) {
        serverProxy.stopEngine();
        playreProxy.exitSearch();
        finish();
    }

    private MBaseAdapter curradaAdapter;

    public void fileBack(View v) {
        if (curradaAdapter == fileAdapter) {
            curradaAdapter = directoryAdapter;
            deviceFileView.setAdapter(directoryAdapter);
        } else if (curradaAdapter == directoryAdapter) {
            curradaAdapter = adapter;
            deviceFileView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mMulticastLock.release();
        mMulticastLock = null;
        deviceBrocastFactory.unRegisterListener();
        super.onDestroy();
    }

    private void updateDMSDeviceList() {
        deviceFileView.setAdapter(adapter);
        curradaAdapter = adapter;
        //获取DMS设备
        List<Device> devices = playreProxy.getDMSDeviceList();
        adapter.refreshData(devices);
    }

    private void updateDMRDeviceList() {
        //获取DMR设备
        List<Device> devices = playreProxy.getDMRDeviceList();
        dmrSelectedWindow.refreshData(devices);
    }

    // ! 注:此处加载dms与dmr时间不同,因此应该分开
    @Override
    public void onDeviceChange(boolean isSelDeviceChange) {
        updateDMSDeviceList();
        updateDMRDeviceList();
    }
}
