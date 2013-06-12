package edu.stu.wifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.test.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 
 * 
 * 
 * 2013/06/13 第一次上傳Github，訊號閥值有點太高了，地點鑑別性不夠高，
 * 另外掃描速度還要調高一點，已改為List方式儲存記憶地點RSSI
 * 
 * 2013/06/11 用陣列做記憶地點RSSI，很容易爆ARRAY，待改為List方式
 * 2013/06/10 牛刀小試，可以scan wifi訊號了，尚待處理判斷記憶地點的方法
 */

public class MainActivity extends Activity implements OnClickListener {
	WifiManager wifi;
	ListView lv;
	TextView TextAPSize;
	TextView textStatus;
	Button buttonScan;
	Button buttonCal;
	Button buttonPos;
	int size = 0;
	List<ScanResult> results;
	List<List<String>> lLocateTEMP = new ArrayList<List<String>>();
	List<List<String>> lLocateA = new ArrayList<List<String>>();
	List<List<String>> lLocateB = new ArrayList<List<String>>();

	List<String> apSSID = new ArrayList<String>();
	List<String> apLevel = new ArrayList<String>();

	int saveFlag = 0;

	String ITEM_KEY = "key";
	ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
	SimpleAdapter adapter;

	/* Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.fl);

		// 使用LayoutInflater取得tabhost
		View view = LayoutInflater.from(getBaseContext()).inflate(
				R.layout.tabhost_template, null);

		TabHost tabhost = (TabHost) view.findViewById(R.id.tabhost);
		tabhost.setup();

		TabHost.TabSpec tabcontent1 = tabhost.newTabSpec("tab01");
		tabcontent1.setIndicator("Scan");
		tabcontent1.setContent(R.id.tab1);

		TabHost.TabSpec tabcontent2 = tabhost.newTabSpec("tab02");
		tabcontent2.setIndicator("Map");
		tabcontent2.setContent(R.id.tab2);

		tabhost.addTab(tabcontent1);
		tabhost.addTab(tabcontent2);

		FrameLayout baseFL = (FrameLayout) findViewById(R.id.fl);
		baseFL.addView(tabhost);

		textStatus = (TextView) findViewById(R.id.textView1);
		TextAPSize = (TextView) findViewById(R.id.textView2);
		buttonScan = (Button) findViewById(R.id.buttonScan);
		buttonCal = (Button) findViewById(R.id.buttonCal);
		buttonPos = (Button) findViewById(R.id.buttonPos);
		buttonScan.setOnClickListener(this);
		buttonCal.setOnClickListener(this);
		buttonPos.setOnClickListener(this);

		lv = (ListView) findViewById(R.id.list);

		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled() == false) {
			Toast.makeText(getApplicationContext(), "wifi現在為關閉狀態，重啟動中",
					Toast.LENGTH_LONG).show();
			wifi.setWifiEnabled(true);
		}
		this.adapter = new SimpleAdapter(MainActivity.this, arraylist,
				R.layout.row, new String[] { ITEM_KEY },
				new int[] { R.id.list_value });

		lv.setAdapter(this.adapter);

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent intent) {
				results = wifi.getScanResults();
				size = results.size();
			}
		}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.buttonScan:
			buttonScan.setEnabled(false);
			lLocateTEMP.clear();

			apSSID.clear();
			apLevel.clear();

			arraylist.clear();
			wifi.startScan();

			// Toast.makeText(this, "Scanning..." + size,
			// Toast.LENGTH_SHORT).show();
			TextAPSize.setText(String.valueOf(size));

			try {
				size = size - 1;

				while (size >= 0) {
					if (results.get(size).level > (-80)) {
						HashMap<String, String> item = new HashMap<String, String>();
						item.put(ITEM_KEY, results.get(size).SSID + "  "
								+ results.get(size).level);

						apSSID.add(results.get(size).SSID);
						apLevel.add(String.valueOf(results.get(size).level));

						arraylist.add(item);
					}
					size--;
					adapter.notifyDataSetChanged();

				}

				lLocateTEMP.add(apSSID);
				lLocateTEMP.add(apLevel);

			} catch (Exception e) {
				Log.d("OOPS", e.toString());
			}
			buttonScan.setEnabled(true);
			break;

		case R.id.buttonCal:

			saveFlag++;
			if (saveFlag == 1) {
				lLocateA = new ArrayList<List<String>>();

				for (List<String> lAP : lLocateTEMP) {
					lLocateA.add(new ArrayList<String>(lAP));
				}
				Toast.makeText(this, "地點A已儲存", Toast.LENGTH_SHORT).show();

			} else if (saveFlag == 2) {
				lLocateB = new ArrayList<List<String>>();

				for (List<String> lAP : lLocateTEMP) {
					lLocateB.add(new ArrayList<String>(lAP));
				}
				Toast.makeText(this, "地點B已儲存", Toast.LENGTH_SHORT).show();
				saveFlag = 0;
			}

			break;

		case R.id.buttonPos:

			textStatus.setText("定位中");
			lLocateTEMP.clear();

			apSSID.clear();
			apLevel.clear();

			arraylist.clear();
			wifi.startScan();

			// Toast.makeText(this, "Pos..." + size, Toast.LENGTH_SHORT).show();
			TextAPSize.setText(String.valueOf(size));

			try {
				int i = 0;
				size = size - 1;

				if (size < 0) {
					int a = 1;
					a++;
				}

				while (size >= 0) {
					if (results.get(size).level > (-80)) {

						HashMap<String, String> item = new HashMap<String, String>();
						item.put(ITEM_KEY, results.get(size).SSID + "  "
								+ results.get(size).level);

						apSSID.add(results.get(size).SSID);
						apLevel.add(String.valueOf(results.get(size).level));

						arraylist.add(item);
					}

					size--;
					adapter.notifyDataSetChanged();

				}

				lLocateTEMP.add(apSSID);
				lLocateTEMP.add(apLevel);

				if (!(lLocateA.size() == 0)
						&& !(lLocateTEMP.get(0).size() == 0)) {
					double dfflevel = 0;
					int check = 0;

					for (int k = 0; k < lLocateTEMP.get(0).size(); k++) {

						for (int j = 0; j < lLocateA.get(0).size(); j++) {
							if (lLocateTEMP.get(0).get(k)
									.equals(lLocateA.get(0).get(j))) {

								dfflevel = Math.abs(Math.abs(Double
										.valueOf(lLocateTEMP.get(1).get(k)))
										- Math.abs(Double.valueOf(lLocateA.get(
												1).get(j))));

								if (dfflevel <= 5) {
									check++;
								}

							}
						}

					}

					if (check >= (lLocateA.get(1).size() / 2)) {
						textStatus.setText("POS A");
						Toast.makeText(this, "Pos...A!!", Toast.LENGTH_SHORT)
								.show();

					}

				}

				else if (!(lLocateB.size() == 0)
						&& !(lLocateTEMP.get(0).size() == 0)) {
					double dfflevel = 0;
					int check = 0;

					for (int k = 0; k < lLocateTEMP.get(0).size(); k++) {

						for (int j = 0; j < lLocateB.get(0).size(); j++) {
							if (lLocateTEMP.get(0).get(k)
									.equals(lLocateB.get(0).get(j))) {

								dfflevel = Math.abs(Math.abs(Double
										.valueOf(lLocateTEMP.get(1).get(k)))
										- Math.abs(Double.valueOf(lLocateB.get(
												1).get(j))));

								if (dfflevel <= 5) {
									check++;
								}

							}
						}

					}

					if (check >= (lLocateB.get(1).size() / 2)) {
						textStatus.setText("POS A");
						Toast.makeText(this, "Pos...A!!", Toast.LENGTH_SHORT)
								.show();

					}

				} else {
					textStatus.setText("Unknow pos");

				}

			} catch (Exception e) {
				Log.d("TAG", e.toString());
				textStatus.setText(e.toString());
			}

			break;

		}

	}
}