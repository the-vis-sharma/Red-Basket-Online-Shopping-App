package com.skylark.redbasket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private TypeWriter tagLine;
    private ImageView logo;
    private ImageView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        logo = findViewById(R.id.ic_logo);
        name = findViewById(R.id.ic_name);
        tagLine = findViewById(R.id.tag_line);
        tagLine.animateText("* MRP से कम");

        Animation animationL = AnimationUtils.loadAnimation(this, R.anim.swipe_left);
        logo.startAnimation(animationL);

        Animation animationR = AnimationUtils.loadAnimation(this, R.anim.swipe_right);
        name.startAnimation(animationR);

        final SharedPreferences pref = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        final String isLogin = pref.getString("isLogin", "false");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(isLogin.equals("true")) {
                    final String defaultDate = new Date().toString();
                    final Date lastLoginTime = new Date(pref.getString("lastLoginTime", defaultDate));
                    if(getDifference(lastLoginTime, new Date()) <= 30) {
                        intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    }
                    else {
                        intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    }
                }
                else {
                    intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 3*1000);

    }

    public int getDifference(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = different / daysInMilli;
        return (int) elapsedDays;
    }
}
