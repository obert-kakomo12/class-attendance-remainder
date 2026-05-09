package com.classtrack.classtrack.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.classtrack.classtrack.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.ivSplashLogo);
        TextView title = findViewById(R.id.tvSplashTitle);
        TextView subtitle = findViewById(R.id.tvSplashSubtitle);

        // Initial states
        logo.setAlpha(0f);
        title.setAlpha(0f);
        subtitle.setAlpha(0f);
        logo.setScaleX(0.5f);
        logo.setScaleY(0.5f);

        // Start 3D Flip and Fade Animation
        logo.animate()
                .alpha(1f)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    // Perform 3D Rotation (around Y-axis)
                    ObjectAnimator flip = ObjectAnimator.ofFloat(logo, "rotationY", 0f, 360f);
                    flip.setDuration(1500);
                    flip.setInterpolator(new AccelerateDecelerateInterpolator());
                    flip.start();

                    // Animate scale back to normal
                    logo.animate().scaleX(1.0f).scaleY(1.0f).setDuration(1000).start();

                    // Fade in text
                    title.animate().alpha(1f).translationY(-20f).setDuration(800).start();
                    subtitle.animate().alpha(1f).setStartDelay(400).setDuration(800).start();

                    flip.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            // Brief delay before moving to MainActivity
                            logo.postDelayed(() -> {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }, 500);
                        }
                    });
                }).start();
    }
}
