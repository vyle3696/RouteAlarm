package com.doan.thongbaodiemdung.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.doan.thongbaodiemdung.R;

public class EditRingtoneActivity extends AppCompatActivity {
    private LinearLayout setRingtoneLayout;
    private LinearLayout customRingtoneLayout;
    private RadioButton rdoCustom;
    private RadioButton rdoRingtone;
    private RadioButton rdoHelloRingtone;
    private TextView txtRingtone;

    private String mRingtoneName = "";
    private String mRingtonePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ringtone);

        Intent intent = getIntent();
        mRingtoneName = intent.getStringExtra("ringtoneName");
        mRingtonePath = intent.getStringExtra("ringtonePath");

        setRingtoneLayout = (LinearLayout) findViewById(R.id.set_ringtone_layout);
        customRingtoneLayout = (LinearLayout) findViewById(R.id.ringtone_layout_custom);
        rdoCustom = (RadioButton) findViewById(R.id.rdo_custom_ringtone);
        rdoRingtone = (RadioButton) findViewById(R.id.rdo_ringtone_1);
        rdoHelloRingtone = (RadioButton) findViewById(R.id.rdo_ringtone_2);
        txtRingtone = (TextView) findViewById(R.id.txt_ringtone_name);

        customRingtoneLayout.setVisibility(View.INVISIBLE);

        if(mRingtoneName.equals("ringtone")) {
            rdoRingtone.setChecked(true);
        } else if (mRingtoneName.equals("hello_omfg_ringtone")) {
            rdoHelloRingtone.setChecked(true);
        } else {
            customRingtoneLayout.setVisibility(View.VISIBLE);
            txtRingtone.setText(mRingtoneName);
            rdoCustom.setChecked(true);
        }

        setRingtoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditRingtoneActivity.this, LoadRingtoneActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        rdoCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdoCustom.setChecked(true);
                rdoRingtone.setChecked(false);
                rdoHelloRingtone.setChecked(false);
            }
        });

        rdoRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdoCustom.setChecked(false);
                rdoRingtone.setChecked(true);
                rdoHelloRingtone.setChecked(false);
                mRingtoneName = "ringtone";
                mRingtonePath = "";
            }
        });

        rdoHelloRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdoCustom.setChecked(false);
                rdoRingtone.setChecked(false);
                rdoHelloRingtone.setChecked(true);
                mRingtoneName = "hello_omfg_ringtone";
                mRingtonePath = "";
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("ringtoneName", mRingtoneName);
        intent.putExtra("ringtonePath", mRingtonePath);
        setResult(10, intent);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            String ringtoneName = data.getStringExtra("ringtoneName");
            String ringtonePath = data.getStringExtra("ringtonePath");
            if(!ringtoneName.equals("") && !ringtonePath.equals("")) {
                mRingtoneName = ringtoneName;
                mRingtonePath = ringtonePath;
                txtRingtone.setText(mRingtoneName);
                rdoCustom.setChecked(true);
                rdoRingtone.setChecked(false);
                rdoHelloRingtone.setChecked(false);
                customRingtoneLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
