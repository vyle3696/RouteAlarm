package com.doan.thongbaodiemdung.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.doan.thongbaodiemdung.Other.AlarmReceiver;
import com.doan.thongbaodiemdung.R;

public class TimeAlarmListFragment extends Fragment {

    private Context context;
    private Button startAlarm;
    private Button stopAlarm;
    private PendingIntent mAlarmIntent;

    public TimeAlarmListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_alarm_list, container, false);

        context = view.getContext();
        startAlarm=(Button) view.findViewById(R.id.startTimeAlarm);
        stopAlarm=(Button) view.findViewById(R.id.stopTimeAlarm);

        Intent launchIntent = new Intent(context, AlarmReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(context,0,launchIntent,0);

        final AlarmManager  manager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        startAlarm.setOnClickListener(new View.OnClickListener() {

            long interval = 5*1000;

            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Da dat bao thuc",Toast.LENGTH_SHORT).show();
                manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()+interval,
                        interval, mAlarmIntent);
            }
        });

        stopAlarm.setOnClickListener(new View.OnClickListener() {

            AlarmManager manager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            long interval = 5*1000;

            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Da huy bao thuc",Toast.LENGTH_SHORT).show();
                manager.cancel(mAlarmIntent);
            }
        });
        return view;
    }

}
