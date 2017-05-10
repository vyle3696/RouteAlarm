package com.doan.thongbaodiemdung.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
        setHasOptionsMenu(true);
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj = adapterView.getItemAtPosition(i);
                FriendInfo friendInfo = (FriendInfo) obj;

                if(friendInfo.isNotifying()) {
                    FirebaseHandle.getInstance().setNotifyFriend(friendInfo.getId(), false);
                    Toast.makeText(context, "Đã hủy thiết lập thông báo đối với " + friendInfo.getName(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseHandle.getInstance().setNotifyFriend(friendInfo.getId(), true);
                    Toast.makeText(context, "Đã thiết lập thông báo đối với " + friendInfo.getName(),
                            Toast.LENGTH_SHORT).show();
                }

                return false;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_friends, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh_action) {
            listView.setAdapter(null);
            listView.setAdapter(new FriendsListAdapter(getListAccount(), context));
            Toast.makeText(context, "Refresh danh sách bạn bè", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
