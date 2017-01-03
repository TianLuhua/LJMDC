package com.han.dlnaplayercontroller.adapter;

import java.util.List;

import org.cybergarage.upnp.Device;

import com.example.administrator.testdmc.R;
import com.han.dlnaplayercontroller.proxy.AllShareProxy;
import com.han.dlnaplayercontroller.utils.DlnaUtils;
import com.han.dlnaplayercontroller.utils.UpnpUtil;

import android.content.Context;
import android.graphics.Color;

public class DeviceAdapter extends MBaseAdapter {

    private List<Device> devices;
    private AllShareProxy proxy;

    public DeviceAdapter(Context context, List<Device> devices) {
        super(context);
        this.devices = devices;
        proxy = AllShareProxy.getInstance(context);
    }

    public void refreshData(List<Device> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Device item = (Device) getItem(position);
        holder.img.setTag(item);
        holder.img.setImageResource(R.drawable.device_img);
        if (UpnpUtil.isMediaRenderDevice(item)) {
            if (item == proxy.getDMRSelectedDevice()) {
                holder.txt.setTextColor(Color.RED);
            } else {
                holder.txt.setTextColor(Color.GREEN);
            }
        } else if (UpnpUtil.isMediaServerDevice(item)) {
            if (item == proxy.getDMSSelectedDevice()) {
                holder.txt.setTextColor(Color.RED);
            } else {
                holder.txt.setTextColor(Color.GREEN);
            }
        }
        holder.txt.setText(item.getFriendlyName());
    }

    @Override
    public Object getItemData(int position) {
        return devices.get(position);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

}
