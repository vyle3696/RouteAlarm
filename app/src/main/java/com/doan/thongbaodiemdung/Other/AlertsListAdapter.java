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
import com.google.android.gms.vision.text.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        public TextView alert_distance;
        public TextView alert_time;
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final AlertsListAdapter.ViewHolder holder;

        if(view == null) {
            view = inflater.inflate(R.layout.fragment_alerts, null);
            holder = new AlertsListAdapter.ViewHolder();
            holder.alert_avatar = (ImageView) view.findViewById(R.id.alert_image);
            holder.alert_name = (TextView) view.findViewById(R.id.alert_name);
            holder.itemLayout = (LinearLayout) view.findViewById(R.id.alert_item);
            holder.alert_distance = (TextView) view.findViewById(R.id.alert_distance);
            holder.alert_time = (TextView) view.findViewById(R.id.noti_time);
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


        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        Double distance = FirebaseHandle.getInstance().getDistanceFromFriend(accounts.get(i).getId());
        holder.alert_distance.setText((distance < 1000) ? ("Cách bạn khoảng: " + decimalFormat.format(distance) + " m") : ("Cách bạn khoảng: " + decimalFormat.format(distance / 1000.0) + " km"));

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy HH:mm");
        String formattedDate = df.format(c.getTime());
        holder.alert_time.setText(formattedDate);

        // Cập nhật location ở đây
        //holder.alert_location = accounts.get(i).getLocation();

        return view;
    }
}
