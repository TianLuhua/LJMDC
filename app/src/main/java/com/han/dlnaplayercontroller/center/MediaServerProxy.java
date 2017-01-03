package com.han.dlnaplayercontroller.center;

import android.content.Context;
import android.content.Intent;

import com.han.dlnaplayercontroller.server.DMSService;

public class MediaServerProxy implements IBaseEngine {

	private static MediaServerProxy singleton;
	private Context mContext;

	private MediaServerProxy(Context context) {
		this.mContext = context;
	}

	public static MediaServerProxy getSingleton(Context context) {
		if (singleton == null) {
			singleton = new MediaServerProxy(context);
		}
		return singleton;
	}

	@Override
	public boolean startEngine() {
		Intent intent=new Intent(DMSService.START_SERVER_ENGINE);
		intent.setPackage(mContext.getPackageName());
		mContext.startService(intent);
		return true;
	}

	@Override
	public boolean stopEngine() {
		Intent service=new Intent(mContext, DMSService.class);
		mContext.stopService(service);
		return true;
	}

	@Override
	public boolean restartEngine() {
		Intent intent=new Intent(DMSService.RESTART_SERVER_ENGINE);
		intent.setPackage(mContext.getPackageName());
		mContext.startService(intent);
		return true;
	}

}
