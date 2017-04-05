package com.doan.thongbaodiemdung;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private ProgressDialog mProgress;

    private MapsHandle mapsHandle;
    private GPSTracker gps;

    public static Location mDestination;
    private String mDestinationInfo = "";

    public static TextView distanceTextView;
    private TextView destinationTextView;
    private Switch switchButton;

    private Intent intentService;

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Loading map ...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(true);
        mProgress.show();

        //set default value in the app's preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });

        destinationTextView = (TextView) findViewById(R.id.destination);
        distanceTextView = (TextView) findViewById(R.id.distance);
        switchButton = (Switch) findViewById(R.id.switchAlarm);

        switchButton.setEnabled(false);
        destinationTextView.setText("Bạn chưa chọn địa điểm nào");
        intentService = new Intent(MapsActivity.this, BackgroundService.class);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(MapsActivity.this, "Đã thiết lập báo thức", Toast.LENGTH_SHORT).show();
                    startService(intentService);
                } else {
                    Toast.makeText(MapsActivity.this, "Đã hủy thiết lập báo thức", Toast.LENGTH_SHORT).show();
                    stopService(intentService);
                }
            }
        });

        //hien thi cong cu tim kiem
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMapSearch();
            }
        });
    }
    //su kien khi map da load xong
    //thiet dat cac thong so va su kien click tren ban do de hien thong tin diem da click
    private void onMyMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapsHandle = new MapsHandle(this, mMap);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mProgress.dismiss();

                // Hiển thị vị trí người dùng.
                askPermissionsAndShowMyLocation();
            }
        });

        //lay thong tin cua diem vua click
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mDestinationInfo = mapsHandle.getPlaceInfo(latLng);
                addDestinationMarker(latLng);
            }
        });

        mapsHandle.setting();
    }

    //kiem tra dieu kien nguoi dung co dong y cho lay dia diem hien tai hay khong
    //neu cho thi hien thi dia diem do
    private void askPermissionsAndShowMyLocation() {
        //ask permission if API >= 23
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {

                String[] permissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION};

                //request permission
                ActivityCompat.requestPermissions(this, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);

                return;
            }
        }

        //show current location
        this.showMyLocation();
        mapsHandle.setMyLocationEnable(true);
    }

    //tra ve ket qua nguoi dung co cho phep lay vi tri hay khong
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {
                //user allow permission
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();
                    this.showMyLocation();
                    mapsHandle.setMyLocationEnable(true);
                }
                //user don't allow
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    //hien thi dia diem hien tai khi moi vua load xong ban do
    private void showMyLocation() {

        gps = new GPSTracker(MapsActivity.this);

        if(gps.isCanGetLocation()) {
            LatLng latLng = new LatLng(gps.getLatitude(), gps.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        } else {
            gps.showSettingsAlert();
        }
    }

    //hien thi cong cu search autocomplete
    public void onMapSearch() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("VN").build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter).build(MapsActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //sau khi search tra ve ket qua gan marker cho diem do
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng latLng = place.getLatLng();
                mDestinationInfo = place.getName().toString();

                addDestinationMarker(latLng);
            } else if(resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if(resultCode == RESULT_CANCELED) {
            }
        }
    }

    //gan marker cho diem den va ve duong noi va tinh khoang cach giua diem hien tai va diem den
    private void addDestinationMarker(LatLng latLng) {
        //add marker for destination
        mapsHandle.addMarker(latLng, mDestinationInfo);

        //set switch button to off and disable
        switchButton.setChecked(false);
        switchButton.setEnabled(false);

        //draw path between current location to search location
        Location searchLocation = new Location(LocationManager.GPS_PROVIDER);
        searchLocation.setLatitude(latLng.latitude);
        searchLocation.setLongitude(latLng.longitude);

        if(gps.getCurrentLocation() != null) {
            List<Location> list = new ArrayList<>();
            list.add(gps.getCurrentLocation());
            list.add(searchLocation);
            mapsHandle.drawPath(list);

            distanceTextView.setText("Khoảng cách: " + gps.getCurrentLocation().distanceTo(searchLocation) + "m");
            switchButton.setEnabled(true);
        } else {
            Toast.makeText(this, "Chưa lấy được vị trí hiện tại", Toast.LENGTH_SHORT).show();
        }
        mDestination = searchLocation;
        destinationTextView.setText(mDestinationInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentSettings = new Intent(MapsActivity.this, SettingsActivity.class);
        startActivity(intentSettings);

        return super.onOptionsItemSelected(item);
    }
}