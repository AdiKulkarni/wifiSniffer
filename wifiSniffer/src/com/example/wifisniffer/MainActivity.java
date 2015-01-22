package com.example.wifisniffer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView txtAssoc, txtScan;
	private IntentFilter i;
	private BroadcastReceiver receiver;
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtAssoc = (TextView) findViewById(R.id.txtAssoc);
		txtScan = (TextView) findViewById(R.id.txtScan);
		i = new IntentFilter();
		i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

		receiver = new BroadcastReceiver() {
			public void onReceive(Context c, Intent i) {
				WifiManager w = (WifiManager) c
						.getSystemService(Context.WIFI_SERVICE);
				List<ScanResult> l = w.getScanResults();
				StringBuilder sb = new StringBuilder("Scan Results:\n");
				sb.append("-----------------------\n");
				for (ScanResult r : l) {
					sb.append(r.SSID + " " + r.level + " dBM\n");
				}
				txtScan.setText(sb.toString());
			}
		};

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				if (wm.isWifiEnabled()) {
					WifiInfo info = wm.getConnectionInfo();
					if (info != null) {
						txtAssoc.setText("Associated with " + info.getSSID()
								+ "\nat " + info.getLinkSpeed()
								+ WifiInfo.LINK_SPEED_UNITS + " ("
								+ info.getRssi() + " dBM)");
					} else {
						txtAssoc.setText("Not currently associated.");
					}
					wm.startScan();
				} else {
					txtAssoc.setText("WIFI is disabled.");
				}
			}
		}, 0, 2000);
		registerReceiver(receiver, i);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onStop();
		timer.cancel();
		unregisterReceiver(receiver);

	}

}
