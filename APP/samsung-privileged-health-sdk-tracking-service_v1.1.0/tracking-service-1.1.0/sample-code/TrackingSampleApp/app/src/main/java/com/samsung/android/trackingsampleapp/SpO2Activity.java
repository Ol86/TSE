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
import com.samsung.android.service.health.tracking.data.ValueKey;
import com.samsung.android.trackingsampleapp.databinding.ActivitySpo2Binding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpO2Activity extends FragmentActivity {

    private final String TAG = SpO2Activity.class.getSimpleName();
    private ActivitySpo2Binding binding;
    private HealthTrackingService healthTrackingService = null;
    @NotNull
    private final String[] permissions = {"android.permission.BODY_SENSORS"};
    private final int REQUEST_ACCOUNT_PERMISSION = 100;
    private boolean isHandlerRunning;
    private final Handler handler = new Handler(Looper.myLooper());
    private HealthTracker spo2Tracker = null;
    private int prevStatus = -100;

    private final ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void onConnectionSuccess() {
            Toast.makeText(
                    getApplicationContext(),"Connected to HSP",Toast.LENGTH_SHORT
            ).show();
            binding.progressBar.setVisibility(View.INVISIBLE);
            try {
                spo2Tracker = healthTrackingService.getHealthTracker(HealthTrackerType.SPO2);
            } catch (final IllegalArgumentException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show()
                );
                finish();
            }

            binding.spo2StartButton.setOnClickListener(view -> {
                Log.i(TAG, " setEventListener called ");
                if(!isHandlerRunning) {
                    binding.spo2StartButton.setEnabled(false);
                    prevStatus = -100;
                    binding.spo2State.setText("SpO2 measuring");
                    handler.post(() -> {
                        spo2Tracker.setEventListener(trackerEventListener);
                        isHandlerRunning = true;
                    });
                }
            });
            binding.spo2StopButton.setOnClickListener(view -> {
                binding.spo2StartButton.setEnabled(true);
                Log.i(TAG, " unsetEventListener called ");
                if (spo2Tracker != null) {
                    spo2Tracker.unsetEventListener();
                }
                handler.removeCallbacksAndMessages(null);
                isHandlerRunning = false;
                binding.spo2State.setText("Measuring stopped");
            });
        }

        @Override
        public void onConnectionEnded() {

        }

        @Override
        public void onConnectionFailed(HealthTrackerException e) {
            if(e.hasResolution()) {
                e.resolve(SpO2Activity.this);
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
                    int status = dataPoint.getValue(ValueKey.SpO2Set.STATUS);
                    if (prevStatus != status) {
                        prevStatus = status;
                        Log.i(TAG, "Status : " + status);
                        runOnUiThread(() -> {
                            if (status == 2) {
                                binding.spo2StatusValue.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                                if(spo2Tracker != null) {
                                    spo2Tracker.unsetEventListener();
                                }
                                binding.spo2StartButton.setEnabled(true);
                                handler.removeCallbacksAndMessages(null);
                                isHandlerRunning = false;
                                binding.spo2State.setText("SpO2 measured");
                            }
                            else if (status == 0) {
                                binding.spo2StatusValue.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            }
                            else if (status == -4){
                                Toast.makeText(getApplicationContext(), "Moving : " + status, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Low Signal : " + status, Toast.LENGTH_SHORT).show();
                            }
                            binding.spo2StatusValue.setText(String.valueOf(status));
                            binding.spo2Value.setText(String.valueOf(dataPoint.getValue(ValueKey.SpO2Set.SPO2)));
                            binding.spo2HrValue.setText(String.valueOf(dataPoint.getValue(ValueKey.SpO2Set.HEART_RATE)));
                        });
                    }
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
        if (PermissionActivity.checkPermission(this, this.permissions)) {
            Log.i(TAG, "onCreate Permission granted");
            setUp();
        } else {
            Log.i(TAG, "onCreate Permission not granted");
            PermissionActivity.showPermissionPrompt(this, this.REQUEST_ACCOUNT_PERMISSION, this.permissions);
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_spo2);
        healthTrackingService = new HealthTrackingService(connectionListener, getApplicationContext());
        healthTrackingService.connectService();
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(spo2Tracker != null) {
            spo2Tracker.unsetEventListener();
        }
        handler.removeCallbacksAndMessages(null);
        isHandlerRunning = false;
        if(healthTrackingService != null) {
            healthTrackingService.disconnectService();
        }
    }
}
