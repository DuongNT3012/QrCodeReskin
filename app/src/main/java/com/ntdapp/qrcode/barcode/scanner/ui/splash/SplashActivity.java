package com.ntdapp.qrcode.barcode.scanner.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ntdapp.qrcode.barcode.scanner.ui.home.HomeActivity;

import com.ntdapp.qrcode.barcode.scanner.R;

public class SplashActivity extends AppCompatActivity {

    /**
     * Constants
     */
    private final int SPLASH_DELAY = 3000;

    /**
     * Fields
     */
    private ImageView mImageViewLogo;
    private TextView mtvSplash;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setBackgroundDrawable(null);

        //firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //

        initializeViews();
        animateLogo();
        goToMainPage();
    }

    /**
     * This method initializes the views
     */
    private void initializeViews() {
        mImageViewLogo = findViewById(R.id.image_view_logo);
        mtvSplash = findViewById(R.id.tv_splash);
    }

    /**
     * This method takes user to the main page
     */
    private void goToMainPage() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        }, SPLASH_DELAY);
    }

    /**
     * This method animates the logo
     */
    private void animateLogo() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_without_duration);
        fadeInAnimation.setDuration(SPLASH_DELAY);

        mImageViewLogo.startAnimation(fadeInAnimation);
        mtvSplash.startAnimation(fadeInAnimation);
    }
}