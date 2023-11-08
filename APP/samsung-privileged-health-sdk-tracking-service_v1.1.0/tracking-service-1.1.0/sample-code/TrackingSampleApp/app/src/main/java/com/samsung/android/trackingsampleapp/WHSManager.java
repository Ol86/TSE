package com.samsung.android.trackingsampleapp;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.health.services.client.ExerciseClient;
import androidx.health.services.client.ExerciseUpdateListener;
import androidx.health.services.client.HealthServices;
import androidx.health.services.client.data.Availability;
import androidx.health.services.client.data.DataPoint;
import androidx.health.services.client.data.DataType;
import androidx.health.services.client.data.ExerciseConfig;
import androidx.health.services.client.data.ExerciseLapSummary;
import androidx.health.services.client.data.ExerciseState;
import androidx.health.services.client.data.ExerciseType;
import androidx.health.services.client.data.ExerciseUpdate;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WHSManager {
    private static WHSManager whsManager;
    private ExerciseClient exerciseClient;
    private final String TAG = WHSManager.class.getSimpleName();
    private Context mContext;
    private final String STEPS_PER_MINUTE = "Step per minute";
    private final String DISTANCE = "Distance";
    private boolean useGps = false;
    private boolean isExceriseStarted = false;

    SPMCallback callback;

    interface SPMCallback {
        void spmCallback(float[] data, long[] dataTs);

        void startExercise();

        void endExercise(boolean exception, String state);

        void distanceCallback(double[] data);
    }

    private WHSManager() {
    }

    private WHSManager(Context context) {
        mContext = context;
        callback = (SPMCallback) context;
        initExerciseClient();
    }

    public static WHSManager getInstance(Context context) {
        if (whsManager == null)
            whsManager = new WHSManager(context);
        return whsManager;
    }

    private void initExerciseClient() {
        if (exerciseClient == null) {
            exerciseClient = HealthServices.getClient(mContext).getExerciseClient();
            Log.i(TAG, "Calling initExerciseClient-setUpdateListener");
            exerciseClient.setUpdateListener(exerciseUpdateListener);
        }
    }

    private final ExerciseUpdateListener exerciseUpdateListener =
            new ExerciseUpdateListener() {

                @Override
                public void onExerciseUpdate(ExerciseUpdate update) {
                    Log.i(TAG, "ExerciseUpdate update :" + update);

                    ExerciseState state = update.getState();
                    if (state == ExerciseState.AUTO_ENDED || state == ExerciseState.TERMINATED) {
                        Log.i(TAG, "Exercise is auto ended:" + state.toString());
                        callback.endExercise(false, "auto ended");
                        return;
                    }

                    update.getLatestMetrics().forEach(this::setMetricView);
                }

                private void setMetricView(DataType dataType, List<DataPoint> dataPoints) {

                    Log.i(TAG,"Datatype: "+dataType.getName());
                    if (dataType.getName().equals(STEPS_PER_MINUTE)) {
                        int listSize = dataPoints.size();
                        Log.i(TAG,"Received DataPoints size = "+ listSize);

                        float[] spmdata = new float[listSize];
                        long[] timeStamp = new long[listSize];
                        for (int i = 0; i < listSize; ++i){
                            spmdata[i] = (float) Iterables.get(dataPoints,i).getValue().asLong();
                            timeStamp[i] = getUtcTimeFromSystemElapsedTime(Iterables.get(dataPoints,i).getEndDurationFromBoot().toMillis());
                        }
                        Log.i(TAG, " SPM Data Value : " +Arrays.toString(spmdata) + " utcTime : " + Arrays.toString(timeStamp));
                        if (isExceriseStarted)
                        {
                            callback.spmCallback(spmdata, timeStamp);
                        }
                    }
                    if(dataType.getName().equals(DISTANCE)) {
                        int listSize = dataPoints.size();
                        Log.i(TAG,"Received DataPoints size = "+ listSize);

                        double[] distancedata = new double[listSize];
                        for (int i = 0; i < listSize; ++i){
                            distancedata[i] = Iterables.get(dataPoints,i).getValue().asDouble();
                        }
                        Log.i(TAG, " Distance Data Value : " +Arrays.toString(distancedata));
                        if (isExceriseStarted)
                        {
                            callback.distanceCallback(distancedata);
                        }
                    }
                }

                @Override
                public void onLapSummary(ExerciseLapSummary lapSummary) {
                    Log.i(TAG, "ExerciseUpdate onLapSummary");
                }

                @Override
                public void onAvailabilityChanged(DataType dataType, Availability availability) {
                    Log.i(TAG, "ExerciseUpdate onAvailabilityChanged");
                }
            };

    private long getUtcTimeFromSystemElapsedTime(long elapsedTimeInMillisecond) {
        long currentSystemElapsed = SystemClock.elapsedRealtime();
        long diffTime = currentSystemElapsed - elapsedTimeInMillisecond;
        return System.currentTimeMillis() - diffTime;
    }

    protected void startExercise() {
        isExceriseStarted = true;
        Set<DataType> dataType = new HashSet<>();
        dataType.add(DataType.STEPS_PER_MINUTE);
        dataType.add(DataType.DISTANCE);

        ExerciseConfig.Builder exerciseConfigBuilder =
                ExerciseConfig.builder()
                        .setExerciseType(ExerciseType.RUNNING)
                        .setAggregateDataTypes(dataType)
                        .setDataTypes(dataType)
                        .setShouldEnableGps(useGps);

        Log.i(TAG, "Calling startExercise");
        Futures.addCallback(exerciseClient.startExercise(exerciseConfigBuilder.build()), new FutureCallback<Void>() {
            @Override
            public void onSuccess(@org.checkerframework.checker.nullness.qual.Nullable Void result) {
                Log.i(TAG, "onSuccess exercise is started");

                callback.startExercise();

            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG, "onFailure starting exercise : " + t.getMessage());
            }
        }, ContextCompat.getMainExecutor(mContext));
    }

    protected void flush(){
        Log.i(TAG, "Calling Flush");
        Futures.addCallback(
                exerciseClient.flushExercise(), new FutureCallback<Void>() {
                    @Override
                    public void onSuccess(@org.checkerframework.checker.nullness.qual.Nullable Void result) {
                        Log.i(TAG, "onSuccess Flush Exercise");
                        endExercise();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i(TAG, "onFailure Flush Exercise : " + t.getMessage());
                        endExercise();
                    }
                }, ContextCompat.getMainExecutor(mContext)
        );
    }
    protected void endExercise() {
        isExceriseStarted = false;
        Log.i(TAG, "Calling endExercise");
        Futures.addCallback(
                exerciseClient.endExercise(), new FutureCallback<Void>() {
                    @Override
                    public void onSuccess(@org.checkerframework.checker.nullness.qual.Nullable Void result) {
                        Log.i(TAG, "onSuccess exercise is ended");
                        callback.endExercise(false, "manually ended");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i(TAG, "onFailure ending exercise : " + t.getMessage());
                        callback.endExercise(true, "");
                    }
                }, ContextCompat.getMainExecutor(mContext)
        );
    }

    protected void endExerciseWithException(){
        isExceriseStarted = false;
        Log.i(TAG, "Calling endExerciseWithException");
        Futures.addCallback(
                exerciseClient.endExercise(), new FutureCallback<Void>() {
                    @Override
                    public void onSuccess(@org.checkerframework.checker.nullness.qual.Nullable Void result) {
                        Log.i(TAG, "onSuccess exercise is ended");
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Log.i(TAG, "onFailure ending exercise : " + t.getMessage());
                    }
                }, ContextCompat.getMainExecutor(mContext)
        );
    }

    protected void clear() {
        if (exerciseClient != null) {
            exerciseClient.clearUpdateListener(exerciseUpdateListener);
        }
        whsManager = null;
    }
}
