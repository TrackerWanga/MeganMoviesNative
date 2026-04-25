package com.meganmovies.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.meganmovies.app.api.ApiService
import com.meganmovies.app.api.Banner
import com.meganmovies.app.api.Movie
import com.meganmovies.app.ui.HeroSlider
import com.meganmovies.app.ui.SimpleBannerAdapter
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var currentPage = 0
    private var banners = listOf<Banner>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_full)
        
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
                
                setupDots(dotsContainer, banners.size)
                
                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        currentPage = position
                        titleText.text = banners.getOrNull(position)?.title ?: ""
                        updateDots(dotsContainer, banners.size, position)
                    }
                })
                
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
                Toast.makeText(this@MainActivity, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupDots(container: LinearLayout, count: Int) {
        container.removeAllViews()
        for (i in 0 until count) {
            val dot = View(this).apply {
                val size = (10 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(4, 0, 4, 0)
                }
                setBackgroundResource(if (i == 0) R.drawable.dot_active else R.drawable.dot_inactive)
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
