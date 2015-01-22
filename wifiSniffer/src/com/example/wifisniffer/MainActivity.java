package com.example.wifisniffer;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView txtAssoc, txtScan;
	private IntentFilter i;
	private BroadcastReceiver receiver;
	private static long SCAN_DELAY = 2000;
	private int apCounter = 0;

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
				StringBuilder sb = new StringBuilder(
						"No         MAC           RSSI (dBM)   F (MHz)   AP_NAME\n");
				sb.append("---------------------------------------------------\n");
				for (ScanResult r : l) {
					sb.append(++apCounter + "] " + r.BSSID + "      " + r.level
							+ "      " + r.frequency + "      " + r.SSID + "\n");
				}

				SpannableString ss1 = new SpannableString(sb.toString());
				ss1.setSpan(
						new ForegroundColorSpan(Color.RED),
						0,
						("No      MAC              RSSI (dBM)   F (MHz)   AP_NAME\n")
								.length(), 0);// set color

				txtScan.setText(ss1);
			}
		};
		Thread wifiScanThread = new Thread() {
			@Override
			public void run() {
				try {
					while (true) {
						sleep(SCAN_DELAY);
						WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
						wm.startScan();
						apCounter =0;
						registerReceiver(receiver, i);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		wifiScanThread.start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		try {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// Your process to do
					WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					if (wm.isWifiEnabled()) {

						WifiInfo info = wm.getConnectionInfo();
						if (info != null) {
							txtAssoc.setText("Associated with "
									+ info.getSSID() + " at "
									+ info.getLinkSpeed()
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

			}, SCAN_DELAY);

			registerReceiver(receiver, i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver(receiver);

	}

}
