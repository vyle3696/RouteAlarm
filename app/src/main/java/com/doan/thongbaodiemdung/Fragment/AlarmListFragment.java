package com.doan.thongbaodiemdung.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.doan.thongbaodiemdung.Data.FirebaseHandle;
import com.doan.thongbaodiemdung.Other.RouteListAdapter;
import com.doan.thongbaodiemdung.R;

import java.io.Serializable;
import java.util.List;

public class AlarmListFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private ListView listView;

    public AlarmListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        listView.setAdapter(new RouteListAdapter(getListRoute(),getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        dbHelper = new DatabaseHelper(getContext());
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(new RouteListAdapter(getListRoute(), getContext()));

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
                alertDialog.setMessage("Bạn có muốn xóa báo thức này không?");
                alertDialog.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
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

                alertDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();

                return true;
            }
        });

        return view;
    }

    private List<Route> getListRoute() {
        return dbHelper.getListRoute("SELECT * FROM " + DatabaseHelper.TABLE_ROUTE);
    }
}
