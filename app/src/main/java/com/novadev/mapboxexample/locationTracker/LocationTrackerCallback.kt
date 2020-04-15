package com.novadev.mapboxexample.locationTracker

import android.widget.Toast
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import java.lang.ref.WeakReference

class LocationTrackerCallback constructor(activity: LocationTracker?) :
    LocationEngineCallback<LocationEngineResult?> {
    private val activityWeakReference: WeakReference<LocationTracker?>?

    /**
     * The LocationEngineCallback interface's method which fires when the device's location has changed.
     *
     * @param result the LocationEngineResult object which has the last known location within it.
     */
    override fun onSuccess(result: LocationEngineResult?) {
        val activity = activityWeakReference?.get()
        if (activity != null) {
            val location = result!!.lastLocation ?: return
            // Create a Toast which displays the new location's coordinates
            Toast.makeText(activity, "New location ${location.latitude}," +
                    " ${location.longitude}", Toast.LENGTH_SHORT).show()

            // Pass the new location to the Maps SDK's LocationComponent
            if (result.lastLocation != null) {
                activity.mapboxMap.locationComponent
                    .forceLocationUpdate(location)
            }
        }
    }

    /**
     * The LocationEngineCallback interface's method which fires when the device's location can't be captured
     *
     * @param exception the exception message
     */
    override fun onFailure(exception: Exception) {
        val activity: LocationTracker? = activityWeakReference?.get()
        if (activity != null) {
            Toast.makeText(activity, exception.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }
    init {
        activityWeakReference = WeakReference(activity)
    }

}