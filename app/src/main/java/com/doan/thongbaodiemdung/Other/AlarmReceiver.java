package com.doan.thongbaodiemdung.Other;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.doan.thongbaodiemdung.Activity.AlarmActivity;
import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.Service.BackgroundService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Asus on 25-12-17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Calendar now = Calendar.getInstance();
        DateFormat formatter = SimpleDateFormat.getTimeInstance();
        Toast.makeText(context,formatter.format(now.getTime()),Toast.LENGTH_SHORT).show();
        if(!BackgroundService.IS_ALARMING) {
            intent = new Intent(context, AlarmActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("info", "Test Time Alarm");
            intent.putExtra("ringtone", "");
            intent.putExtra("ringtonePath", "");
            context.startActivity(intent);
            BackgroundService.IS_ALARMING=true;
        }
        else{
            Toast.makeText(context,"IS_ALARMING=TRUE",Toast.LENGTH_SHORT).show();
        }
    }
}
