package com.example.gravn.opengltest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class HighscoreActivity extends Activity
{
    private TextView tv;
    private AdRequest adRequest;
    private InterstitialAd mInterstitialAd;
    private String[] savedScores;
    private StringBuilder scoreBuild;
    private DBHandlerNew dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(0xFFFFFFFF,
                WindowManager.LayoutParams.FLAG_FULLSCREEN| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        //Load the ad so that it is ready to use
        mInterstitialAd = new InterstitialAd(this);
       // mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.setAdUnitId("ca-app-pub-2164798054231876/1460732941");
        adRequest = new AdRequest.Builder().addTestDevice("4ED34337D31D544858354D03C057D96B").build();
        mInterstitialAd.loadAd(adRequest);
        //Creates the banner ad
        AdView mAdView = (AdView)findViewById(R.id.adView);
        mAdView.loadAd(adRequest);
        tv = (TextView)findViewById(R.id.hsTxt);
        dbHandler = new DBHandlerNew(this,null,null,17);
        //Creates highscore
        CreateHighscore();
        PrintScores();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //Fullscreen ad when pressing the android devices' back button.
        if (mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
        this.finish();

    }
    public void PrintScores(){
        savedScores = dbHandler.ToString().split("//");
        scoreBuild = new StringBuilder("");
        for (int i = 0; i < savedScores.length;i++){
            scoreBuild.append(i+1+". "+savedScores[i]+"\n");
        }
        tv.setText(scoreBuild.toString());


    }
    public void CreateHighscore()
    {
        String playerName = MenuActivity.sp.getString("Name","");
        float playerPoints = MenuActivity.sp.getFloat("Points",0);
        if(playerPoints != 0){
            Highscore highscore = new Highscore(playerName,Float.toString(playerPoints));
            dbHandler.AddItem(highscore);
            Toast toast = Toast.makeText(this, "Your points was: "+playerPoints, Toast.LENGTH_LONG);
            toast.show();
            Player.getInstance().points = 0;
        }
    }
}
