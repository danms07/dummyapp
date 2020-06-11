package com.hms.example.dummyapplication.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hms.example.dummyapplication.R
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.PolygonOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var hMap: HuaweiMap

    private lateinit var mMapView: MapView
    private val TAG="MapFragment"


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
    }

    override fun onMapReady(map: HuaweiMap?) {
        Log.d(TAG, "onMapReady: ")
        if (map != null) {
            hMap = map
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


}