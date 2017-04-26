package com.doan.thongbaodiemdung.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.Fragment.AlarmListFragment;
import com.doan.thongbaodiemdung.Fragment.MapsFragment;
import com.doan.thongbaodiemdung.MapsActivity;
import com.doan.thongbaodiemdung.Other.AppService;
import com.doan.thongbaodiemdung.Other.CircleTransform;
import com.doan.thongbaodiemdung.Other.FirebaseHandle;
import com.doan.thongbaodiemdung.R;
import com.doan.thongbaodiemdung.SettingsActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName;
    private Toolbar toolbar;

    //index to identify current menu item
    public static int navItemIndex = 0;

    //tag used to attach the fragment
    private static final String TAG_MAP = "map";
    private static final String TAG_ALARM = "alarm";
    private static final String TAG_FRIENDS = "friends";
    public static String CURRENT_TAG = TAG_MAP;

    private static final String urlProfileImg = "https://scontent.fsgn2-2.fna.fbcdn.net/v/t1.0-9/12119138_1512040019118750_7771828771415927462_n.jpg?oh=962a62cafc003edba31349b4accee231&oe=599A50EB";

    //toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    //flag to load map fragment when user presses back key
    private boolean shouldLoadMapFragOnBackPress = true;
    private Handler mHandle;

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandle = new Handler();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        //Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.username);
        imgProfile = (ImageView) navHeader.findViewById(R.id.image_profile);

        //load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        //load nav menu header data
        loadNavHeader();

        //initializing navigation menu
        setUpNavigationView();

        if(savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_MAP;
            loadHomeFragment();
        }

        Intent intent = new Intent(this, AppService.class);
        startService(intent);
    }

    /***
     * Load navigation menu header information
     */
    private void loadNavHeader() {
        txtName.setText("Hồng Hạnh");

        //load profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

    }

    /***
     * Return respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        //select appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        //if user select the current navigation menu again,
        //don't do anything
        //just close the navigation drawer
        if(getSupportFragmentManager().findFragmentByTag(CURRENT_TAG)
                != null) {
            drawerLayout.closeDrawers();
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if(mPendingRunnable != null) {
            mHandle.post(mPendingRunnable);
        }

        drawerLayout.closeDrawers();
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                MapsFragment mapsFragment = new MapsFragment();
                return mapsFragment;
            case 1:
                AlarmListFragment alarmListFragment = new AlarmListFragment();
                return alarmListFragment;
            default:
                return new MapsFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_maps:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_MAP;
                        break;
                    case R.id.nav_alarm:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_ALARM;
                        break;
                    case R.id.nav_friends:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_FRIENDS;
                        break;
                    case R.id.nav_info:
                        //start activity info
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_help:
                        //start activity help
                        drawerLayout.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                if(item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                item.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
            return;
        }

        if(shouldLoadMapFragOnBackPress) {
            if(navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_MAP;
                loadHomeFragment();
                return;
            }
        }
        //super.onBackPressed();
    }

    @Override
    protected void onStop() {
        Log.e("MainActivity", "Stop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.e("MainActivity", "Pause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        Log.e("MainActivity", "Destroy");
    }
}
