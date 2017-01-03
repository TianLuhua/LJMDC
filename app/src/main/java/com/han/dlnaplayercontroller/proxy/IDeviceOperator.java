package com.han.dlnaplayercontroller.proxy;

import java.util.List;

import org.cybergarage.upnp.Device;

public interface IDeviceOperator {

	public void  addDevice(Device d);
	public void removeDevice(Device d);
	public void clearDevice();
	
	public static interface IDMSDeviceOperator{
		public  List<Device> getDMSDeviceList();
		public Device getDMSSelectedDevice();
		public void setDMSSelectedDevice(Device selectedDevice);

	}
	
}
