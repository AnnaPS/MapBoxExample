package com.novadev.mapboxexample.marker

import android.content.Context
import android.os.AsyncTask
import com.mapbox.geojson.FeatureCollection
import java.lang.ref.WeakReference
import java.nio.charset.Charset

/**
 * AsyncTask to load data from the assets folder.
 */
class LoadGeoJsonDataTask(activity: InfoWindowSymbolLayerActivity) :
    AsyncTask<Void?, Void?, FeatureCollection?>() {
    private val activityRef: WeakReference<InfoWindowSymbolLayerActivity> = WeakReference(activity)

    override fun onPostExecute(featureCollection: FeatureCollection?) {
        super.onPostExecute(featureCollection)
        val activity = activityRef.get()
        if (featureCollection == null || activity == null) {
            return
        }
        // This example runs on the premise that each GeoJSON Feature has a "selected" property,
        // with a boolean value. If your data's Features don't have this boolean property,
        // add it to the FeatureCollection 's features with the following code:
        for (singleFeature in featureCollection.features()!!) {
            singleFeature.addBooleanProperty(
                InfoWindowSymbolLayerActivity.PROPERTY_SELECTED,
                false
            )
        }
        activity.setUpData(featureCollection)
        GenerateViewIconTask(activity).execute(featureCollection)
    }

    companion object {
        fun loadGeoJsonFromAsset(
            context: Context,
            filename: String?
        ): String {
            return try { // Load GeoJSON file from local asset folder
                val `is` = context.assets.open(filename!!)
                val size = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                `is`.close()
                String(buffer, Charset.forName("UTF-8"))
            } catch (exception: Exception) {
                throw RuntimeException(exception)
            }
        }
    }

    override fun doInBackground(vararg p0: Void?): FeatureCollection? {
        val activity = activityRef.get() ?: return null
        val geoJson =
            loadGeoJsonFromAsset(activity, "madridmujeres.geojson")
        return FeatureCollection.fromJson(geoJson)
    }

}