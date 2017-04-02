package com.doan.thongbaodiemdung;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by HongHa on 4/1/2017.
 */

public class BackgroundService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private Location mLastLocation;
    private Location mDestination;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private boolean isRinging;
    private MediaPlayer mediaPlayer;
    private String minDistance;
    private String ringtone;
    private int resId;

    private SharedPreferences sharedPreferences;

    public BackgroundService(){}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        buildGoogleApiClient();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ringtone = sharedPreferences.getString("pref_ringtone", "");
        minDistance = sharedPreferences.getString("pref_minDistance", "");
        Log.d("BackgroundService", "ringtone name: " + ringtone);

        // register listener for SharedPreferences changes
        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        resId = this.getResources().getIdentifier(ringtone, "raw", this.getPackageName());
        mediaPlayer = MediaPlayer.create(this, resId);
        mediaPlayer.setLooping(true);

        mDestination = MapsActivity.mDestination;
        isRinging = false;
    }

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if(key.equals("pref_ringtone")) {
                        ringtone = sharedPreferences.getString(key, "");
                        resId = BackgroundService.this.getResources().getIdentifier(ringtone, "raw",
                                BackgroundService.this.getPackageName());
                        if(mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            isRinging = false;
                        }
                        mediaPlayer = MediaPlayer.create(BackgroundService.this, resId);
                        mediaPlayer.setLooping(true);
                    } else if(key.equals("pref_minDistance")) {
                        minDistance = sharedPreferences.getString(key, "");
                    }
                }
            };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return  START_NOT_STICKY;
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
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if(mDestination != null) {
            //if current position close to destination position, ring alarm
            //Toast.makeText(this, "Distance: " + mLastLocation.distanceTo(mDestination), Toast.LENGTH_SHORT).show();
            MapsActivity.distanceTextView.setText("Khoảng cách: " + mLastLocation.distanceTo(mDestination) + "m");
            Log.d("Service", "Updating... " + mLastLocation.distanceTo(mDestination) + "m");
            if(mLastLocation.distanceTo(mDestination) < Integer.parseInt(minDistance) && !isRinging) {
                //alarm ringing
                mediaPlayer.start();
                isRinging = true;
                PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
                wakeLock.acquire();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }
}
