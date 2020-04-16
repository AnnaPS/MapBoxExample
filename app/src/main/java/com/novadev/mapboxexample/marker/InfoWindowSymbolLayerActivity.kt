package com.novadev.mapboxexample.marker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.OnMapClickListener
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.novadev.mapboxexample.R
import kotlinx.android.synthetic.main.activity_marker.*
import java.util.*

/**
 * Use a SymbolLayer to show a BubbleLayout above a SymbolLayer icon. This is a more performant
 * way to show the BubbleLayout that appears when using the MapboxMap.addMarker() method.
 */
class InfoWindowSymbolLayerActivity : AppCompatActivity(), OnMapReadyCallback,
    OnMapClickListener {
    private lateinit var mapboxMap: MapboxMap
    private var source: GeoJsonSource? = null
    private var featureCollection: FeatureCollection? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.map_box_auth_key))
        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_marker)
        // Initialize the map view
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            LoadGeoJsonDataTask(this@InfoWindowSymbolLayerActivity).execute()
            mapboxMap.addOnMapClickListener(this@InfoWindowSymbolLayerActivity)
        }
    }

    override fun onMapClick(point: LatLng): Boolean {
        return handleClickIcon(mapboxMap.projection.toScreenLocation(point))
    }

    /**
     * Sets up all of the sources and layers needed for this example
     *
     * @param collection the FeatureCollection to set equal to the globally-declared FeatureCollection
     */
    fun setUpData(collection: FeatureCollection?) {
        featureCollection = collection
        mapboxMap.getStyle { style: Style ->
            setupSource(style)
            setUpImage(style)
            setUpMarkerLayer(style)
            setUpInfoWindowLayer(style)
        }
    }

    /**
     * Adds the GeoJSON source to the map
     */
    private fun setupSource(loadedStyle: Style) {
        source = GeoJsonSource(
            GEOJSON_SOURCE_ID,
            featureCollection
        )
        loadedStyle.addSource(source!!)
    }

    /**
     * Adds the marker image to the map for use as a SymbolLayer icon
     */
    private fun setUpImage(loadedStyle: Style) {
        loadedStyle.addImage(
            MARKER_IMAGE_ID, this.resources.getDrawable(R.drawable.ic_location_purple))
    }

    /**
     * Updates the display of data on the map after the FeatureCollection has been modified
     */
    fun refreshSource() {
        if (source != null && featureCollection != null) {
            source!!.setGeoJson(featureCollection)
        }
    }

    /**
     * Setup a layer with maki icons, eg. west coast city.
     */
    private fun setUpMarkerLayer(loadedStyle: Style) {
        loadedStyle.addLayer(
            SymbolLayer(
                MARKER_LAYER_ID,
                GEOJSON_SOURCE_ID
            )
                .withProperties(
                    PropertyFactory.iconImage(
                        MARKER_IMAGE_ID
                    ),
                    PropertyFactory.iconAllowOverlap(true),
                    PropertyFactory.iconOffset(
                        arrayOf(
                            0f,
                            -8f
                        )
                    )
                )
        )
    }

    /**
     * Setup a layer with Android SDK call-outs
     *
     *
     * name of the feature is used as key for the iconImage
     *
     */
    private fun setUpInfoWindowLayer(loadedStyle: Style) {
        loadedStyle.addLayer(
            SymbolLayer(
                CALLOUT_LAYER_ID,
                GEOJSON_SOURCE_ID
            )
                .withProperties( /* show image with id title based on the value of the name feature property */
                    PropertyFactory.iconImage("{name}"),  /* set anchor of icon to bottom-left */
                    PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM),  /* all info window and marker image to appear at the same time*/
                    PropertyFactory.iconAllowOverlap(true),  /* offset the info window to be above the marker */
                    PropertyFactory.iconOffset(
                        arrayOf(
                            -2f,
                            -28f
                        )
                    )
                ) /* add a filter to show only when selected feature property is true */
                .withFilter(
                    Expression.eq(
                        Expression.get(
                            PROPERTY_SELECTED
                        ), Expression.literal(true)
                    )
                )
        )
    }

    /**
     * This method handles click events for SymbolLayer symbols.
     *
     *
     * When a SymbolLayer icon is clicked, we moved that feature to the selected state.
     *
     *
     * @param screenPoint the point on screen clicked
     */
    private fun handleClickIcon(screenPoint: PointF): Boolean {
        val features = mapboxMap.queryRenderedFeatures(
            screenPoint,
            MARKER_LAYER_ID
        )
        return if (!features.isEmpty()) {
            val name = features[0]
                .getStringProperty(PROPERTY_NAME)
            val featureList = featureCollection!!.features()
            if (featureList != null) {
                for (i in featureList.indices) {
                    if (featureList[i].getStringProperty(PROPERTY_NAME) == name) {
                        if (featureSelectStatus(i)) {
                            setFeatureSelectState(featureList[i], false)
                        } else {
                            setSelected(i)
                        }
                    }
                }
            }
            true
        } else {
            false
        }
    }

    /**
     * Set a feature selected state.
     *
     * @param index the index of selected feature
     */
    private fun setSelected(index: Int) {
        if (featureCollection!!.features() != null) {
            val feature = featureCollection!!.features()!![index]
            setFeatureSelectState(feature, true)
            refreshSource()
        }
    }

    /**
     * Selects the state of a feature
     *
     * @param feature the feature to be selected.
     */
    private fun setFeatureSelectState(
        feature: Feature,
        selectedState: Boolean
    ) {
        if (feature.properties() != null) {
            feature.properties()!!.addProperty(
                PROPERTY_SELECTED,
                selectedState
            )
            refreshSource()
        }
    }

    /**
     * Checks whether a Feature's boolean "selected" property is true or false
     *
     * @param index the specific Feature's index position in the FeatureCollection's list of Features.
     * @return true if "selected" is true. False if the boolean property is false.
     */
    private fun featureSelectStatus(index: Int): Boolean {
        return if (featureCollection == null) {
            false
        } else featureCollection!!.features()!![index]
            .getBooleanProperty(PROPERTY_SELECTED)
    }

    /**
     * Invoked when the bitmaps have been generated from a view.
     */
    fun setImageGenResults(imageMap: HashMap<String, Bitmap>?) {
        mapboxMap.getStyle { style: Style ->
            // calling addImages is faster as separate addImage calls for each bitmap.
            style.addImages(imageMap!!)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapboxMap.removeOnMapClickListener(this)
        mapView.onDestroy()
    }

    companion object {
        const val GEOJSON_SOURCE_ID = "GEOJSON_SOURCE_ID"
        const val MARKER_IMAGE_ID = "MARKER_IMAGE_ID"
        const val MARKER_LAYER_ID = "MARKER_LAYER_ID"
        const val CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID"
        const val PROPERTY_SELECTED = "selected"
        const val PROPERTY_NAME = "name"
        const val PROPERTY_CAPITAL = "capital"
    }
}