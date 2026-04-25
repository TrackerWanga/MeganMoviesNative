package com.megan.movies

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.megan.movies.api.ApiService
import com.megan.movies.ui.SimpleBannerAdapter
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var currentPage = 0
    private var banners = listOf<com.megan.movies.api.Banner>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_simple)
        
        loadBanners()
    }
    
    private fun loadBanners() {
        scope.launch {
            try {
                banners = ApiService.fetchBanners()
                
                val viewPager = findViewById<ViewPager2>(R.id.heroViewPager)
                val titleText = findViewById<TextView>(R.id.bannerTitle)
                val dotsContainer = findViewById<LinearLayout>(R.id.dotsContainer)
                
                viewPager.adapter = SimpleBannerAdapter(banners)
                titleText.text = banners.firstOrNull()?.title ?: ""
                
                // Setup dots
                setupDots(dotsContainer, banners.size)
                
                // Page change listener
                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        currentPage = position
                        titleText.text = banners.getOrNull(position)?.title ?: ""
                        updateDots(dotsContainer, banners.size, position)
                    }
                })
                
                // Auto-rotate
                if (banners.size > 1) {
                    val handler = android.os.Handler(android.os.Looper.getMainLooper())
                    val runnable = object : Runnable {
                        override fun run() {
                            currentPage = (currentPage + 1) % banners.size
                            viewPager.setCurrentItem(currentPage, true)
                            handler.postDelayed(this, 5000)
                        }
                    }
                    handler.postDelayed(runnable, 5000)
                }
                
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Failed to load banners: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupDots(container: LinearLayout, count: Int) {
        container.removeAllViews()
        for (i in 0 until count) {
            val dot = android.view.View(this).apply {
                val size = (10 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(4, 0, 4, 0)
                }
                setBackgroundResource(
                    if (i == 0) R.drawable.dot_active else R.drawable.dot_inactive
                )
            }
            container.addView(dot)
        }
    }
    
    private fun updateDots(container: LinearLayout, count: Int, current: Int) {
        for (i in 0 until container.childCount) {
            container.getChildAt(i).setBackgroundResource(
                if (i == current) R.drawable.dot_active else R.drawable.dot_inactive
            )
        }
    }
    
    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
