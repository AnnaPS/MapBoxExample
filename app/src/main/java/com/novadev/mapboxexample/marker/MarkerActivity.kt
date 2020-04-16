package com.novadev.mapboxexample.marker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.novadev.mapboxexample.R
import com.novadev.mapboxexample.model.CityPlaces
import kotlinx.android.synthetic.main.activity_marker.*
import kotlinx.android.synthetic.main.marker_bubble.view.*


class MarkerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var markerViewManager: MarkerViewManager
    private lateinit var mapBox: MapboxMap
    private var madridPlaces: MutableList<CityPlaces> = mutableListOf()
    private lateinit var symbolManager: SymbolManager
    private lateinit var myLayer: SymbolLayer
    private var symbols = mutableListOf<Symbol>()


    companion object {
        val JUSTICIA = "Justicia"
        val IGUALDAD = "Igualdad"
        val EDUCACION = "Educacion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.map_box_auth_key))

        setContentView(R.layout.activity_marker)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        generateList()
    }

    private fun generateList() {
        madridPlaces.add(
            CityPlaces(
                "40.36902428128397", "-3.710212677306876",
                "Espacio de Igualdad Berta Cáceres", IGUALDAD
            )
        )
        madridPlaces.add(
            CityPlaces(
                "40.46155641637237", "-3.6501714580414126",
                "Espacio de Igualdad Carme Chacón", IGUALDAD
            )
        )
        madridPlaces.add(
            CityPlaces(
                "40.42591696701239", "-3.6929960000180335",
                "Audiencia Nacional. Sala de lo Penal", JUSTICIA
            )
        )
        madridPlaces.add(
            CityPlaces(
                "40.43745442601165", "-3.6752517217446106",
                "Centro de Danza Carmen Roche", EDUCACION
            )
        )
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapBox = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            markerViewManager = MarkerViewManager(mapView, mapboxMap)
            addMarkers()

        }
    }

    private fun addMarkers() {


        madridPlaces.forEach {

            val customView: View = LayoutInflater.from(this@MarkerActivity).inflate(
                R.layout.marker_bubble, null
            )

            customView.layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            when (it.typePlace) {
                EDUCACION -> {
                    customView.ivMarker.setImageResource(R.drawable.ic_location_green)
                    val marker =
                        MarkerView(LatLng(it.lat.toDouble(), it.lag.toDouble()), customView)
                    marker?.let { marker ->
                        markerViewManager?.addMarker(marker)
                    }

                }

                JUSTICIA -> {
                    customView.ivMarker.setImageResource(R.drawable.ic_location_orange)
                    val marker =
                        MarkerView(LatLng(it.lat.toDouble(), it.lag.toDouble()), customView)
                    marker?.let { marker ->
                        markerViewManager?.addMarker(marker)
                    }
                }
                IGUALDAD -> {

                    customView.ivMarker.setImageResource(R.drawable.ic_location_purple)
                    val marker =
                        MarkerView(LatLng(it.lat.toDouble(), it.lag.toDouble()), customView)
                    marker?.let { marker ->
                        markerViewManager?.addMarker(marker)
                    }
                }
            }

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
