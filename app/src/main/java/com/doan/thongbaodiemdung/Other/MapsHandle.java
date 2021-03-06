package com.doan.thongbaodiemdung.Other;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vthha on 4/2/2017.
 */

public class MapsHandle {

    public static MapsHandle instance;

    private static GoogleMap mMap;
    private static Geocoder geocoder;
    private Marker mMarker;
    private Polyline currentPolyline;

    private MapsHandle() {
    }

    public static MapsHandle getInstance(Context context, GoogleMap map) {
        if(instance == null) {
            instance = new MapsHandle();
        }
        mMap = map;
        geocoder = new Geocoder(context, Locale.getDefault());
        return instance;
    }

    public String getPlaceInfo(LatLng latLng) {
        List<Address> addresses = new ArrayList<>();
        String mDestinationInfo = "";
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            android.location.Address address = null;
            address = addresses.get(0);

            if(address != null) {
                StringBuilder builder = new StringBuilder();
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    builder.append(address.getAddressLine(i) + ", ");
                }

                mDestinationInfo = builder.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if(mDestinationInfo.equals("")) {
            mDestinationInfo = "Chưa xác định được thông tin địa điểm";
        }
        return mDestinationInfo;
    }

    public void setting() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void setMyLocationEnable(boolean isEnable) {
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    public void addMarker(LatLng latLng, String title) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        if(mMarker != null)
            mMarker.remove();
        mMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    public void drawPath(List<Location> list) {
        PolylineOptions options = new PolylineOptions();
        options.color(Color.RED);
        options.width(5);
        options.visible(true);

        for(Location loc : list) {
            options.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
        }

        if(currentPolyline != null) {
            currentPolyline.remove();
        }

        currentPolyline = mMap.addPolyline(options);
    }
}
