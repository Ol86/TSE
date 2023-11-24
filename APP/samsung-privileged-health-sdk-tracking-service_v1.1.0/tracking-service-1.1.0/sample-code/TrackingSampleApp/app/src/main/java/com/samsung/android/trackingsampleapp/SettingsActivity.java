package com.samsung.android.trackingsampleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.samsung.android.service.health.tracking.HealthTrackingService;
import com.samsung.android.trackingsampleapp.databinding.ActivitySettingsBinding;

public class SettingsActivity extends FragmentActivity {

    private final String TAG = SettingsActivity.class.getSimpleName();
    private ActivitySettingsBinding binding;
    private SharedPreferences sharedPreferences;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        Log.i(TAG, "onCreate");

        sharedPreferences =
                this.getSharedPreferences(Constants.USER_PROFILE, Context.MODE_PRIVATE);

        setView();

        binding.buttonOk.setOnClickListener(view -> {
            Log.i(TAG, "Auto test case set");
            int selectedOption = binding.radioProfile.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedOption);
            String selected = radioButton.getText().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (selected.equals(Constants.profile[0])) {
                editor.putString(Constants.USER_PROFILE, Constants.profile[0]);
            } else if (selected.equals(Constants.profile[1])) {
                editor.putString(Constants.USER_PROFILE, Constants.profile[1]);
            } else if (selected.equals(Constants.profile[2])) {
                editor.putString(Constants.USER_PROFILE, Constants.profile[2]);
            }
            editor.apply();
            editor.commit();
            finish();
        });

    }

    private void setView() {
        if (sharedPreferences.contains(Constants.USER_PROFILE)) {
            if (sharedPreferences.getString(Constants.USER_PROFILE, Constants.profile[2]).equals(Constants.profile[0])) {
                binding.radioProfile1.setChecked(true);
            } else if (sharedPreferences.getString(Constants.USER_PROFILE, Constants.profile[2]).equals(Constants.profile[1])) {
                binding.radioProfile2.setChecked(true);
            } else if (sharedPreferences.getString(Constants.USER_PROFILE, Constants.profile[2]).equals(Constants.profile[2])) {
                binding.radioDefault.setChecked(true);
            }
        } else {
            binding.radioDefault.setChecked(true);
        }

        PackageInfo pInfo = null;
        try {
            pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        binding.appVersionValue.setText(pInfo.versionName);

        HealthTrackingService healthTrackingService = new HealthTrackingService(null, getApplicationContext());
        String sdkVer = healthTrackingService.getTrackingCapability().getVersion();
        binding.sdkVersionValue.setText(sdkVer);
    }
}
