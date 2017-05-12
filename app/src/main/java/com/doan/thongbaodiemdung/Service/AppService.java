package com.doan.thongbaodiemdung.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.doan.thongbaodiemdung.Activity.AlarmActivity;
import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.FirebaseHandle;
import com.doan.thongbaodiemdung.Data.FriendInfo;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hong Hanh on 4/25/2017.
 */

public class AppService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        buildGoogleApiClient();

        List<Route> listRouteEnable = dbHelper.getListRoute("SELECT * FROM " + DatabaseHelper.TABLE_ROUTE + " WHERE isEnable = 1");
        if(listRouteEnable.size() > 0) {
            Intent intent = new Intent(this, BackgroundService.class);
            startService(intent);
        }

        FirebaseHandle.getInstance().setStatusChange();


        List<Route> listRoute = dbHelper.getListRoute("SELECT * FROM " + DatabaseHelper.TABLE_ROUTE);

        for(Route route : listRoute) {
            FirebaseHandle.getInstance().updateRoute(route);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return  START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        FirebaseHandle.getInstance().updateCurPos(location.getLatitude(), location.getLongitude());
        FirebaseHandle.getInstance().setStatusChange();
        List<FriendInfo> friends = FirebaseHandle.getInstance().getListFriends();
        if(friends != null) {
            for (FriendInfo friend: friends) {
                if(friend.getStatus().equals("online") && friend.isFollowing()) {
                    Location friendPos = new Location(LocationManager.GPS_PROVIDER);
                    friendPos.setLatitude(friend.getLatitude());
                    friendPos.setLongitude(friend.getLongitude());
                    if(location.distanceTo(friendPos) < friend.getMinDis()) {
                        if(friend.getRingtoneName().equals("")) {
                            Notification.Builder noti = new Notification.Builder(this)
                                    .setSmallIcon(R.drawable.ic_friends_white)
                                    .setContentText(location.distanceTo(friendPos) + "m")
                                    .setContentTitle(friend.getName() + " đang ở gần bạn");
                            noti.setAutoCancel(true);
                            noti.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(1, noti.build());

                        } else {
                            Intent intent = new Intent(AppService.this, AlarmActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("info", friend.getName() + " đang ở cách bạn " + location.distanceTo(friendPos) + " m");
                            intent.putExtra("ringtone", friend.getRingtoneName());
                            intent.putExtra("ringtonePath", friend.getRingtonePath());
                            startActivity(intent);
                        }
                        FirebaseHandle.getInstance().setFollowFriend(friend.getId(), false);
                    }
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        super.onDestroy();
    }


}
