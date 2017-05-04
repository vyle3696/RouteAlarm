package com.doan.thongbaodiemdung.Other;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.FirebaseHandle;
import com.doan.thongbaodiemdung.Data.FriendInfo;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.R;
import com.doan.thongbaodiemdung.Service.BackgroundService;
import com.google.android.gms.games.Games;

import java.util.List;

/**
 * Created by HongHa on 4/27/2017.
 */

public class FriendsListAdapter extends BaseAdapter {
    private List<FriendInfo> accounts;
    private LayoutInflater inflater;
    private Context context;


    public FriendsListAdapter(List<FriendInfo> accounts, Context context) {
        this.accounts = accounts;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int i) {
        return accounts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        public LinearLayout itemLayout;
        public ImageView friend_avatar;
        public TextView friend_name;
        public CheckBox friend_chkBox;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final FriendsListAdapter.ViewHolder holder;

        if(view == null) {
            view = inflater.inflate(R.layout.fragment_friends, null);
            holder = new FriendsListAdapter.ViewHolder();
            holder.friend_avatar = (ImageView) view.findViewById(R.id.img_avatar);
            holder.friend_name = (TextView) view.findViewById(R.id.friend_name);
            holder.itemLayout = (LinearLayout) view.findViewById(R.id.friend_item);
            holder.friend_chkBox = (CheckBox) view.findViewById(R.id.friend_chkbox);
            view.setTag(holder);
        } else{
            holder = (FriendsListAdapter.ViewHolder) view.getTag();
        }

        Glide.with(context).load(accounts.get(i).getAvatarURL())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.friend_avatar);

        holder.friend_name.setText(accounts.get(i).getName());

        if(accounts.get(i).getStatus().equals("online")) {
            holder.itemLayout.setBackgroundColor(Color.WHITE);
        }

        if(accounts.get(i).isFollowing()) {
            holder.friend_chkBox.setChecked(true);
        }

        final FriendInfo friendInfo= accounts.get(i);

        holder.friend_chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FirebaseHandle.getInstance().setFollowFriend(friendInfo.getId(), b);
                if(b) {
                    Toast.makeText(context, "Đã thiết lập theo dõi vị trí với " + friendInfo.getName(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Đã hủy theo dõi vị trí với " + friendInfo.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
