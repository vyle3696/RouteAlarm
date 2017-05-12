package com.doan.thongbaodiemdung.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.doan.thongbaodiemdung.Data.FirebaseHandle;
import com.doan.thongbaodiemdung.Data.FriendInfo;
import com.doan.thongbaodiemdung.R;

public class SetFriendNotiActivity extends AppCompatActivity {

    private TextView friendDesInfo;
    private TextView friendDis;
    private TextView friendMinDis;
    private SeekBar friendSeekBar;
    private Button btnSetNoti;
    private RadioButton rdoNotify;
    private RadioButton rdoRingtone;
    private LinearLayout ringtoneLayout;
    private TextView txtRingToneName;

    private String ringtoneName;
    private String ringtonePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_friend_noti);

        friendDesInfo = (TextView) findViewById(R.id.friend_des_info);
        friendDis = (TextView) findViewById(R.id.friend_dis);
        friendMinDis = (TextView) findViewById(R.id.friend_min_dis);
        friendSeekBar = (SeekBar) findViewById(R.id.friend_seekbar);
        btnSetNoti = (Button) findViewById(R.id.set_noti_btn);
        rdoNotify = (RadioButton) findViewById(R.id.rdo_notify);
        rdoRingtone = (RadioButton) findViewById(R.id.rdo_ringtone);
        ringtoneLayout = (LinearLayout) findViewById(R.id.ringtone_layout_friend);
        txtRingToneName = (TextView) findViewById(R.id.txt_ringtone);

        friendDesInfo.setText(getIntent().getStringExtra("info"));
        friendDis.setText(getIntent().getStringExtra("distance") + "m");

        final FriendInfo friendInfo = (FriendInfo) getIntent().getSerializableExtra("friendInfo");
        friendMinDis.setText(friendInfo.getMinDis() + "m");
        friendSeekBar.setProgress(friendInfo.getMinDis());

        ringtoneName = friendInfo.getRingtoneName();
        ringtonePath = friendInfo.getRingtonePath();

        if(ringtoneName.equals("")) {
            ringtoneLayout.setVisibility(View.INVISIBLE);
            rdoNotify.setChecked(true);
        } else {
            ringtoneLayout.setVisibility(View.VISIBLE);
            rdoRingtone.setChecked(true);
            txtRingToneName.setText(ringtoneName);
        }

        friendSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                friendMinDis.setText(i + "m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rdoNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    rdoRingtone.setChecked(false);
                    ringtoneLayout.setVisibility(View.INVISIBLE);
                    ringtoneName = "";
                    ringtonePath = "";
                }
            }
        });

        rdoRingtone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    rdoNotify.setChecked(false);
                    ringtoneLayout.setVisibility(View.VISIBLE);
                    ringtoneName = getResources().getText(R.string.ringtone).toString();
                }
            }
        });

        ringtoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetFriendNotiActivity.this, EditRingtoneActivity.class);
                intent.putExtra("ringtoneName", ringtoneName);
                intent.putExtra("ringtonePath", ringtonePath);
                startActivityForResult(intent,10);
            }
        });

        btnSetNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseHandle.getInstance().updateNotiOfFriend(friendInfo.getId(),
                        friendSeekBar.getProgress(), ringtoneName, ringtonePath);
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10)
        {
            ringtoneName = data.getStringExtra("ringtoneName");
            ringtonePath = data.getStringExtra("ringtonePath");
            txtRingToneName.setText(ringtoneName);
        }
    }
}
