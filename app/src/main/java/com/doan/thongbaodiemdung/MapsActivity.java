package com.doan.thongbaodiemdung;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements LocationListener {

    private GoogleMap myMap;
    private ProgressDialog myProgress;

    private Location mLastLocation;
    private Marker mCurrentMarker;

    private Geocoder geocoder;

    public static Location mDestination;
    private Marker mDestinationMarker;
    private String mDestinationInfo = "";

    private TextView destinationTextView;

    private static final String MYTAG = "MapsActivity";

    // Mã yêu cầu hỏi người dùng cho phép xem vị trí hiện tại của họ
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Tạo Progress Bar
        myProgress = new ProgressDialog(this);
        myProgress.setTitle("Map Loading ...");
        myProgress.setMessage("Please wait...");
        myProgress.setCancelable(true);

        // Hiển thị Progress Bar
        myProgress.show();

        geocoder = new Geocoder(this, Locale.getDefault());

        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);


        // Sét đặt sự kiện thời điểm GoogleMap đã sẵn sàng.
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });

        destinationTextView = (TextView) findViewById(R.id.destination);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMapSearch();
            }
        });
    }

    private void onMyMapReady(GoogleMap googleMap) {

        // Lấy đối tượng Google Map ra:
        myMap = googleMap;

        // Thiết lập sự kiện đã tải Map thành công
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {

                // Đã tải thành công thì tắt Dialog Progress đi
                myProgress.dismiss();

                // Hiển thị vị trí người dùng.
                askPermissionsAndShowMyLocation();
            }
        });

        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                List<Address> addresses = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Address address = addresses.get(0);

                if(address != null) {
                    StringBuilder builder = new StringBuilder();
                    for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        builder.append(address.getAddressLine(i) + ", ");
                    }

                    mDestinationInfo = builder.toString();
                }

                addDestinationMarker(latLng);
            }
        });


        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(true);
    }


    private void askPermissionsAndShowMyLocation() {


        // Với API >= 23, bạn phải hỏi người dùng cho phép xem vị trí của họ.
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);


            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {

                // Các quyền cần người dùng cho phép.
                String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};

                // Hiển thị một Dialog hỏi người dùng cho phép các quyền trên.
                ActivityCompat.requestPermissions(this, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);

                return;
            }
        }

        // Hiển thị vị trí hiện thời trên bản đồ.
        this.showMyLocation();
        myMap.setMyLocationEnabled(true);
    }


    // Khi người dùng trả lời yêu cầu cấp quyền (cho phép hoặc từ chối).
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {


                // Chú ý: Nếu yêu cầu bị bỏ qua, mảng kết quả là rỗng.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();

                    // Hiển thị vị trí hiện thời trên bản đồ.
                    this.showMyLocation();
                }
                // Hủy bỏ hoặc từ chối.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    // Tìm một nhà cung cấp vị trị hiện thời đang được mở.
    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        // Tiêu chí để tìm một nhà cung cấp vị trí.
        Criteria criteria = new Criteria();

        // Tìm một nhà cung vị trí hiện thời tốt nhất theo tiêu chí trên.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show();
            return null;
        }
        return bestProvider;
    }

    // Chỉ gọi phương thức này khi đã có quyền xem vị trí người dùng.
    private void showMyLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String locationProvider = this.getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }

        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

        Location myLocation = null;
        try {

            // Đoạn code nay cần người dùng cho phép (Hỏi ở trên ***).
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

            // Lấy ra vị trí.
            myLocation = locationManager
                    .getLastKnownLocation(locationProvider);
        }
        // Với Android API >= 23 phải catch SecurityException.
        catch (SecurityException e) {
            Toast.makeText(this, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }
        if (myLocation != null) {
            mLastLocation = myLocation;
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));


            //place current position marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mCurrentMarker = myMap.addMarker(markerOptions);
            mCurrentMarker.showInfoWindow();

            myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        } else {
            Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
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

    private void addDestinationMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Destination");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        if(mDestinationMarker != null)
            mDestinationMarker.remove();
        mDestinationMarker = myMap.addMarker(markerOptions);

        //move map camera
        myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        destinationTextView.setText(mDestinationInfo);
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


    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //place current position marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        if(mCurrentMarker != null) {
            mCurrentMarker.remove();
        }
        mCurrentMarker = myMap.addMarker(markerOptions);
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

}