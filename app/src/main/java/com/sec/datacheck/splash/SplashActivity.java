package com.sec.datacheck.splash;

import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sec.datacheck.R;
import com.sec.datacheck.checkdata.model.PrefManager;
import com.sec.datacheck.checkdata.view.activities.map.MainActivity;
import com.sec.datacheck.checkdata.view.activities.map.MapActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            TextView desc_1 = findViewById(R.id.description_1);
            TextView desc_2 = findViewById(R.id.description_2);
            ImageView imageView = findViewById(R.id.splash_logo);

            Typeface nexaFontBold = Typeface.createFromAsset(this.getAssets(), "fonts/Nexa-Bold.otf");
            desc_1.setTypeface(nexaFontBold);
            desc_2.setTypeface(nexaFontBold);

            Animation imageAnimation = new TranslateAnimation(0, 0, -500, 0);
            imageAnimation.setDuration(1000);
            imageAnimation.setFillAfter(true);

            Animation animation = new TranslateAnimation(-1000, 0, 0, 0);
            animation.setDuration(1000);
            animation.setFillAfter(true);

            imageView.setAnimation(imageAnimation);
            desc_1.startAnimation(animation);
            desc_2.startAnimation(animation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(() -> {
            //If user not logged in before open login activity
            Intent intent;
//                if (isLoggedInBefore()) {
//                intent =new Intent(SplashActivity.this, MapActivity.class);
//                }else{
//                    intent =new Intent(SplashActivity.this, LoginActivity.class);
//                }
            intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }


    boolean isLoggedInBefore() {
        try {
            PrefManager prefManager = new PrefManager(this);

            String code = prefManager.readString(PrefManager.KEY_SURVEYOR_CODE);
            if (code != null && !code.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
