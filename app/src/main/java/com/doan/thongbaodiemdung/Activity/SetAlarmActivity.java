package com.doan.thongbaodiemdung.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.doan.thongbaodiemdung.Other.BackgroundService;
import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.R;

import java.text.DecimalFormat;

import static android.R.attr.defaultValue;

public class SetAlarmActivity extends AppCompatActivity {
    private EditText editDesName;
    private TextView txtDesInfo;
    private TextView txtDisToRing;
    private TextView txtRingtone;
    private TextView txtCurDistance;
    private Button btnSetAlarm;
    private SeekBar disSeekBar;

    private String mDesName;
    private String mDesInfo;
    private int mDisToRing;
    private String mRingtone;
    private Double mLatitude;
    private Double mLongitude;
    private Double mDistance;

    private DatabaseHelper dbHelper;
    private Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        dbHelper = new DatabaseHelper(this);

        editDesName = (EditText) findViewById(R.id.destination_name);
        txtDesInfo = (TextView) findViewById(R.id.destination_info);
        txtDisToRing = (TextView) findViewById(R.id.distanceToRing);
        txtRingtone = (TextView) findViewById(R.id.ringtone);
        txtCurDistance = (TextView) findViewById(R.id.cur_distance);
        btnSetAlarm = (Button) findViewById(R.id.set_alarm_btn);
        disSeekBar = (SeekBar) findViewById(R.id.seekBar);

        Intent intent = getIntent();
        mDesName = "";
        mDesInfo = intent.getStringExtra("des_info");
        mLatitude = intent.getDoubleExtra("latitude", defaultValue);
        mLongitude = intent.getDoubleExtra("longitude", defaultValue);
        mDistance = intent.getDoubleExtra("cur_dis", defaultValue);
        mDisToRing = 100;
        mRingtone = "ringtone";

        txtDesInfo.setText(mDesInfo);
        editDesName.setText(mDesInfo);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        txtCurDistance.setText("" + ((mDistance < 1000)? "" + mDistance + "m": "" + decimalFormat.format(mDistance/1000.0) + "km"));

        disSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = seekBar.getProgress();
                mDisToRing = progress;
                txtDisToRing.setText("" + progress + "m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editDesName.getText().equals("")) {
                    //Set alarm
                    addRouteToDatabase(editDesName.getText().toString(), mDesInfo, mLatitude,
                            mLongitude, mDistance, mDisToRing, mRingtone );
                    intentService = new Intent(SetAlarmActivity.this, BackgroundService.class);
                    startService(intentService);
                }
            }
        });
    }

    private void addRouteToDatabase(String name, String info, double latitude, double longitude,
                                    double distance, int minDistance, String ringtone) {
        Route route = new Route();
        route.setInfo(info)
                .setName(name)
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setIsEnable(1)
                .setDistance(distance)
                .setMinDistance(minDistance)
                .setRingtone(ringtone);
        dbHelper.insertRoute(route);
    }
}
