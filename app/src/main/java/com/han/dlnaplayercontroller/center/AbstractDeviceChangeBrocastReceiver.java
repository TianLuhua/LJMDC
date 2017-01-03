package com.han.dlnaplayercontroller.center;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.han.dlnaplayercontroller.proxy.IDeviceChangeListener;

public abstract class AbstractDeviceChangeBrocastReceiver extends BroadcastReceiver{

	public static final CommonLog log = LogFactory.createLog();
	protected IDeviceChangeListener mListener;
	
	@Override
	public void onReceive(Context context, Intent intent) {
	
	}
	
	public void setListener(IDeviceChangeListener listener){
		mListener  = listener;
	}
	
}
