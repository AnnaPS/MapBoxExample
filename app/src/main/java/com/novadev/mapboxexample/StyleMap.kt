package com.novadev.mapboxexample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_user_location.*


class StyleMap : AppCompatActivity() {
    private lateinit var mapboxMap: MapboxMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(
            this,
            getString(R.string.map_box_auth_key)
        )
        setContentView(R.layout.activity_style_map)
        mapView.onCreate(savedInstanceState)
        mapView.onCreate(savedInstanceState)

        initView()
    }


    // Set default map style
    private fun initView(){
        mapView.getMapAsync { mapboxMap ->
            this@StyleMap.mapboxMap = mapboxMap
            mapboxMap.setStyle(Style.MAPBOX_STREETS)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mapstylemenu, menu)
        return true
    }

    // Menu options
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       return when(item.itemId){
           R.id.menu_streets ->{
               mapboxMap.setStyle(Style.MAPBOX_STREETS)
               return true
           }
           R.id.menu_dark->{
               mapboxMap.setStyle(Style.DARK)
               return true
           }

           R.id.menu_light->{
               mapboxMap.setStyle(Style.LIGHT)
               return true
           }
           R.id.menu_outdoors->{
               mapboxMap.setStyle(Style.OUTDOORS)
               return true
           }

           R.id.menu_satellite ->{
               mapboxMap.setStyle(Style.SATELLITE)
               return true
           }
           R.id.menu_satellite_streets ->{
               mapboxMap.setStyle(Style.SATELLITE_STREETS)
               return true
           }
           R.id.menu_traffic_day ->{
               mapboxMap.setStyle(Style.TRAFFIC_DAY)
               return true
           }
           R.id.menu_traffic_night ->{
               mapboxMap.setStyle(Style.TRAFFIC_NIGHT)
               return true
           }
           R.id.home ->{
               finish()
               return true
           }

           else -> return super.onOptionsItemSelected(item)
       }
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

}
