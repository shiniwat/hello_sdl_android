package com.sdl.hellosdlandroid;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.smartdevicelink.transport.SdlBroadcastReceiver;
import com.smartdevicelink.transport.SdlRouterBase;
import com.smartdevicelink.transport.SdlRouterService;
import com.smartdevicelink.transport.TransportConstants;
import com.smartdevicelink.transport.USBTransport;
import com.smartdevicelink.transport.enums.TransportType;

public class SdlReceiver  extends SdlBroadcastReceiver {		
	private static final String TAG = "SdlBroadcastReciever";
	public static final String RECONNECT_LANG_CHANGE = "RECONNECT_LANG_CHANGE";

	@Override
	public void onSdlEnabled(Context context, Intent intent) {
		Log.d(TAG, "SDL Enabled");
		intent.setClass(context, SdlService.class);
		context.startService(intent);
	}

	@Override
	public Class<? extends SdlRouterService> defineLocalSdlRouterClass() {
		return com.sdl.hellosdlandroid.SdlRouterService.class;
	}

	@Override
	public Class<? extends SdlRouterBase> defineLocalSdlAoaRouterClass() {
		return com.sdl.hellosdlandroid.SdlAoaRouterService.class;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent); // Required if overriding this method

		if (intent != null) {
			String action = intent.getAction();
			if (action != null){
				if(action.equalsIgnoreCase(TransportConstants.START_ROUTER_SERVICE_ACTION)) {
					if (intent.getBooleanExtra(RECONNECT_LANG_CHANGE, false)) {
						onSdlEnabled(context, intent);
					}
				} else if (action.equalsIgnoreCase(USBTransport.ACTION_USB_ACCESSORY_ATTACHED)) {
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						SdlReceiver.queryForConnectedService(context, TransportType.MULTIPLEX_AOA);
					} else {
						// @TODO: need to figure out the better way when we don't have USB permission granted.
						// Anyway, we still need service to access the UsbAccessory.
						Log.d(TAG, "We have not USB permission granted; but launch service anyway");
						SdlReceiver.queryForConnectedService(context, TransportType.MULTIPLEX_AOA);
					}
				} else if (action.equalsIgnoreCase(TransportConstants.AOA_ROUTER_OPEN_ACCESSORY)) {
					Intent proxyIntent = new Intent(context, SdlService.class);
					proxyIntent.setAction(TransportConstants.AOA_ROUTER_OPEN_ACCESSORY);
					context.startService(proxyIntent);
				}
			}
		}
	}
}