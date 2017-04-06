package com.doan.thongbaodiemdung;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by VYLE on 04/04/2017.
 */

public class SplashScreen extends AppCompatActivity {
    protected boolean _active = true;
    protected int _splashTime = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splashscreen);

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    finish();

                    Intent mainIntent = new Intent(SplashScreen.this, FacebookConnection.class);
                    SplashScreen.this.startActivity(mainIntent);
                }
            }
        };
        splashTread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();

    }
}