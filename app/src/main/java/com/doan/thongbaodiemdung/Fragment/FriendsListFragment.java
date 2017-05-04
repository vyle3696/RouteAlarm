package com.doan.thongbaodiemdung.Fragment;

import android.app.AlertDialog;
import android.content.Context;
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
import com.doan.thongbaodiemdung.Activity.LocationFriendActivity;
import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.FirebaseHandle;
import com.doan.thongbaodiemdung.Data.FriendInfo;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.Other.Account;
import com.doan.thongbaodiemdung.Other.FriendsListAdapter;
import com.doan.thongbaodiemdung.Other.RouteListAdapter;
import com.doan.thongbaodiemdung.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HongHa on 4/27/2017.
 */

public class FriendsListFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private ListView listView;
    private Context context;


    public FriendsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        context = view.getContext();

        //dbHelper = new DatabaseHelper(getContext());
        listView = (ListView) view.findViewById(R.id.list_friends);
        listView.setAdapter(new FriendsListAdapter(getListAccount(), getContext()));

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

    private List<FriendInfo> getListAccount() {
        List<FriendInfo> account = FirebaseHandle.getInstance().getListFriends();
        if(account == null) {
            account = new ArrayList<>();
        }
        return account;
    }

}
