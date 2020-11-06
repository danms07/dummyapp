package com.hms.example.dummyapplication.ui

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hms.example.dummyapplication.R
import com.hms.example.dummyapplication.utils.GPS
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import com.huawei.hms.maps.model.PointOfInterest
//import com.huawei.hms.maps.model.PolygonOptions
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.DetailSearchRequest
import com.huawei.hms.site.api.model.DetailSearchResponse
import com.huawei.hms.site.api.model.SearchStatus
import com.huawei.hms.site.api.model.Site
import kotlinx.android.synthetic.main.fragment_map.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class MapFragment : Fragment(), OnMapReadyCallback, View.OnClickListener, GPS.OnGPSEventListener,
    HuaweiMap.OnPoiClickListener, HuaweiMap.OnMarkerDragListener {



    private lateinit var mMapView: MapView
    //private lateinit var search: SearchFragment
    private lateinit var hMap: HuaweiMap
    private lateinit var gps: GPS
    private val TAG="MapFragment"
    private val currentLocation:LatLng=LatLng(19.0,-99.0)
    private val LOCATION_REQUEST=100
    private val KEY="CV6vKDxoaSXKhlopIDAKuRANlut2oSNt66X9V69qtRLcbAhiQ8e8j1I/x3SZsjqmcnQM6vE9+KQVTH+myk9gNrBjTjXE"
    private val API_KEY=URLEncoder.encode(KEY, StandardCharsets.UTF_8.name())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.setApiKey("CV6vKDxoaSXKhlopIDAKuRANlut2oSNt66X9V69qtRLcbAhiQ8e8j1I/x3SZsjqmcnQM6vE9+KQVTH+myk9gNrBjTjXE")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(null)
        mapView.getMapAsync(this)
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
            //hMap.isMyLocationEnabled=true
            hMap.uiSettings.isMyLocationButtonEnabled=true
            if(arguments!=null){
                val latitude=arguments?.getDouble("lat",0.0)
                val longitude=arguments?.getDouble("lon",0.0)
                val location=LatLng(latitude!!,longitude!!)
                val update=CameraUpdateFactory.newLatLngZoom(location, 10.0f)
                hMap.clear()
                hMap.animateCamera(update)
                //hMap.setOnMarkerDragListener(this)
            } else navigateToLocation(currentLocation)

        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        //mapView.onDestroy()
        super.onDestroy()

    }

    override fun onPause() {
        mapView.onPause()

        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
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
                navigateToLocation(currentLocation)
            }else gps.startLocationsRequest()
        } else requestLocationPermissions()
    }

    private fun navigateToLocation(location: LatLng, zoom: Float=16.0f) {
        val update=CameraUpdateFactory.newLatLngZoom(location, zoom)
        hMap.clear()
        hMap.animateCamera(update)
        val marker=MarkerOptions()
            .title("You are here")
            .position(location)
        hMap.addMarker(marker)
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
        val backgroundLocation= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            PackageManager.PERMISSION_GRANTED
        }
        return location==PackageManager.PERMISSION_GRANTED&&backgroundLocation==PackageManager.PERMISSION_GRANTED
    }

    override fun onResolutionRequired(e: Exception) {
        //This callback is triggered if the user
        // hasn't gave location permissions to the HMSCore app
    }

    override fun onLastKnownLocation(lat: Double, lon: Double) {
        currentLocation.latitude=lat
        currentLocation.longitude=lon
        //

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
        val id =poi.placeId
        val searchService = SearchServiceFactory.create(requireContext(),
            API_KEY)
        val request = DetailSearchRequest()
        request.siteId = id
        // Create a search result listener.
        val resultListener =
            object : SearchResultListener<DetailSearchResponse?> {
                // Return search results upon a successful search.
                override fun onSearchResult(result: DetailSearchResponse?) {
                    var site: Site?=null
                    if (result == null || result.site.also { site = it } == null) {
                        return
                    }
                    loadDialog(site!!)
                    //Log.e("SITE","${}")
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

    private fun loadDialog(site: Site) {
        val builder=AlertDialog.Builder(requireContext())
        builder.setTitle(site.name)
        builder.setMessage(site.formatAddress)
        builder.setPositiveButton(R.string.ok){dialog, which ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun onMarkerDragEnd(p0: Marker?) {
        TODO("Not yet implemented")
        
    }

    override fun onMarkerDragStart(p0: Marker?) {
        TODO("Not yet implemented")
    }

    override fun onMarkerDrag(p0: Marker?) {
        TODO("Not yet implemented")
    }
}