package com.doan.thongbaodiemdung.Fragment;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.doan.thongbaodiemdung.Activity.SetAlarmActivity;
import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.TimeInfo;
import com.doan.thongbaodiemdung.Other.AlarmReceiver;
import com.doan.thongbaodiemdung.R;
import com.doan.thongbaodiemdung.Service.BackgroundService;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetTimeAlarmFragment extends Fragment {

    private Context context;
    private TimePicker timePicker;
    private Button setAlarm;
    private EditText noteText;

    private DatabaseHelper dbHelper;
    private Intent intentService;

    public SetTimeAlarmFragment() {
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
        View view= inflater.inflate(R.layout.fragment_set_time_alarm, container, false);
        context = view.getContext();
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        setAlarm = (Button)view.findViewById(R.id.btnSetTimeAlarm);
        noteText = (EditText)view.findViewById(R.id.remindText);

        dbHelper = new DatabaseHelper(context);

        setAlarm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Calendar calendar = Calendar.getInstance();
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                            timePicker.getHour(), timePicker.getMinute(), 0);
                    addTimeInfoToDatabase(noteText.getText().toString(), timePicker.getHour(), timePicker.getMinute());
                } else {
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                            timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                    addTimeInfoToDatabase(noteText.getText().toString(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                }

                setAlarm(calendar.getTimeInMillis());
            }
        });
        return view;
    }
    private void setAlarm(long time) {
        //getting the alarm manager
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(context, AlarmReceiver.class);

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        //setting the repeating alarm that will be fired every day
        am.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY, pi);
        Toast.makeText(context, getResources().getText(R.string.set_alarm_success), Toast.LENGTH_SHORT).show();
    }

    private void addTimeInfoToDatabase(String note, int hour, int minute) {
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setNote(note)
                .setHour(hour)
                .setMinute(minute);
        dbHelper.insertTimeInfo(timeInfo);
    }
}
