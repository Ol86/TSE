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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.samsung.android.service.health.tracking.ConnectionListener;
import com.samsung.android.service.health.tracking.HealthTracker;
import com.samsung.android.service.health.tracking.HealthTrackerException;
import com.samsung.android.service.health.tracking.HealthTrackingService;
import com.samsung.android.service.health.tracking.data.DataPoint;
import com.samsung.android.service.health.tracking.data.DataType;
import com.samsung.android.service.health.tracking.data.ExerciseState;
import com.samsung.android.service.health.tracking.data.HealthTrackerType;
import com.samsung.android.service.health.tracking.data.TrackerUserProfile;
import com.samsung.android.service.health.tracking.data.ValueKey;
import com.samsung.android.trackingsampleapp.databinding.ActivitySweatLossBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SweatLossActivity extends FragmentActivity implements WHSManager.SPMCallback {

    private final String TAG = SweatLossActivity.class.getSimpleName();
    private ActivitySweatLossBinding binding;
    private HealthTrackingService healthTrackingService = null;
    @NotNull
    private final String[] permissions = {"android.permission.BODY_SENSORS", "android.permission.ACTIVITY_RECOGNITION"};
    private final int REQUEST_ACCOUNT_PERMISSION = 100;
    private boolean isHandlerRunning;
    private final Handler handler = new Handler(Looper.myLooper());
    private HealthTracker sweatLossTracker = null;
    private TrackerUserProfile profile;
    private float height = 170f;
    private float weight = 65f;
    private int age = 21;
    private int gender = 1;
    private int seconds = 0;
    private double distance = 0.0;
    private boolean timerRunning;
    private static WHSManager wearHealthServiceManager;
    private SharedPreferences sharedPreferences;

    private final ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void onConnectionSuccess() {
            Toast.makeText(
                    getApplicationContext(), "Connected to HSP", Toast.LENGTH_SHORT
            ).show();
            try {
                Log.i(TAG, "Height : " + profile.getHeight() + " Weight : " + profile.getWeight()
                + " Age : " + profile.getAge() + " Gender : " + profile.getGender());
                sweatLossTracker = healthTrackingService.getHealthTracker(HealthTrackerType.SWEAT_LOSS, profile, com.samsung.android.service.health.tracking.data.ExerciseType.RUNNING);
            } catch (final IllegalArgumentException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show()
                );
                finish();
            } catch (final UnsupportedOperationException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show()
                );
                finish();
            }


            binding.sweatlossStartButton.setOnClickListener(view -> {
                Log.i(TAG, " start button called ");
                binding.sweatlossStartButton.setEnabled(false);
                if(!binding.sweatlossRegisterButton.isEnabled()){
                    timerRunning = true;
                    seconds = 0;
                    distance = 0;
                    binding.sweatlossRawValue.setText("");
                    binding.statusValue.setText("");
                    binding.exerciseState.setText("Exercise starting");
                }
                wearHealthServiceManager.startExercise();
            });

            binding.sweatlossStopButton.setOnClickListener(view -> {
                Log.i(TAG, " stop button called ");
                timerRunning = false;
                binding.sweatlossStartButton.setEnabled(true);
                runOnUiThread(() -> binding.exerciseState.setText("Exercise ending"));
                wearHealthServiceManager.flush();
            });

            binding.sweatlossRegisterButton.setOnClickListener(view -> {
                Log.i(TAG, " register button called ");
                if (!isHandlerRunning) {
                    handler.post(() -> {
                        binding.sweatlossRegisterButton.setEnabled(false);
                        binding.sweatlossStartButton.setEnabled(true);
                        sweatLossTracker.setEventListener(trackerEventListener);
                        isHandlerRunning = true;
                    });
                }
            });
        }

        @Override
        public void onConnectionEnded() {

        }

        @Override
        public void onConnectionFailed(HealthTrackerException e) {
            if (e.hasResolution()) {
                e.resolve(SweatLossActivity.this);
            }
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Unable to connect to HSP", Toast.LENGTH_LONG).show());
            finish();
        }
    };

    private final HealthTracker.TrackerEventListener trackerEventListener = new HealthTracker.TrackerEventListener() {
        @Override
        public void onDataReceived(@NonNull List<DataPoint> list) {
            if (list.size() != 0) {
                Log.i(TAG, "onDataReceived List is " + list.size());
                Log.i(TAG, "Timestamp : " + list.get(0).getTimestamp());
                float sweatLoss = list.get(0).getValue(ValueKey.SweatLossSet.SWEAT_LOSS);
                int status = list.get(0).getValue(ValueKey.SweatLossSet.STATUS);
                Log.i(TAG, "Sweat Loss Value : " + sweatLoss+"  Status " +status);
                runOnUiThread(() -> {
                    binding.sweatlossRawValue.setText(String.valueOf(sweatLoss));
                    if(status == 0)
                        binding.statusValue.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                    else
                        binding.statusValue.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    binding.statusValue.setText(String.valueOf(status));
                });
            } else {
                Log.i(TAG, "onDataReceived List is zero");
            }
        }

        @Override
        public void onFlushCompleted() {
            Log.i(TAG, " onFlushCompleted called");
        }

        @Override
        public void onError(HealthTracker.TrackerError trackerError) {
            Log.i(TAG, " onError called");
            if (trackerError == HealthTracker.TrackerError.PERMISSION_ERROR) {
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
        if (PermissionActivity.checkPermission((Context) this, this.permissions)) {
            Log.i(TAG, "onCreate Permission granted");
            setUp();
        } else {
            Log.i(TAG, "onCreate Permission not granted");
            PermissionActivity.showPermissionPrompt((Activity) this, this.REQUEST_ACCOUNT_PERMISSION, this.permissions);
        }
    }

    private void runTimer() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
                binding.sweatlossTime.setText(time);

                if (timerRunning) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
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
        runTimer();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sweat_loss);
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.USER_PROFILE, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.USER_PROFILE)) {
            if (sharedPreferences.getString(Constants.USER_PROFILE, Constants.profile[2]).equals(Constants.profile[0])) {
                weight = Float.parseFloat(Constants.PROFILE1[0]);
                height = Float.parseFloat(Constants.PROFILE1[1]);
                age = Integer.parseInt(Constants.PROFILE1[2]);
                gender = Integer.parseInt(Constants.PROFILE1[3]);
            } else if (sharedPreferences.getString(Constants.USER_PROFILE, Constants.profile[2]).equals(Constants.profile[1])) {
                weight = Float.parseFloat(Constants.PROFILE2[0]);
                height = Float.parseFloat(Constants.PROFILE2[1]);
                age = Integer.parseInt(Constants.PROFILE2[2]);
                gender = Integer.parseInt(Constants.PROFILE2[3]);
            } else {
                weight = Float.parseFloat(Constants.DEFAULT[0]);
                height = Float.parseFloat(Constants.DEFAULT[1]);
                age = Integer.parseInt(Constants.DEFAULT[2]);
                gender = Integer.parseInt(Constants.DEFAULT[3]);
            }
        }
        profile = new TrackerUserProfile.Builder()
                .setHeight(height)
                .setWeight(weight)
                .setGender(gender)
                .setAge(age)
                .build();
        wearHealthServiceManager = WHSManager.getInstance(this);
        healthTrackingService = new HealthTrackingService(connectionListener, getApplicationContext());
        healthTrackingService.connectService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        if(wearHealthServiceManager != null) {
            wearHealthServiceManager.endExerciseWithException();
            wearHealthServiceManager.clear();
        }
        if (sweatLossTracker != null) {
            binding.sweatlossRegisterButton.setEnabled(true);
            sweatLossTracker.unsetEventListener();
        }
        handler.removeCallbacksAndMessages(null);
        isHandlerRunning = false;
        if (healthTrackingService != null) {
            healthTrackingService.disconnectService();
        }

    }

    @Override
    public void spmCallback(float[] spmData, long[] timeStamp) {
        Log.i(TAG, "setting exercise data");
        try {
            sweatLossTracker.setExerciseData(DataType.STEP_PER_MINUTE, spmData, timeStamp);
            binding.spmvalue.setText(String.valueOf(spmData[spmData.length-1]));
        } catch (final IllegalArgumentException e) {
            Log.i(TAG, " Error Data SPM Value : " + Arrays.toString(spmData) + " utcTime : " + timeStamp[0]);
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show()
            );
        } catch (final IllegalStateException e) {
            Log.i(TAG, e.getMessage());
            wearHealthServiceManager.endExerciseWithException();
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show());
        }

    }

    @Override
    public void distanceCallback(double[] distanceData)
    {
        for(double dist:distanceData){
            distance += dist;
        }
        binding.distancevalue.setText(String.format("%.3f",distance/1000)+"km");
        Log.i(TAG, "Distance Values : " + Arrays.toString(distanceData));
    }

    @Override
    public void startExercise() {
        Log.i(TAG, "setting exercise state to start");
        try {
            sweatLossTracker.setExerciseState(ExerciseState.START);
        } catch (final IllegalStateException e) {
            Log.i(TAG, e.getMessage());
            wearHealthServiceManager.endExerciseWithException();
            runOnUiThread(() -> {binding.sweatlossStartButton.setEnabled(true);
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }

        runOnUiThread(() -> {
            binding.exerciseState.setText("Exercise started");
        });
    }

    @Override
    public void endExercise(boolean exception, String state) {
        Log.i(TAG, "setting exercise state to stop");
        if(!state.equals("auto ended")) {
            try {
                sweatLossTracker.setExerciseState(ExerciseState.STOP);
            } catch (final IllegalStateException e) {
                Log.i(TAG, e.getMessage());
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }
        runOnUiThread(() -> binding.sweatlossStartButton.setEnabled(true));
        if (!exception) {
            updateUI("Exercise " + state);
        } else {
            updateUI("");
        }

    }

    private void updateUI(String message){
        runOnUiThread(() -> binding.exerciseState.setText(message));
    }
}
