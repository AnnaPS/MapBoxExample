package com.novadev.mapboxexample.marker

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.novadev.mapboxexample.R
import kotlinx.android.synthetic.main.activity_marker.*
import java.net.URI
import java.net.URISyntaxException

/**
 * The most basic example of adding a map to an activity.
 */
class MarkerGeojson : AppCompatActivity() {
    private lateinit var mapboxMap: MapboxMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.map_box_auth_key))
        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_marker)
        mapView.onCreate(savedInstanceState)

        initListeners()
        getMap()




    }

    private fun initListeners() {
        ivClose.setOnClickListener {
            cvInfo.visibility = View.GONE
        }
    }

    private fun getMap() {
        mapView.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(
                Style.MAPBOX_STREETS
            ) {
                this@MarkerGeojson.mapboxMap = mapboxMap
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
                GeoJSONToMap(
                    "source-id1",
                    "first-layer-id",
                    "asset://madridmujeres.geojson"
                )
                GeoJSONToMap(
                    "source-id2",
                    "second-layer-id",
                    "asset://madridoficinascorreos.geojson"
                )

                mapboxMap.addOnMapClickListener { point ->
                    val pixel = mapboxMap.projection.toScreenLocation(point)
                    val features = mapboxMap.queryRenderedFeatures(pixel)

                    // Get the first feature within the list if one exist
                    if (features.size > 0) {
                        val feature = features[0]

                        // Ensure the feature has properties defined
                        for ((key, value) in feature.properties()!!.entrySet()) {
                            // Log all the properties

                            Log.d("TAG", String.format("%s = %s", key, value))
                            when(key){
                                "NOMBRE"-> tvTitleMarker.text = value.toString()
                                "TELEFONO" -> tvSubtitlemarker.text = value.toString()
                            }

                            cvInfo.visibility = View.VISIBLE

                        }
                    }
                    true
                }
            }
        }
    }

    private fun GeoJSONToMap(
        sourceId: String?,
        layerId: String,
        asset_id: String?
    ) {
        mapboxMap!!.getStyle { style ->
            try {
                val source = GeoJsonSource(sourceId, URI(asset_id))
                style.addSource(source)
                if (layerId == "first-layer-id") {
                    style.addImage("$layerId marker", this.resources.getDrawable(R.drawable.ic_location_purple))
                } else {
                    style.addImage("$layerId marker", this.resources.getDrawable(R.drawable.ic_location_orange))
                }
                val symbolLayer = SymbolLayer(layerId, sourceId)
                symbolLayer.setProperties(
                    PropertyFactory.iconImage("$layerId marker"),
                    PropertyFactory.iconAllowOverlap(true),
                    PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM),  // You should use this if you're using a pin-like icon image
                    PropertyFactory.iconIgnorePlacement(true)
                )
                style.addLayer(symbolLayer)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    public override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    public override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

}