package com.doan.thongbaodiemdung.Other;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.doan.thongbaodiemdung.Activity.AlarmActivity;
import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.Data.TimeInfo;
import com.doan.thongbaodiemdung.R;
import com.doan.thongbaodiemdung.Service.BackgroundService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Asus on 25-12-17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private List<TimeInfo> listTimeInfo;
    public static boolean IS_ALARMING = false;
    private DatabaseHelper dbHelper;


    @Override
    public void onReceive(Context context, Intent intent){
        dbHelper = new DatabaseHelper(context);
        listTimeInfo = dbHelper.getListTimeInfo("SELECT * FROM " + DatabaseHelper.TABLE_TIMEHISTORY);

//        Calendar now = Calendar.getInstance();
//        DateFormat formatter = SimpleDateFormat.getTimeInstance();
//        Toast.makeText(context,formatter.format(now.getTime()),Toast.LENGTH_SHORT).show();
        Toast.makeText(context,"Vao alarm receiver",Toast.LENGTH_SHORT).show();

        if(listTimeInfo != null && listTimeInfo.size() > 0){
            Toast.makeText(context,"Vao if",Toast.LENGTH_SHORT).show();
            if(!this.IS_ALARMING) {
                for (int i = 0; i < listTimeInfo.size(); i++){
Log.d("timelist", String.valueOf(listTimeInfo.get(i).getHour()));
                    Toast.makeText(context,"Vao for"+String.valueOf(listTimeInfo.get(i).getHour()),Toast.LENGTH_SHORT).show();


//                    intent = new Intent(context, AlarmActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("info", listTimeInfo.get(i).getNote());
//                    intent.putExtra("ringtone", "");
//                    intent.putExtra("ringtonePath", "");
//                    context.startActivity(intent);
//                    this.IS_ALARMING=true;
                }
            }
        }

    }
}
