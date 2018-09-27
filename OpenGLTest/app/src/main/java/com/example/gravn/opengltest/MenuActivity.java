package com.example.gravn.opengltest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class MenuActivity extends Activity {

    private String playerName = "";
    private EditText et;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(0xFFFFFFFF,
                WindowManager.LayoutParams.FLAG_FULLSCREEN| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        et = (EditText)findViewById(R.id.nameTxt);
        sp = getSharedPreferences("MinPreference",MODE_PRIVATE);
        et.setText(sp.getString("Name",""));
        editor = sp.edit();
        editor.remove("Points");
        editor.apply();


    }
    public void OnClickBtn(View v){
        if(v.equals(findViewById(R.id.PlayBtn))){
            playerName = et.getText().toString();
            if (playerName.equals("")){
                Toast.makeText(getApplicationContext(), "Please enter a name", Toast.LENGTH_LONG).show();
            }else if(playerName.length()>10){
                Toast.makeText(getApplicationContext(), "You name can maximum be 10 characters", Toast.LENGTH_LONG).show();
            }
            else {
                editor.remove("Name");
                editor.remove("Points");
                editor.commit();
                playerName = et.getText().toString();
                editor.putString("Name",playerName);
                editor.commit();
                Intent openGame = new Intent(this,GameActivity.class);
                startActivity(openGame);
                //Toast.makeText(getApplicationContext(), "Your name is: "+playerName, Toast.LENGTH_LONG).show();
            }

        }
        if(v.equals(findViewById(R.id.HsBtn))){
            editor.remove("Points");
            editor.apply();
           Intent openHighscore = new Intent(this,HighscoreActivity.class);
           startActivity(openHighscore);
        }
        if (v.equals(findViewById(R.id.button))){
            Intent openHighscore = new Intent(this,HowToPlayer.class);
            startActivity(openHighscore);
        }
    }
}
