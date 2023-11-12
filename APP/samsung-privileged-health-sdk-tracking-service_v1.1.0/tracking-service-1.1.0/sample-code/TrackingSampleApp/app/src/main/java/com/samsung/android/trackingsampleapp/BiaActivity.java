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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.samsung.android.service.health.tracking.ConnectionListener;
import com.samsung.android.service.health.tracking.HealthTracker;
import com.samsung.android.service.health.tracking.HealthTracker.TrackerEventListener;
import com.samsung.android.service.health.tracking.HealthTracker.TrackerError;
import com.samsung.android.service.health.tracking.HealthTrackerException;
import com.samsung.android.service.health.tracking.HealthTrackingService;
import com.samsung.android.service.health.tracking.data.DataPoint;
import com.samsung.android.service.health.tracking.data.HealthTrackerType;
import com.samsung.android.service.health.tracking.data.TrackerUserProfile;
import com.samsung.android.service.health.tracking.data.ValueKey;
import com.samsung.android.trackingsampleapp.databinding.ActivityBiaBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BiaActivity extends FragmentActivity {

	private final String TAG = BiaActivity.class.getSimpleName();
	private ActivityBiaBinding binding;
	private HealthTrackingService healthTrackingService = null;
	@NotNull
	private final String[] permissions = {"android.permission.BODY_SENSORS"};
	private final int REQUEST_ACCOUNT_PERMISSION = 100;
	private boolean isHandlerRunning;
	private final Handler handler = new Handler(Looper.myLooper());
	private HealthTracker biaTracker = null;
	private TrackerUserProfile profile;
	private final float HEIGHT = 162f;
	private final float WEIGHT = 65f;
	private final int AGE = 35;
	private final int MALE = 1;

	private final ConnectionListener connectionListener = new ConnectionListener() {
		@Override
		public void onConnectionSuccess() {
			Toast.makeText(
					getApplicationContext(),"Connected to HSP",Toast.LENGTH_SHORT
			).show();
			binding.progressBar.setVisibility(View.INVISIBLE);
			profile = new TrackerUserProfile.Builder()
					.setHeight(HEIGHT)
					.setWeight(WEIGHT)
					.setGender(MALE)
					.setAge(AGE)
					.build();
			try {
				biaTracker = healthTrackingService.getHealthTracker(HealthTrackerType.BIA, profile);
			} catch (final IllegalArgumentException e) {
				runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show()
				);
				finish();
			}

			binding.biaStartButton.setOnClickListener(view -> {
				Log.i(TAG, " setEventListener called ");
				if(!isHandlerRunning) {
					handler.post(() -> {
						biaTracker.setEventListener(trackerEventListener);
						isHandlerRunning = true;
					});
				}
			});
			binding.biaStopButton.setOnClickListener(view -> {
				Log.i(TAG, " unsetEventListener called ");
				if (biaTracker != null) {
					biaTracker.unsetEventListener();
				}
				handler.removeCallbacksAndMessages(null);
				isHandlerRunning = false;
			});
		}

		@Override
		public void onConnectionEnded() {

		}

		@Override
		public void onConnectionFailed(HealthTrackerException e) {
			if(e.hasResolution()) {
				e.resolve(BiaActivity.this);
			}
			runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Unable to connect to HSP", Toast.LENGTH_LONG).show()
			);
			finish();
		}
	};

	private final TrackerEventListener trackerEventListener = new TrackerEventListener() {
		@Override
		public void onDataReceived(@NonNull List<DataPoint> list) {
			if (list.size() != 0) {
				Log.i(TAG, "List Size : "+list.size());
				for(DataPoint dataPoint : list) {
					Log.i(TAG, "Timestamp : "+dataPoint.getTimestamp());
					int status = dataPoint.getValue(ValueKey.BiaSet.STATUS);
					runOnUiThread(() -> {
						if(status == 0)
							binding.biaStatusValue.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
						else
							binding.biaStatusValue.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
						binding.biaStatusValue.setText(String.valueOf(status));
						binding.biaBodyFatRatioValue.setText(String.valueOf(dataPoint.getValue(ValueKey.BiaSet.BODY_FAT_RATIO)));
						binding.biaBodyFatMassValue.setText(String.valueOf(dataPoint.getValue(ValueKey.BiaSet.BODY_FAT_MASS)));
						binding.biaTotalBodyWaterValue.setText(String.valueOf(dataPoint.getValue(ValueKey.BiaSet.TOTAL_BODY_WATER)));
						binding.biaSkeletalMuscleRatioValue.setText(String.valueOf(dataPoint.getValue(ValueKey.BiaSet.SKELETAL_MUSCLE_RATIO)));
						binding.biaSkeletalMuscleMassValue.setText(String.valueOf(dataPoint.getValue(ValueKey.BiaSet.SKELETAL_MUSCLE_MASS)));
						binding.biaBasalMetabolicRateValue.setText(String.valueOf(dataPoint.getValue(ValueKey.BiaSet.BASAL_METABOLIC_RATE)));
						binding.biaFatFreeRatioValue.setText(String.valueOf(dataPoint.getValue(ValueKey.BiaSet.FAT_FREE_RATIO)));
						binding.biaFatFreeMassValue.setText(String.valueOf(dataPoint.getValue(ValueKey.BiaSet.FAT_FREE_MASS)));
					});
				}
			} else {
				Log.i(TAG, "onDataReceived List is zero");
			}
		}

		@Override
		public void onFlushCompleted() {
			Log.i(TAG, " onFlushCompleted called");
		}

		@Override
		public void onError(TrackerError trackerError) {
			Log.i(TAG, " onError called");
			if (trackerError == TrackerError.PERMISSION_ERROR) {
				runOnUiThread(() -> Toast.makeText(getApplicationContext(),
						"Permissions Check Failed", Toast.LENGTH_SHORT).show());
			}
			if (trackerError == HealthTracker.TrackerError.SDK_POLICY_ERROR) {
				runOnUiThread(() -> Toast.makeText(getApplicationContext(),
						"SDK Policy denied", Toast.LENGTH_SHORT).show());
			}
			isHandlerRunning = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (PermissionActivity.checkPermission((Context)this, this.permissions)) {
			Log.i(TAG, "onCreate Permission granted");
			setUp();
		} else {
			Log.i(TAG, "onCreate Permission not granted");
			PermissionActivity.showPermissionPrompt((Activity)this, this.REQUEST_ACCOUNT_PERMISSION, this.permissions);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "onActivityResult requestCode = " + requestCode + " resultCode = " + resultCode);
		if (requestCode == this.REQUEST_ACCOUNT_PERMISSION) {
			if (resultCode == -1) {
				setUp();
			} else {
				finish();
			}
		}
	}

	public final void setUp() {
		binding = DataBindingUtil.setContentView(this, R.layout.activity_bia);
		healthTrackingService = new HealthTrackingService(connectionListener, getApplicationContext());
		healthTrackingService.connectService();
		binding.progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(biaTracker != null) {
			biaTracker.unsetEventListener();
		}
		handler.removeCallbacksAndMessages(null);
		isHandlerRunning = false;
		if(healthTrackingService != null) {
			healthTrackingService.disconnectService();
		}
	}
}
