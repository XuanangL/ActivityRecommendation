package com.wintereye;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.animation.Animator.AnimatorListener;

public class Splash extends AppCompatActivity {

    private static boolean active = false;
    private Handler waitHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        LinearLayout mainLayout = findViewById(R.id.splash_main_layout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGuide();
            }
        });

        LinearLayout mainContainer = findViewById(R.id.main_container);
        ImageView logo = findViewById(R.id.logo);
        TextView name = findViewById(R.id.name);
        ImageView ellipse = findViewById(R.id.ellipse);

        // measure the current width of the TextView name and then set the width to 0
        name.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int nameTargetWidth = name.getMeasuredWidth();
        name.getLayoutParams().width = 0;

        waitHandler = new Handler();
        active = true;

        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        Animation scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        // restore the initial width pf the TextView name
        ValueAnimator nameAnimator = ValueAnimator.ofInt(name.getWidth(), nameTargetWidth);
        nameAnimator.setDuration(2000);
        nameAnimator.setInterpolator(new DecelerateInterpolator());
        nameAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                name.getLayoutParams().width = (int) animation.getAnimatedValue();
                name.requestLayout();
            }
        });
        nameAnimator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse)
            {
                // wait for 1 second then redirect to guide page
                waitHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (active) {
                            openGuide();
                        }
                    }
                }, 1000);
            }
        });



        // set animation listener on scale up animation
        scaleUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                nameAnimator.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        //AnimationSet splash = new AnimationSet(false); // false means don't share interpolators
        //splash.addAnimation(scaleUp);
        //splash.addAnimation(translateLeft);
        //logo.startAnimation(splash);

        ellipse.startAnimation(scaleDown);
        logo.startAnimation(scaleUp);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }
    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
    public void openGuide(){
        startActivity(new Intent(this,Guide.class));
    }
}