package com.sdl.hellosdlandroid;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.smartdevicelink.transport.TransportConstants;
import com.smartdevicelink.transport.enums.TransportType;
import com.smartdevicelink.util.DebugTool;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
	private SdlReceiver mReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DebugTool.enableDebugTool();
		//If we are connected to a module we want to start our SdlService
		if(BuildConfig.TRANSPORT.equals("MBT")) {
			SdlReceiver.queryForConnectedService(this, TransportType.MULTIPLEX);
		}else if(BuildConfig.TRANSPORT.equals("TCP") || BuildConfig.TRANSPORT.equals("LBT")) {
			Intent proxyIntent = new Intent(this, SdlService.class);
			startService(proxyIntent);
		}
		registerReceiver();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_restart) {
			SdlApplication app = (SdlApplication)getApplication();
			app.restartSDL();
		} else if (id == R.id.action_forceaoa) {
			SdlApplication.forceAoa = true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver();
		super.onDestroy();
	}

	private void registerReceiver() {
		if (mReceiver != null) {
			//return;
		}
		mReceiver = new SdlReceiver();
		registerReceiver(mReceiver, new IntentFilter("android.bluetooth.device.action.ACL_CONNECTED"));
		registerReceiver(mReceiver, new IntentFilter("android.bluetooth.device.action.ACL_DISCONNECTED"));
		registerReceiver(mReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
		registerReceiver(mReceiver, new IntentFilter(TransportConstants.START_ROUTER_SERVICE_ACTION));
		registerReceiver(mReceiver, new IntentFilter(TransportConstants.AOA_ROUTER_OPEN_ACCESSORY));
	}

	private void unregisterReceiver() {
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}
}
