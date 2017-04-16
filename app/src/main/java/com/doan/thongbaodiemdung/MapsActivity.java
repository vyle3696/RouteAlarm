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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.Other.BackgroundService;
import com.doan.thongbaodiemdung.Other.GPSTracker;
import com.doan.thongbaodiemdung.Other.MapsHandle;
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

    private Location mCurrentDestination;
    private String mDestinationInfo = "";
    private double currentDistance;

    public static TextView distanceTextView;
    private TextView destinationTextView;
    private Switch switchButton;

    private Intent intentService;

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    private DatabaseHelper dbHelper;

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

        dbHelper = new DatabaseHelper(this);
        final Route route = dbHelper.getRoute("SELECT * FROM " + DatabaseHelper.TABLE_ROUTE + " WHERE isEnable = 1");

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

        intentService = new Intent(MapsActivity.this, BackgroundService.class);

        if(route != null) {
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(route.getLatitude());
            location.setLongitude(route.getLongitude());
            mCurrentDestination = location;

            mDestinationInfo = route.getInfo();

            destinationTextView.setText(route.getInfo());
            distanceTextView.setText("Khoảng cách: " + route.getDistance() + "m");
            if(route.getIsEnable() == 1) {
                switchButton.setChecked(true);
                startService(intentService);
            }
            Log.d("MainActivityAlarm", route.toString());
        } else
        {
            switchButton.setEnabled(false);
            destinationTextView.setText("Bạn chưa chọn địa điểm nào");
        }

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Route routeEnable = dbHelper.getRoute("SELECT * FROM " + DatabaseHelper.TABLE_ROUTE + " WHERE isEnable = 1");
                if(isChecked) {
                    Toast.makeText(MapsActivity.this, "Đã thiết lập báo thức", Toast.LENGTH_SHORT).show();
                    if(routeEnable == null)
                        addRouteToDatabase(mDestinationInfo, mCurrentDestination.getLatitude(), mCurrentDestination.getLongitude());

                    startService(intentService);

                } else {
                    Toast.makeText(MapsActivity.this, "Đã hủy thiết lập báo thức", Toast.LENGTH_SHORT).show();
                    if(routeEnable != null) {
                        routeEnable.setIsEnable(0);
                        dbHelper.updateRoute(routeEnable);
                    }
                    stopService(intentService);
                }
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
            gps.getLocation();
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
            //mapsHandle.drawPath(list);

            currentDistance = gps.getCurrentLocation().distanceTo(searchLocation);
            distanceTextView.setText("Khoảng cách: " + currentDistance + "m");
            switchButton.setEnabled(true);

            destinationTextView.setText(mDestinationInfo);
            mCurrentDestination = searchLocation;
        } else {
            Toast.makeText(this, "Chưa lấy được vị trí hiện tại", Toast.LENGTH_SHORT).show();
        }


    }

    private void addRouteToDatabase(String info, double latitude, double longitude) {
        dbHelper.delete("isEnable = 0");
        Route route = new Route();
        route.setInfo(info)
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setIsEnable(1)
                .setDistance(currentDistance);
        dbHelper.insertRoute(route);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intentSettings = new Intent(MapsActivity.this, SettingsActivity.class);
            startActivity(intentSettings);
        } else {
            onMapSearch();
        }

        return super.onOptionsItemSelected(item);
    }

}