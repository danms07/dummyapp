package com.hms.example.dummyapplication.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hms.example.dummyapplication.R
import com.hms.example.dummyapplication.utils.GPS
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.PointOfInterest
import com.huawei.hms.maps.model.PolygonOptions
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.DetailSearchRequest
import com.huawei.hms.site.api.model.DetailSearchResponse
import com.huawei.hms.site.api.model.SearchStatus
import com.huawei.hms.site.api.model.Site
import java.net.URLEncoder


class MapFragment : Fragment(), OnMapReadyCallback, View.OnClickListener, GPS.OnGPSEventListener,
    HuaweiMap.OnPoiClickListener {

    private lateinit var hMap: HuaweiMap

    private lateinit var mMapView: MapView
    private lateinit var fab:FloatingActionButton
    private lateinit var gps: GPS
    private val TAG="MapFragment"
    private val currentLocation:LatLng=LatLng(19.0,-99.0)
    private val LOCATION_REQUEST=100


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView=view.findViewById(R.id.mapView)
        mMapView.onCreate(null)
        mMapView.getMapAsync(this)
        fab=view.findViewById(R.id.fab)
        fab.setOnClickListener(this)

        if(checkLocationPermissions()){
            setupGPS()
        }else{
            requestLocationPermissions()
        }

    }

    override fun onMapReady(map: HuaweiMap?) {
        Log.d(TAG, "onMapReady: ")
        if (map != null) {
            hMap = map
            hMap.setOnPoiClickListener(this)
            val options= PolygonOptions()
            val p1= LatLng(19.0,-99.0)
            val p2= LatLng(19.4261064,-99.1285141)
            val p3= LatLng(19.3565288,-99.0988653)
            val array= arrayOf(p1,p2,p3)
            options.addAll(array.asIterable())

            hMap.addPolygon(options)
        }
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onPause() {
        mMapView.onPause()

        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(gps.isStarted){
            gps.removeLocationUpdatesWithCallback()
        }
    }

    override fun onClick(v: View?) {
        if(checkLocationPermissions()){
            if(gps.isStarted){
                val update=CameraUpdateFactory.newLatLngZoom(currentLocation, 3.0f)
                hMap.animateCamera(update)
            }else gps.startLocationsRequest()
        } else requestLocationPermissions()
    }

    private fun requestLocationPermissions() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION),LOCATION_REQUEST)
    }

    private fun setupGPS() {
        gps= GPS(requireContext())
        gps.gpsEventListener=this
        gps.startLocationsRequest()
    }

    private fun checkLocationPermissions(): Boolean {
        val location:Int =ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) or ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
        val backgroundLocation=ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if(location==PackageManager.PERMISSION_GRANTED&&backgroundLocation==PackageManager.PERMISSION_GRANTED) return true
        return false
    }

    override fun onResolutionRequired(e: Exception) {
        TODO("Not yet implemented")
    }

    override fun onLastKnownLocation(lat: Double, lon: Double) {
        currentLocation.latitude=lat
        currentLocation.longitude=lon
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(checkLocationPermissions()){
            setupGPS()
        }
    }

    override fun onDetach() {
        super.onDetach()
        if(gps.isStarted){
            gps.removeLocationUpdatesWithCallback()
        }
    }

    override fun onPoiClick(poi: PointOfInterest) {
        val latLng=poi.latLng
        val id =poi.placeId
        val searchService = SearchServiceFactory.create(requireContext(),
            URLEncoder.encode("CV6vKDxoaSXKhlopIDAKuRANlut2oSNt66X9V69qtRLcbAhiQ8e8j1I/x3SZsjqmcnQM6vE9+KQVTH+myk9gNrBjTjXE", "UTF-8") )
        val request = DetailSearchRequest()
        request.setSiteId(id)
        // Create a search result listener.

        // Create a search result listener.
        val resultListener =
            object : SearchResultListener<DetailSearchResponse?> {
                // Return search results upon a successful search.
                override fun onSearchResult(result: DetailSearchResponse?) {
                    var site: Site?=null
                    if (result == null || result.site.also { site = it } == null) {
                        return
                    }
                    Log.e("SITE","${site?.formatAddress}")
                }

                // Return the result code and description upon a search exception.
                override fun onSearchError(status: SearchStatus) {
                    Log.e(
                        "TAG",
                        "Error : " + status.errorCode + " " + status.errorMessage
                    )
                }
            }
        searchService.detailSearch(request, resultListener)
    }
}