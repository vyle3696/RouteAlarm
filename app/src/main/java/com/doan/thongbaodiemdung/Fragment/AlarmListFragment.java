package com.doan.thongbaodiemdung.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.doan.thongbaodiemdung.Activity.EditAlarmActivity;
import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.Data.FirebaseHandle;
import com.doan.thongbaodiemdung.Other.RouteListAdapter;
import com.doan.thongbaodiemdung.R;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmListFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private ListView listView;
    private Timer timer;
    private Context context;

    public AlarmListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(timer == null)
            setUpdateListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        context = view.getContext();
        dbHelper = new DatabaseHelper(getContext());
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(new RouteListAdapter(getListRoute(), context));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj = adapterView.getItemAtPosition(i);
                Route route = (Route) obj;

                //move edit alarm activity
                Intent intent = new Intent(getContext(), EditAlarmActivity.class);
                intent.putExtra("route", (Serializable) route);
                getContext().startActivity(intent);
            }
        });

        //remove item if long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj = adapterView.getItemAtPosition(i);
                final Route route = (Route) obj;

                //show alert dialog to confirm user to delete route
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setMessage(getResources().getText(R.string.ask_for_delete));
                alertDialog.setPositiveButton(getResources().getText(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //delete route
                        dbHelper.deleteRoute("id = " + route.getId());

                        FirebaseHandle.getInstance().removeRoute(String.valueOf(route.getId()));
                        //remove item
                        listView.setAdapter(null);
                        listView.setAdapter(new RouteListAdapter(getListRoute(), getContext()));

                    }
                });

                alertDialog.setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();

                return true;
            }
        });

        setUpdateListView();

        return view;
    }

    private void setUpdateListView() {
        timer = new Timer();

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                listView.setAdapter(null);
                listView.setAdapter(new RouteListAdapter(getListRoute(), context));
            }
        };

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };

        timer.schedule(timerTask, 3000, 1500);
    }

    private List<Route> getListRoute() {
        return dbHelper.getListRoute("SELECT * FROM " + DatabaseHelper.TABLE_ROUTE);
    }
}
