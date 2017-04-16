package com.doan.thongbaodiemdung.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.R;

public class EditAlarmActivity extends AppCompatActivity {

    private EditText editDesName;
    private TextView txtDesInfo;
    private TextView txtCurDis;
    private TextView txtMinDis;
    private SeekBar btnSetMinDis;
    private TextView txtRingtone;
    private Button btnExit;
    private Button btnUpdate;

    private Route route;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        editDesName = (EditText) findViewById(R.id.edit_name_des);
        txtDesInfo = (TextView) findViewById(R.id.edit_des_info);
        txtCurDis = (TextView) findViewById(R.id.edit_cur_dis);
        txtMinDis = (TextView) findViewById(R.id.edit_dis_to_ring);
        btnSetMinDis = (SeekBar) findViewById(R.id.edit_seekBar);
        txtRingtone = (TextView) findViewById(R.id.edit_ringtone);
        btnExit = (Button) findViewById(R.id.edit_alarm_exit);
        btnUpdate = (Button) findViewById(R.id.edit_alarm_update);

        Intent intent = getIntent();
        route = (Route) intent.getSerializableExtra("route");

        //put value to field
        editDesName.setText(route.getName());
        txtDesInfo.setText(route.getInfo());
        txtCurDis.setText(route.getDistance() + "m");
        txtMinDis.setText(route.getMinDistance() + "m");
        btnSetMinDis.setProgress(route.getMinDistance());
        txtRingtone.setText(route.getRingtone());
    }
}
