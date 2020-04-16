package com.novadev.mapboxexample.marker

import android.graphics.Bitmap
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.annotations.BubbleLayout
import com.novadev.mapboxexample.R
import java.lang.ref.WeakReference
import java.util.*

/**
 * AsyncTask to generate Bitmap from Views to be used as iconImage in a SymbolLayer.
 *
 *
 * Call be optionally be called to update the underlying data source after execution.
 *
 *
 *
 * Generating Views on background thread since we are not going to be adding them to the view hierarchy.
 *
 */
class GenerateViewIconTask (
    activity: InfoWindowSymbolLayerActivity,
    private val refreshSource: Boolean = false
) : AsyncTask<FeatureCollection?, Void?, HashMap<String, Bitmap>?>() {

    private val activityRef: WeakReference<InfoWindowSymbolLayerActivity> = WeakReference(activity)


    override fun onPostExecute(bitmapHashMap: HashMap<String, Bitmap>?) {
        super.onPostExecute(bitmapHashMap)
        val activity = activityRef.get()
        if (activity != null && bitmapHashMap != null) {
            activity.setImageGenResults(bitmapHashMap)
            if (refreshSource) {
                activity.refreshSource()
            }
        }
        Toast.makeText(activity, "R.string.tap_on_marker_instruction", Toast.LENGTH_SHORT).show()
    }

    override fun doInBackground(vararg params: FeatureCollection?): HashMap<String, Bitmap>? {
        val activity = activityRef.get()
        return if (activity != null) {
            val imagesMap =
                HashMap<String, Bitmap>()
            val inflater = LayoutInflater.from(activity)
            val featureCollection = params[0]

//            for (feature in featureCollection?.features()!!) {
//                val bubbleLayout =
//                    inflater.inflate(R.layout.marker_info, null) as BubbleLayout
//                val name =
//                    feature.getStringProperty(InfoWindowSymbolLayerActivity.PROPERTY_NAME)
//                val titleTextView =
//                    bubbleLayout.findViewById<TextView>(R.id.tvTitleMarker)
//                titleTextView.text = name
//                val style =
//                    feature.getStringProperty(InfoWindowSymbolLayerActivity.PROPERTY_CAPITAL)
//                val descriptionTextView =
//                    bubbleLayout.findViewById<TextView>(R.id.tvSubtitlemarker)
//                descriptionTextView.text = String.format(
//                    activity.getString(R.string.capital),
//                    style
//                )
//                val measureSpec = View.MeasureSpec.makeMeasureSpec(
//                    0,
//                    View.MeasureSpec.UNSPECIFIED
//                )
//                bubbleLayout.measure(measureSpec, measureSpec)
//                val measuredWidth = bubbleLayout.measuredWidth.toFloat()
//                bubbleLayout.arrowPosition = measuredWidth / 2 - 5
//                val bitmap = SymbolGenerator.generate(bubbleLayout)
//                imagesMap[name] = bitmap
//                viewMap[name] = bubbleLayout
//            }
            imagesMap
        } else {
            null
        }
    }

}