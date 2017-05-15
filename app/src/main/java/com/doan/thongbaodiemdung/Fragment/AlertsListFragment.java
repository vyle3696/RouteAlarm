package com.doan.thongbaodiemdung.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.doan.thongbaodiemdung.Activity.LocationFriendActivity;
import com.doan.thongbaodiemdung.Data.FirebaseHandle;
import com.doan.thongbaodiemdung.Data.FriendInfo;
import com.doan.thongbaodiemdung.Other.AlertsListAdapter;
import com.doan.thongbaodiemdung.Other.FriendsListAdapter;
import com.doan.thongbaodiemdung.R;
import com.doan.thongbaodiemdung.Service.AppService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HongHa on 5/11/2017.
 */

public class AlertsListFragment extends Fragment {

    private ListView listView;
    private Context context;

    public AlertsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private List<FriendInfo> getListAccount() {
        List<FriendInfo> account = AppService.friendNear;
        if(account == null) {
            account = new ArrayList<>();
        }
        Log.e("hanh benh", String.valueOf(account.size()));
        return account;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts_list, container, false);
        context = view.getContext();

        //dbHelper = new DatabaseHelper(getContext());
        listView = (ListView) view.findViewById(R.id.list_alerts);
        listView.setAdapter(new AlertsListAdapter(getListAccount(), getContext()));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj = adapterView.getItemAtPosition(i);
                FriendInfo friendInfo = (FriendInfo) obj;

                if(friendInfo.getStatus().equals("online")) {
                    Intent intent = new Intent(context, LocationFriendActivity.class);
                    intent.putExtra("account", friendInfo);
                    context.startActivity(intent);
                }
            }
        });


        return view;
    }

}
