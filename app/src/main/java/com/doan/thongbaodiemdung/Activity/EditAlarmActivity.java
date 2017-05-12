package com.doan.thongbaodiemdung.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.Data.FirebaseHandle;
import com.doan.thongbaodiemdung.R;

import java.text.DecimalFormat;

public class EditAlarmActivity extends AppCompatActivity {

    private EditText editDesName;
    private TextView txtDesInfo;
    private TextView txtCurDis;
    private TextView txtMinDis;
    private SeekBar btnSetMinDis;
    private TextView txtRingtone;
    private Button btnExit;
    private Button btnUpdate;
    private LinearLayout editRingtoneLayout;

    private Route route;
    private DatabaseHelper dbHelper;

    private int mMinDis;
    private String mName;
    private String mRingtone;
    private String mRingtonePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        dbHelper = new DatabaseHelper(this);

        editDesName = (EditText) findViewById(R.id.edit_name_des);
        txtDesInfo = (TextView) findViewById(R.id.edit_des_info);
        txtCurDis = (TextView) findViewById(R.id.edit_cur_dis);
        txtMinDis = (TextView) findViewById(R.id.edit_dis_to_ring);
        btnSetMinDis = (SeekBar) findViewById(R.id.edit_seekBar);
        txtRingtone = (TextView) findViewById(R.id.edit_ringtone);
        btnExit = (Button) findViewById(R.id.edit_alarm_exit);
        btnUpdate = (Button) findViewById(R.id.edit_alarm_update);
        editRingtoneLayout = (LinearLayout) findViewById(R.id.edit_ringtone_layout);

        final Intent intent = getIntent();
        route = (Route) intent.getSerializableExtra("route");

        //put value to field
        editDesName.setText(route.getName());
        txtDesInfo.setText(route.getInfo());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        Double curDis = route.getDistance();
        txtCurDis.setText((curDis < 1000) ? (decimalFormat.format(curDis) + "m") : (decimalFormat.format(curDis/1000.0) + "km"));
        txtMinDis.setText(route.getMinDistance() + "m");
        btnSetMinDis.setProgress(route.getMinDistance());
        txtRingtone.setText(route.getRingtone());

        mMinDis = route.getMinDistance();
        mRingtone = route.getRingtone();
        mRingtonePath = route.getRingtonePath();

        btnSetMinDis.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = seekBar.getProgress();
                mMinDis = progress;
                txtMinDis.setText("" + progress + "m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        editRingtoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditAlarmActivity.this, EditRingtoneActivity.class);
                intent.putExtra("ringtoneName", mRingtone);
                intent.putExtra("ringtonePath", mRingtonePath);
                startActivityForResult(intent,10);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editDesName.getText().length() > 0) {
                    //Set alarm
                    route.setName(editDesName.getText().toString())
                            .setMinDistance(mMinDis)
                            .setRingtone(mRingtone)
                            .setRingtonePath(mRingtonePath);
                    dbHelper.updateRoute(route);

                    FirebaseHandle.getInstance().updateRoute(route);

                    Toast.makeText(EditAlarmActivity.this,
                            getResources().getText(R.string.update_alarm_success), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(EditAlarmActivity.this,
                            getResources().getText(R.string.error_update_alarm), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditAlarmActivity.this);
                dialog.setTitle(getResources().getText(R.string.warning));
                dialog.setMessage(getResources().getText(R.string.warning_exit));
                dialog.setPositiveButton(getResources().getText(R.string.exit),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                });
                dialog.setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10)
        {
            mRingtone = data.getStringExtra("ringtoneName");
            mRingtonePath = data.getStringExtra("ringtonePath");
            txtRingtone.setText(mRingtone);
        }
    }
}
