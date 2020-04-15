package com.novadev.mapboxexample.locationTracker

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener
import com.mapbox.mapboxsdk.location.OnLocationCameraTransitionListener
import com.mapbox.mapboxsdk.location.OnLocationClickListener
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.novadev.mapboxexample.R
import kotlinx.android.synthetic.main.activity_location_tracker.*


class LocationTracker : AppCompatActivity(),
    OnMapReadyCallback, PermissionsListener,
    OnLocationClickListener, OnCameraTrackingChangedListener {
    private val SAVED_STATE_CAMERA = "saved_state_camera"
    private val SAVED_STATE_RENDER = "saved_state_render"
    private val SAVED_STATE_LOCATION = "saved_state_location"
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private var lastLocation: Location? = null

    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    lateinit var mapboxMap: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var locationEngine: LocationEngine
    private val callback: LocationTrackerCallback =
        LocationTrackerCallback(this)

    @CameraMode.Mode
    private var cameraMode = CameraMode.TRACKING

    @RenderMode.Mode
    private var renderMode = RenderMode.NORMAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.map_box_auth_key))
        setContentView(R.layout.activity_location_tracker)


        // Check and use saved instance state in case of device rotation
        if (savedInstanceState != null) {
            cameraMode = savedInstanceState.getInt(SAVED_STATE_CAMERA)
            renderMode = savedInstanceState.getInt(SAVED_STATE_RENDER)
            lastLocation = savedInstanceState.getParcelable(SAVED_STATE_LOCATION)
        }
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapView.getMapAsync {
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                enableLocationComponent(it)
                setModeButtonListeners()
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Set the LocationComponent activation options
            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .useDefaultLocationEngine(true)
                    .locationEngineRequest(
                        LocationEngineRequest.Builder(750)
                            .setFastestInterval(750)
                            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                            .build()
                    )
                    .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {
                // Activate the LocationComponent with options
                this.activateLocationComponent(locationComponentActivationOptions)
                // Enable to make the LocationComponent visible
                this.isLocationComponentEnabled = true
                // Set the LocationComponent's camera mode
                this.cameraMode = cameraMode
                // Set the LocationComponent's render mode
                this.renderMode = renderMode
                // Set listeners
                this.addOnLocationClickListener(this@LocationTracker)
                this.addOnCameraTrackingChangedListener(this@LocationTracker)
                getUserLangAndLatAnimate()
            }

            initLocationEngine()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }


    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this)

        val request =
            LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()

        locationEngine.requestLocationUpdates(request, callback, mainLooper)
        locationEngine.getLastLocation(callback)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(
            this, "user_location_permission_explanation",
            Toast.LENGTH_LONG
        ).show(); }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapView.getMapAsync(this)
            mapboxMap?.getStyle { style ->
                enableLocationComponent(style)
            }
        } else {
            Toast.makeText(
                this, "user_location_permission_not_granted",
                Toast.LENGTH_LONG
            ).show()
        }
        permissionsManager.requestLocationPermissions(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Listen to and use a tap on the LocationComponent
     */
    override fun onLocationComponentClick() {
        Toast.makeText(this, "clicked_on_location_component", Toast.LENGTH_LONG).show()
    }

    override fun onCameraTrackingChanged(currentMode: Int) {
        this.cameraMode = currentMode
        when (currentMode) {
            CameraMode.NONE ->
                button_location_tracking.text = getString(R.string.none)
            CameraMode.NONE_COMPASS ->
                button_location_tracking.text = getString(R.string.none_compass)
            CameraMode.NONE_GPS ->
                button_location_tracking.text = getString(R.string.none_gps)
            CameraMode.TRACKING ->
                button_location_tracking.text = getString(R.string.tracking)
            CameraMode.TRACKING_COMPASS ->
                button_location_tracking.text = getString(R.string.tracking_compass)
            CameraMode.TRACKING_GPS ->
                button_location_tracking.text = getString(R.string.tracking_gps)
            CameraMode.TRACKING_GPS_NORTH ->
                button_location_tracking.text = getString(R.string.tracking_gps_north)
        }
    }

    override fun onCameraTrackingDismissed() {
        button_location_tracking.text = getString(R.string.none)
    }


    /**
     * Adjust the LocationComponent's image to one of the preset options.
     *
     * @param mode desired normal (small blue circle laid on top of larger white dot),
     * compass (arrow point representing the phone's bearing), or
     * GPS (blue arrow within a white circle).
     */
    private fun setRendererMode(@RenderMode.Mode mode: Int) {
        renderMode = mode
        mapboxMap.locationComponent.renderMode = mode
        when (mode) {
            RenderMode.NORMAL -> button_location_mode.text = getString(R.string.normal)
            RenderMode.COMPASS -> button_location_mode.text = getString(R.string.compass)
            RenderMode.GPS -> button_location_mode.text = getString(R.string.gps)
        }
    }

    private fun showModeListDialog() {
        val modes: MutableList<String> = ArrayList()
        modes.add(getString(R.string.normal))
        modes.add(getString(R.string.compass))
        modes.add(getString(R.string.gps))

        val profileAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, modes
        )
        val listPopup = ListPopupWindow(this)
        listPopup.setAdapter(profileAdapter)
        listPopup.anchorView = button_location_mode
        listPopup.setOnItemClickListener { _, _, position, _ ->
            val selectedMode = modes[position]
            button_location_mode.text = selectedMode
            when {
                selectedMode.contentEquals(getString(R.string.normal)) -> setRendererMode(RenderMode.NORMAL)
                selectedMode.contentEquals(getString(R.string.compass)) -> setRendererMode(
                    RenderMode.COMPASS
                )
                selectedMode.contentEquals(getString(R.string.gps)) -> setRendererMode(RenderMode.GPS)
            }
            listPopup.dismiss()
        }
        listPopup.show()
    }


    private fun showTrackingListDialog() {
        val trackingTypes: MutableList<String> = ArrayList()
        trackingTypes.add(getString(R.string.none))
        trackingTypes.add(getString(R.string.none_compass))
        trackingTypes.add(getString(R.string.none_gps))
        trackingTypes.add(getString(R.string.tracking))
        trackingTypes.add(getString(R.string.tracking_compass))
        trackingTypes.add(getString(R.string.tracking_gps))
        trackingTypes.add(getString(R.string.tracking_gps_north))

        val profileAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, trackingTypes
        )

        val listPopup = ListPopupWindow(this)
        listPopup.setAdapter(profileAdapter)
        listPopup.anchorView = button_location_tracking
        listPopup.setOnItemClickListener { _, _, position, _ ->
            val selectedTrackingType = trackingTypes[position]
            button_location_tracking.text = selectedTrackingType
            when {
                selectedTrackingType.contentEquals(getString(R.string.none)) ->
                    setCameraTrackingMode(CameraMode.NONE)
                selectedTrackingType.contentEquals(getString(R.string.none_compass)) ->
                    setCameraTrackingMode(CameraMode.NONE_COMPASS)
                selectedTrackingType.contentEquals(getString(R.string.none_gps)) ->
                    setCameraTrackingMode(CameraMode.NONE_GPS)
                selectedTrackingType.contentEquals(getString(R.string.tracking)) ->
                    setCameraTrackingMode(CameraMode.TRACKING)
                selectedTrackingType.contentEquals(getString(R.string.tracking_compass)) ->
                    setCameraTrackingMode(CameraMode.TRACKING_COMPASS)
                selectedTrackingType.contentEquals(getString(R.string.tracking_gps)) ->
                    setCameraTrackingMode(CameraMode.TRACKING_GPS)
                selectedTrackingType.contentEquals(getString(R.string.tracking_gps_north)) ->
                    setCameraTrackingMode(CameraMode.TRACKING_GPS_NORTH)
            }
            listPopup.dismiss()
        }
        listPopup.show()

    }

    private fun setCameraTrackingMode(@CameraMode.Mode mode: Int) {
        mapboxMap.locationComponent.setCameraMode(
            mode,
            object : OnLocationCameraTransitionListener {
                override fun onLocationCameraTransitionFinished(cameraMode: Int) {
                    if (mode != CameraMode.NONE) {
                        mapboxMap.locationComponent.zoomWhileTracking(15.0, 750, object :
                            MapboxMap.CancelableCallback {
                            override fun onFinish() {
                                mapboxMap.locationComponent.tiltWhileTracking(45.0)
                            }

                            override fun onCancel() {}
                        })
                    } else {
                        mapboxMap.easeCamera(CameraUpdateFactory.tiltTo(0.0))
                    }
                }
                override fun onLocationCameraTransitionCanceled(cameraMode: Int) {}
            })
    }

    private fun setModeButtonListeners() {
        button_location_mode.setOnClickListener {
            showModeListDialog()
        }
        button_location_tracking.setOnClickListener {
            showTrackingListDialog()
        }

        fabSearch.setOnClickListener {
            mapboxMap?.getStyle { style ->
                enableLocationComponent(style)
            }
        }
    }

    private fun getUserLangAndLatAnimate() {
        // Animate camera

        val position = CameraPosition.Builder()
            .target(
                LatLng(
                    mapboxMap.locationComponent.lastKnownLocation!!.latitude,
                    mapboxMap.locationComponent.lastKnownLocation!!.longitude
                )
            ) // Sets the new camera position
            .zoom(17.0) // Sets the zoom
            .bearing(180.0) // Rotate the camera
            .tilt(30.0) // Set the camera tilt
            .build() // Creates a CameraPosition from the builder

        mapboxMap.animateCamera(
            CameraUpdateFactory
                .newCameraPosition(position), 4000
        )
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    @SuppressLint("MissingPermission")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)

        // Save LocationComponent-related settings to use once device rotation is finished
        outState.putInt(SAVED_STATE_CAMERA, cameraMode)
        outState.putInt(SAVED_STATE_RENDER, renderMode)
        outState.putParcelable(SAVED_STATE_LOCATION, mapboxMap.locationComponent.lastKnownLocation)

    }

    override fun onDestroy() {
        super.onDestroy()
        // Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback)
        }
        mapView.onDestroy()

    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


}
