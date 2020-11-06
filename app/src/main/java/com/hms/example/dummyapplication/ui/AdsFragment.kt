package com.hms.example.dummyapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hms.example.dummyapplication.R
import com.hms.example.dummyapplication.utils.MyAdListener
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import kotlinx.android.synthetic.main.fragment_ads.view.*

class AdsFragment : Fragment(){



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ads, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        loadBannerAd()
    }

    fun loadBannerAd(){
        //view?.setVisibility(View.GONE)
        val bannerView=view?.banner
        bannerView?.adId = getString(R.string.ad_id)
        val adParam = AdParam.Builder().build()
        bannerView?.adListener= MyAdListener(requireContext(),"Banner Ad")
        bannerView?.loadAd(adParam)
    }
}