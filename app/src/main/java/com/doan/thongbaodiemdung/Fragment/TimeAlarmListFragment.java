package com.doan.thongbaodiemdung.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.doan.thongbaodiemdung.Activity.EditAlarmActivity;
import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.Data.TimeInfo;
import com.doan.thongbaodiemdung.Other.AlarmReceiver;
import com.doan.thongbaodiemdung.Other.RouteListAdapter;
import com.doan.thongbaodiemdung.Other.TimeListAdapter;
import com.doan.thongbaodiemdung.R;

import java.io.Serializable;
import java.util.List;

public class TimeAlarmListFragment extends Fragment {

    private Context context;
    private ListView listView;

    private DatabaseHelper dbHelper;


    public TimeAlarmListFragment() {

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

        dbHelper = new DatabaseHelper(getContext());
        listView = (ListView) view.findViewById(R.id.list_time_view);
        listView.setAdapter(new TimeListAdapter(getListTime(), context));

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Object obj = adapterView.getItemAtPosition(i);
//                Route route = (Route) obj;
//
//                //move edit alarm activity
//                Intent intent = new Intent(getContext(), EditAlarmActivity.class);
//                intent.putExtra("route", (Serializable) route);
//                getContext().startActivity(intent);
//            }
//        });

        return view;
    }
    private List<TimeInfo> getListTime() {
        return dbHelper.getListTimeInfo("SELECT * FROM " + DatabaseHelper.TABLE_TIMEHISTORY);
    }
}
