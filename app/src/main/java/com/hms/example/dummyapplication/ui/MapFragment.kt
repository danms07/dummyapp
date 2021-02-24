package com.hms.example.dummyapplication.ui

import android.Manifest
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
import com.hms.example.dummyapplication.R
import com.hms.example.dummyapplication.databinding.MapBinding
import com.hms.example.dummyapplication.utils.LocationTracker
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import com.huawei.hms.maps.model.PointOfInterest
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.DetailSearchRequest
import com.huawei.hms.site.api.model.DetailSearchResponse
import com.huawei.hms.site.api.model.SearchStatus
import com.huawei.hms.site.api.model.Site
import java.lang.StringBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class MapFragment : Fragment(), OnMapReadyCallback, View.OnClickListener, LocationTracker.OnGPSEventListener,
    HuaweiMap.OnPoiClickListener, HuaweiMap.OnMarkerDragListener {
    //private lateinit var search: SearchFragment

    companion object{
        private const val TAG="MapFragment"
        private const val LOCATION_REQUEST=100
    }
    private val lastKnownLocation:LatLng=LatLng(19.0,-99.0)
    private var hMap: HuaweiMap?=null
    private var locationTracker: LocationTracker?=null
    private var mapView:MapView?=null
    private var apiKey:String=""
    private lateinit var mapBinding:MapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiKey= AGConnectServicesConfig
            .fromContext(requireContext())
            .getString("client/api_key")
        MapsInitializer.setApiKey(apiKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mapBinding= MapBinding.inflate(inflater,container,false)
        return mapBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView=mapBinding.mapView
        mapView?.apply {
            onCreate(null)
            getMapAsync(this@MapFragment)
        }
        mapBinding.fab.setOnClickListener(this)
        if(checkLocationPermissions()){
            setupLocationTracker()
        }else{
            requestLocationPermissions()
        }

    }

    override fun onMapReady(map: HuaweiMap?) {

            hMap = map
            hMap?.apply {
                setOnPoiClickListener(this@MapFragment)
                //hMap.isMyLocationEnabled=true
                uiSettings.isMyLocationButtonEnabled=true
                if(arguments!=null){
                    val latitude=arguments?.getDouble("lat",0.0)
                    val longitude=arguments?.getDouble("lon",0.0)
                    val location=LatLng(latitude!!,longitude!!)
                    val update=CameraUpdateFactory.newLatLngZoom(location, 10.0f)
                    clear()
                    animateCamera(update)
            }else navigateToLocation(lastKnownLocation)

                //hMap.setOnMarkerDragListener(this)
            }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        super.onDestroy()

    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationTracker?.apply {
            if(isStarted){
                removeLocationUpdates()
            }
        }

    }

    override fun onClick(v: View?) {
        if(checkLocationPermissions()){
            locationTracker?.apply {
                if(isStarted){
                    navigateToLocation(lastKnownLocation)
                }else startLocationsRequest()
            }
        } else requestLocationPermissions()
    }

    private fun navigateToLocation(location: LatLng, zoom: Float=16.0f) {
        val update=CameraUpdateFactory.newLatLngZoom(location, zoom)
        hMap?.apply {
            clear()
            animateCamera(update)
            val marker=MarkerOptions()
                .title("You are here")
                .position(location)
            addMarker(marker)
        }
    }

    private fun requestLocationPermissions() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION),LOCATION_REQUEST)
    }

    private fun setupLocationTracker() {
        if(locationTracker==null){
            locationTracker= LocationTracker(requireContext())
        }
        locationTracker?.apply {
            gpsEventListener=this@MapFragment
            startLocationsRequest()
        }
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
        lastKnownLocation.latitude=lat
        lastKnownLocation.longitude=lon
        //

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(checkLocationPermissions()){
            setupLocationTracker()
        }
    }

    override fun onDetach() {
        super.onDetach()
        locationTracker?.apply {
            if(isStarted){
                removeLocationUpdates()
            }
        }

    }

    override fun onPoiClick(poi: PointOfInterest) {
        val encodedKey=URLEncoder.encode(apiKey, StandardCharsets.UTF_8.name())
        val searchService = SearchServiceFactory.create(requireContext(),
            encodedKey)


        val request = DetailSearchRequest().apply { siteId=poi.placeId }
        // Create a search result listener.
        val resultListener =
            object : SearchResultListener<DetailSearchResponse?> {
                // Return search results upon a successful search.
                override fun onSearchResult(result: DetailSearchResponse?) {
                    var site: Site?=null
                    if (result == null || result.site.also { site = it } == null) {
                        return
                    }
                    site?.let {
                        Log.e(TAG,"Website: ${it.poi.websiteUrl}\t Phone:${it.poi.phone} \t Rating:${it.poi.rating}")
                        loadDialog(it) }
                }

                // Return the result code and description upon a search exception.
                override fun onSearchError(status: SearchStatus) {
                    Log.e(
                        TAG,
                        "Error : " + status.errorCode + " " + status.errorMessage
                    )
                }
            }
        searchService.detailSearch(request, resultListener)
    }

    private fun loadDialog(site: Site) {
        val tempMessage="${site.formatAddress}\n\n${site.poi.phone}\n\n${site.poi.websiteUrl}"
        val message=tempMessage.replace("\n\nnull","")
        AlertDialog.Builder(requireContext()).apply {
            setTitle(site.name)
            setMessage(message)
            setPositiveButton(R.string.ok){dialog,_ ->
                dialog.dismiss()
            }
        }.create().show()
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