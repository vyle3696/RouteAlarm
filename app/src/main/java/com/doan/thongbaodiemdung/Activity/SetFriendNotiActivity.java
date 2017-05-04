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
    private CheckBox chkNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_friend_noti);

        friendDesInfo = (TextView) findViewById(R.id.friend_des_info);
        friendDis = (TextView) findViewById(R.id.friend_dis);
        friendMinDis = (TextView) findViewById(R.id.friend_min_dis);
        friendSeekBar = (SeekBar) findViewById(R.id.friend_seekbar);
        btnSetNoti = (Button) findViewById(R.id.set_noti_btn);
        chkNoti = (CheckBox) findViewById(R.id.chk_friend_noti);


        friendDesInfo.setText(getIntent().getStringExtra("info"));
        friendDis.setText(getIntent().getStringExtra("distance") + "m");
        final FriendInfo friendInfo = (FriendInfo) getIntent().getSerializableExtra("friendInfo");
        friendMinDis.setText(friendInfo.getMinDis() + "m");
        friendSeekBar.setProgress(friendInfo.getMinDis());
        if(friendInfo.isNotifying()) {
            chkNoti.setChecked(true);
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

        btnSetNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseHandle.getInstance().updateNotiOfFriend(friendInfo.getId(), friendSeekBar.getProgress());
                FirebaseHandle.getInstance().setNotifyFriend(friendInfo.getId(), chkNoti.isChecked());
                onBackPressed();
            }
        });
    }

}
