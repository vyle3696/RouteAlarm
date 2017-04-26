package com.doan.thongbaodiemdung.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.Service.BackgroundService;
import com.doan.thongbaodiemdung.Data.FirebaseHandle;
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
    private LinearLayout layoutRingtone;

    private String mDesInfo;
    private int mDisToRing;
    private String mRingtone;
    private String mRingtonePath;
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
        layoutRingtone = (LinearLayout) findViewById(R.id.layoutRingtone) ;


        final Intent intent = getIntent();
        mDesInfo = intent.getStringExtra("des_info");
        mLatitude = intent.getDoubleExtra("latitude", defaultValue);
        mLongitude = intent.getDoubleExtra("longitude", defaultValue);
        mDistance = intent.getDoubleExtra("cur_dis", defaultValue);
        mDisToRing = 100;
        mRingtone = "ringtone";
        mRingtonePath = "";

        txtDesInfo.setText(mDesInfo);
        editDesName.setText(mDesInfo);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        //mDistance = mDistance < 1000 ? mDistance : mDistance/1000.0;
        txtCurDistance.setText((mDistance < 1000) ? (decimalFormat.format(mDistance) + "m") : (decimalFormat.format(mDistance/1000.0) + "km"));

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
                            mLongitude, mDistance, mDisToRing, mRingtone, mRingtonePath );
                    intentService = new Intent(SetAlarmActivity.this, BackgroundService.class);
                    startService(intentService);
                    Toast.makeText(SetAlarmActivity.this, "Thiết lập báo thức thành công", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(SetAlarmActivity.this, "Tên của báo thức không được để trống", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //xu li su kien thay nhap vao thay doi nhac bao thuc
        layoutRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetAlarmActivity.this, EditRingtoneActivity.class);
                intent.putExtra("ringtoneName", "ringtone");
                intent.putExtra("ringtonePath", "");
                startActivityForResult(intent,10);
            }
        });
    }

    //nhan ket qua sau khi chon nhac chuong
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10)
        {
            setRingtone(data.getStringExtra("ringtoneName"),data.getStringExtra("ringtonePath"));
        }


    }

    private void addRouteToDatabase(String name, String info, double latitude, double longitude,
                                    double distance, int minDistance, String ringtone, String ringtonePath) {
        Route route = new Route();
        route.setInfo(info)
                .setName(name)
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setIsEnable(1)
                .setDistance(distance)
                .setMinDistance(minDistance)
                .setRingtone(ringtone)
                .setRingtonePath(ringtonePath);
        dbHelper.insertRoute(route);
        route = dbHelper.getRoute("SELECT * FROM " + DatabaseHelper.TABLE_ROUTE + " ORDER BY id DESC LIMIT 1");

        FirebaseHandle.getInstance().updateRoute(route);
    }

    public void setRingtone(String ringtoneName, String ringtonePath)
    {
        mRingtone = ringtoneName;
        mRingtonePath = ringtonePath;
        txtRingtone.setText(mRingtone);
    }
}