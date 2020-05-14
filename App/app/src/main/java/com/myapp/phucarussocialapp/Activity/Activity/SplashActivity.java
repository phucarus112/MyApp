package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.myapp.phucarussocialapp.Activity.Object.CheckInternet;
import com.myapp.phucarussocialapp.R;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar ;
    public static CheckInternet checkInternet;
    SeekBar sb;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkInternet= new CheckInternet(SplashActivity.this);
        String res = null;
        res = getIntent().getStringExtra("receive");
        if(res == null) setUpComponent();
        else {
            sharedPreferences = getSharedPreferences("notificationSP",MODE_PRIVATE);
            editor = sharedPreferences.edit();
            if(isContainAllNum(res) == false) //noti tin nhắn
                editor.putString("typeNoti","1");
            else //noti thong báo
                editor.putString("typeNoti","2");
            editor.putString("data",res);
            editor.commit();
            Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean isContainAllNum(String res) {
        for(int i=0;i<res.length();i++){
            if(!(res.charAt(i) >= '0' && res.charAt(i) <= '9')){
                return false;
            }
        }
        return true;
    }

    private void setUpComponent() {
        progressBar = (ProgressBar)findViewById(R.id.splash_progressbar);
        sb= (SeekBar) findViewById(R.id.load_sb);
        CountDownTimer countDownTimer  = new CountDownTimer(3000, 300) {
            @Override
            public void onTick(long millisUntilFinished) {
                int plus = sb.getProgress();
                plus+=10;
                sb.setProgress(plus);
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                progressBar.setVisibility(  View.VISIBLE);
                startActivity(intent);
                finish();
            }
        };
        countDownTimer.start();
    }
}
