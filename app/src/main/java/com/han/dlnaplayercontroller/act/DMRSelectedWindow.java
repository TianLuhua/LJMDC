package com.han.dlnaplayercontroller.act;

import java.util.ArrayList;
import java.util.List;

import org.cybergarage.upnp.Device;

import com.example.administrator.testdmc.R;
import com.han.dlnaplayercontroller.adapter.DeviceAdapter;
import com.han.dlnaplayercontroller.adapter.MBaseAdapter.OnItemClickListener;
import com.han.dlnaplayercontroller.center.PlayActionManager;
import com.han.dlnaplayercontroller.proxy.AllShareProxy;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;

public class DMRSelectedWindow extends PopupWindow implements OnItemClickListener {

    private ListView dmrListView;
    private DeviceAdapter adapter;
    private AllShareProxy playreProxy;

    public DMRSelectedWindow(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(
                R.layout.dmr_selected_window, null);
        setContentView(view);
        playreProxy = AllShareProxy.getInstance(context);
        dmrListView = (ListView) view.findViewById(R.id.dmr_selected_list);
        adapter = new DeviceAdapter(context, new ArrayList<Device>());
        dmrListView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        setWidth(outMetrics.widthPixels);
        setHeight(outMetrics.heightPixels);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);

    }

    public void refreshData(List<Device> devices) {
        if (devices != null) {
            adapter.refreshData(devices);
        }
    }

    public void showPopupWindow(View targetView) {
        if (!this.isShowing()) {
            // int[] location = new int[2];
            // targetView.getLocationOnScreen(location);
            showAsDropDown(targetView);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        View view = v.findViewById(R.id.device_item_img);
        Device device = (Device) view.getTag();
        if (device != null) {
            playreProxy.setDMRSelectedDevice(device);
            PlayActionManager.getSingleTon().setCurrentDevice(device);
        }
        dismiss();
    }
}
