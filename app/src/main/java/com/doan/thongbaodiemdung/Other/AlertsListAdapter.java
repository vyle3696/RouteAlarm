package com.doan.thongbaodiemdung.Other;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.doan.thongbaodiemdung.Data.FirebaseHandle;
import com.doan.thongbaodiemdung.Data.FriendInfo;
import com.doan.thongbaodiemdung.Fragment.AlertsListFragment;
import com.doan.thongbaodiemdung.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by HongHa on 5/11/2017.
 */

public class AlertsListAdapter extends BaseAdapter {
    private List<FriendInfo> accounts;
    private LayoutInflater inflater;
    private Context context;


    public AlertsListAdapter(List<FriendInfo> accounts, Context context) {
        this.accounts = accounts;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    static class ViewHolder {
        public LinearLayout itemLayout;
        public ImageView alert_avatar;
        public TextView alert_name;
        public TextView alert_location;
        public TextView alert_distance;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final AlertsListAdapter.ViewHolder holder;

        if(view == null) {
            view = inflater.inflate(R.layout.fragment_alerts, null);
            holder = new AlertsListAdapter.ViewHolder();
            holder.alert_avatar = (ImageView) view.findViewById(R.id.img_avatar);
            holder.alert_name = (TextView) view.findViewById(R.id.alert_name);
            holder.itemLayout = (LinearLayout) view.findViewById(R.id.alert_item);
            holder.alert_location = (TextView) view.findViewById(R.id.alert_location);
            holder.alert_distance = (TextView) view.findViewById(R.id.alert_distance);
            view.setTag(holder);
        } else{
            holder = (AlertsListAdapter.ViewHolder) view.getTag();
        }

        Glide.with(context).load(accounts.get(i).getAvatarURL())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.alert_avatar);

        holder.alert_name.setText(accounts.get(i).getName() + " đang ở gần bạn");

        if(accounts.get(i).getStatus() == "online") {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            Double distance = FirebaseHandle.getInstance().getDistanceFromFriend(accounts.get(i).getId());
            holder.alert_distance.setText((distance < 1000) ? ("Cách bạn khoảng: " + decimalFormat.format(distance) + " m") : ("Cách bạn khoảng: " + decimalFormat.format(distance / 1000.0) + " km"));
        }
        else
            holder.alert_distance.setText("");

        final FriendInfo friendInfo= accounts.get(i);

        // Cập nhật location ở đây
        //holder.alert_location = accounts.get(i).getLocation();

        return view;
    }
}
