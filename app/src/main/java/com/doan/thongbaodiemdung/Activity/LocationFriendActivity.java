package com.doan.thongbaodiemdung.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.doan.thongbaodiemdung.Data.FirebaseHandle;
import com.doan.thongbaodiemdung.Data.FriendInfo;
import com.doan.thongbaodiemdung.Other.CircleTransform;
import com.doan.thongbaodiemdung.Other.MapsHandle;
import com.doan.thongbaodiemdung.R;
import com.doan.thongbaodiemdung.Service.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.ExecutionException;

public class LocationFriendActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private GPSTracker gps;
    private Location curPos;
    private FriendInfo account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_friend);

        showMyLocation();

        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps);


        account = (FriendInfo) getIntent().getSerializableExtra("account");
        Location friendPos = new Location(LocationManager.GPS_PROVIDER);
        friendPos.setLatitude(account.getLatitude());
        friendPos.setLongitude(account.getLongitude());

        final double distance = curPos.distanceTo(friendPos);
        final String posInfo = MapsHandle.getInstance(this, mMap).getPlaceInfo(new LatLng(
                friendPos.getLatitude(), friendPos.getLongitude()));

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                LatLng latLng = new LatLng(account.getLatitude(), account.getLongitude());
                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(posInfo);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        mMap.addMarker(markerOptions);
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {

                            Bitmap bitmap = Glide.with(LocationFriendActivity.this)
                                    .load(account.getAvatarURL())
                                    .asBitmap()
                                    .into(-1, -1).get();

                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(circleBitmap(bitmap)));

                        } catch (Exception ex) {
                            Log.e("LocationFriendActivity", ex.getMessage());
                        }
                        return null;
                    }
                }.execute();


                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Intent intent = new Intent(LocationFriendActivity.this, SetFriendNotiActivity.class);
                        intent.putExtra("info", posInfo);
                        intent.putExtra("distance", String.valueOf(distance));
                        intent.putExtra("friendInfo", account);
                        startActivity(intent);
                        return true;
                    }
                });
            }
        });
    }

    public static Bitmap circleBitmap(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap circleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle((float) width/2,
                (float) height/2,
                (float) Math.min(width, height/2),
                Path.Direction.CCW);

        final Canvas canvas = new Canvas(circleBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return circleBitmap;
    }

    private void showMyLocation() {

        gps = GPSTracker.getInstance(this);

        if(gps.isCanGetLocation()) {
            curPos = new Location(LocationManager.GPS_PROVIDER);
            curPos.setLatitude(gps.getLatitude());
            curPos.setLongitude(gps.getLongitude());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
