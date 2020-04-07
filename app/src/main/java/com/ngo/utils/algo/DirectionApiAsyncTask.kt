package com.ngo.utils.algo

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.ngo.ui.generalpublic.view.AsyncResponse
import com.ngo.utils.PreferenceHandler
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class DirectionApiAsyncTask(context: Context, latitude: String, longitude: String,var asyncResponse: AsyncResponse) :
    AsyncTask<URL, Integer, String>() {
    var context = context
    var latitude = latitude
    var longitude = longitude
    var resultDistance: String = ""

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: URL?): String {
        val latitude1 = PreferenceHandler.readString(context, PreferenceHandler.LATITUDE, "")
        val longitude1 = PreferenceHandler.readString(context, PreferenceHandler.LONGITUDE, "")
        var mUrlConnection: HttpURLConnection? = null
        var mJsonResults = StringBuilder();
        try {
            var urlFinal =
                "https://maps.googleapis.com/maps/api/directions/json?origin=" + latitude1 + "," + longitude1 + "&destination=" + latitude + "," + longitude + "&key=AIzaSyDfCcAJOo_cqeIYwKggFlv152B5wVr6iso";
            var sb = urlFinal

            var url = URL(sb)
            mUrlConnection = url.openConnection() as HttpURLConnection
            val r = BufferedReader(InputStreamReader(mUrlConnection.getInputStream()))
            var line: String? = null
            while (r.readLine().also({ line = it }) != null) {
                mJsonResults.append(line!!).append('\n')
            }

            try {
                var jsonObject = JSONObject(mJsonResults.toString())
                var array = jsonObject.getJSONArray("routes")
                var routes = array.getJSONObject(0)
                var legs = routes.getJSONArray("legs")
                var steps = legs.getJSONObject(0)
                var distance = steps.getJSONObject("distance")
                resultDistance = distance.get("text").toString()
                Log.i("Distance", distance.toString())
                // dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]", ""));

            } catch (e: JSONException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        } catch (e: MalformedURLException) {
            System.out.println("Error processing Distance Matrix API URL")
            //return null

        } catch (e: IOException) {
            System.out.println("Error connecting to Distance Matrix")
            //return null;
        } finally {
            if (mUrlConnection != null) {
                mUrlConnection.disconnect()
            }
        }
        return resultDistance
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        asyncResponse.processFinish(result);
    }
}