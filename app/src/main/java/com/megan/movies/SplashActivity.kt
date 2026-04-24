package com.megan.movies

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd

class SplashActivity : AppCompatActivity() {
    
    companion object {
        private const val SPLASH_SHOWN_KEY = "splash_shown"
        private const val SPLASH_DURATION = 3500L
    }
    
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        prefs = getSharedPreferences("megan_prefs", MODE_PRIVATE)
        
        val logoView = findViewById<ImageView>(R.id.splashLogo)
        val letterView = findViewById<TextView>(R.id.splashLetter)
        val titleView = findViewById<TextView>(R.id.splashTitle)
        val subtitleView = findViewById<TextView>(R.id.splashSubtitle)
        val progressBar = findViewById<ProgressBar>(R.id.splashProgress)
        
        // Start animations
        playSplashAnimation(logoView, letterView, titleView, subtitleView, progressBar)
        
        // Navigate after duration
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMain()
        }, SPLASH_DURATION)
    }
    
    private fun playSplashAnimation(
        logo: ImageView,
        letter: TextView,
        title: TextView,
        subtitle: TextView,
        progress: ProgressBar
    ) {
        // 1. Logo pulse animation (3 pulses)
        val pulseAnim = ObjectAnimator.ofFloat(logo, "scaleX", 1.0f, 1.15f, 1.0f, 1.1f, 1.0f, 1.05f, 1.0f).apply {
            duration = 2500
            interpolator = OvershootInterpolator(0.5f)
        }
        
        val pulseAnimY = ObjectAnimator.ofFloat(logo, "scaleY", 1.0f, 1.15f, 1.0f, 1.1f, 1.0f, 1.05f, 1.0f).apply {
            duration = 2500
            interpolator = OvershootInterpolator(0.5f)
        }
        
        // 2. Letter glow animation
        val glowAnim = ObjectAnimator.ofFloat(letter, "alpha", 0.8f, 1.0f).apply {
            duration = 800
            repeatCount = 3
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }
        
        // 3. Title slide up + fade in
        val titleAlpha = ObjectAnimator.ofFloat(title, "alpha", 0f, 1f).apply {
            duration = 600
            startDelay = 400
        }
        val titleSlide = ObjectAnimator.ofFloat(title, "translationY", 30f, 0f).apply {
            duration = 600
            startDelay = 400
            interpolator = OvershootInterpolator()
        }
        
        // 4. Subtitle fade in
        val subtitleAlpha = ObjectAnimator.ofFloat(subtitle, "alpha", 0f, 1f).apply {
            duration = 500
            startDelay = 800
        }
        
        // 5. Progress bar fill
        progress.max = 100
        val progressAnim = ValueAnimator.ofInt(0, 100).apply {
            duration = 3000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                progress.progress = animation.animatedValue as Int
            }
        }
        
        // Play all animations together
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            pulseAnim, pulseAnimY, glowAnim,
            titleAlpha, titleSlide, subtitleAlpha,
            progressAnim
        )
        animatorSet.start()
    }
    
    private fun navigateToMain() {
        prefs.edit().putBoolean(SPLASH_SHOWN_KEY, true).apply()
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
    
    override fun onBackPressed() {
        // Disable back press during splash
    }
}
