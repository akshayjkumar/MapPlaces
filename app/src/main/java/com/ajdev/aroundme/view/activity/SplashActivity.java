package com.ajdev.aroundme.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ajdev.aroundme.view.activity.NearbyPlacesActivity;
import com.google.android.gms.maps.MapsInitializer;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Pre load / Initialize prior to primary activity load.
         * Speeds up map loading significantly.
         */
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, NearbyPlacesActivity.class);
        startActivity(intent);
        finish();
    }
}
