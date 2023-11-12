/*
 * Copyright (C) 2021 Samsung Electronics Co., Ltd. All rights reserved.
 *
 * Mobile Communication Division,
 * IT & Mobile Communications, Samsung Electronics Co., Ltd.
 *
 * This software and its documentation are confidential and proprietary
 * information of Samsung Electronics Co., Ltd.  No part of the software and
 * documents may be copied, reproduced, transmitted, translated, or reduced to
 * any electronic medium or machine-readable form without the prior written
 * consent of Samsung Electronics.
 *
 * Samsung Electronics makes no representations with respect to the contents,
 * and assumes no responsibility for any errors that might appear in the
 * software and documents. This publication and the contents hereof are subject
 * to change without notice.
 */
package com.samsung.android.trackingsampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

	private final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i(TAG, "onCreate");
		String[] values = new String[]{"HeartRate", "PPG Green", "PPG Red", "PPG Ir", "ECG", "Accelerometer", "BIA", "SweatLoss", "SpO2", "Settings"};
		ListView mListView = findViewById(R.id.sensors_list_view);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener( (__,___,position,____) -> {
			switch (position) {
				case 0: {
					startActivity(new Intent(this, HeartRateActivity.class));
					break;
				}
				case 1: {
					startActivity(new Intent(this, PpgGreenActivity.class));
					break;
				}
				case 2: {
					startActivity(new Intent(this, PpgRedActivity.class));
					break;
				}
				case 3: {
					startActivity(new Intent(this, PpgIrActivity.class));
					break;
				}
				case 4: {
					startActivity(new Intent(this, EcgActivity.class));
					break;
				}
				case 5: {
					startActivity(new Intent(this, AccelerometerActivity.class));
					break;
				}
				case 6: {
					startActivity(new Intent(this, BiaActivity.class));
					break;
				}
				case 7: {
					startActivity(new Intent(this, SweatLossActivity.class));
					break;
				}
				case 8: {
					startActivity(new Intent(this, SpO2Activity.class));
					break;
				}
				case 9: {
					startActivity(new Intent(this, SettingsActivity.class));
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
	}
}
