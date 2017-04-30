package com.doan.thongbaodiemdung.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.doan.thongbaodiemdung.Data.FriendInfo;
import com.doan.thongbaodiemdung.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationFriendActivity extends AppCompatActivity {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_friend);

        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps);

        final FriendInfo account = (FriendInfo) getIntent().getSerializableExtra("account");

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                LatLng latLng = new LatLng(account.getLatitude(), account.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
        });
    }


}
