package com.example.gravn.opengltest;


import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Mathiaspc on 17/05/2016.
 */
public class Advertisment
{
    private InterstitialAd mInterstitialAd;
    private LinearLayout ll;
    private AdRequest.Builder adRequest;
    private AdView bannerView;
    private Context context;
    public Advertisment(Context context)
    {
        this.context = context;
       /* ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        bannerView = new AdView(context);
        bannerView.setAdSize(AdSize.SMART_BANNER);
        bannerView.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        adRequest = new AdRequest.Builder();
        adRequest.addTestDevice("4ED34337D31D544858354D03C057D96B").build();
        ll.addView(bannerView);
        bannerView.loadAd(adRequest.build());*/
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //mInterstitialAd.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);


    }
    public void InterstitialAd()
    {
       /* if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(context, "Ad did not load", Toast.LENGTH_SHORT).show();
        }*/
        mInterstitialAd.show();
    }
    public void BannerAd()
    {
        //Log.i("Advertisment:",""+bannerView.isLoading()+"");
        bannerView.loadAd(adRequest.build());
    }
}
