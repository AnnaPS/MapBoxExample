package com.novadev.mapboxexample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.novadev.mapboxexample.locationTracker.LocationTracker
import com.novadev.mapboxexample.marker.InfoWindowSymbolLayerActivity
import com.novadev.mapboxexample.marker.MarkerActivity
import com.novadev.mapboxexample.marker.MarkerGeojson
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initListeners()

    }

    private fun initListeners() {
        btUserLocation.setOnClickListener {
            startActivity(Intent(this, UserLocation::class.java))
        }

        btUStyleMap.setOnClickListener {
            startActivity(Intent(this, StyleMap::class.java))
        }

        btLocationTracker.setOnClickListener {
            startActivity(Intent(this, LocationTracker::class.java))
        }

        btMarker.setOnClickListener {
            startActivity(Intent(this, MarkerGeojson::class.java))
        }
    }


}

