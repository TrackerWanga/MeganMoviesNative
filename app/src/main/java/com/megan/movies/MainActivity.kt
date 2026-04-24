package com.megan.movies

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.megan.movies.api.ApiService
import com.megan.movies.api.Banner
import com.megan.movies.api.Movie
import com.megan.movies.ui.HeroSlider
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var heroSlider: HeroSlider
    private lateinit var trendingAdapter: MovieAdapter
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupHeroSlider()
        setupTrending()
        setupFooter()
        setupSearch()
        
        // Show shimmer immediately
        trendingAdapter.showShimmer()
        
        // Load data with retry
        loadAllSections()
    }
    
    private fun setupHeroSlider() {
        val viewPager = findViewById<ViewPager2>(R.id.heroViewPager)
        val dotsContainer = findViewById<LinearLayout>(R.id.dotsContainer)
        val titleText = findViewById<TextView>(R.id.bannerTitle)
        val typeText = findViewById<TextView>(R.id.bannerType)
        
        heroSlider = HeroSlider(viewPager, dotsContainer, titleText, typeText) { banner ->
            Toast.makeText(this, "Opening: ${banner.title}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupTrending() {
        val trendingRecycler = findViewById<RecyclerView>(R.id.trendingRecycler)
        trendingRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        trendingAdapter = MovieAdapter { movie ->
            Toast.makeText(this, "Clicked: ${movie.title}", Toast.LENGTH_SHORT).show()
        }
        trendingRecycler.adapter = trendingAdapter
    }
    
    private fun setupSearch() {
        val searchInput = findViewById<EditText>(R.id.searchInput)
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = searchInput.text.toString()
                if (query.isNotBlank()) {
                    searchMovies(query)
                }
                true
            } else false
        }
    }
    
    private fun setupFooter() {
        val footerLink = findViewById<TextView>(R.id.footerLink)
        footerLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://movies.megan.qzz.io"))
            startActivity(intent)
        }
    }
    
    private fun loadAllSections() {
        scope.launch {
            try {
                // Load banners
                val banners = ApiService.fetchBanners()
                heroSlider.setBanners(banners)
                
                // Load trending
                val trending = ApiService.fetchTrending()
                trendingAdapter.submitList(trending)
                
                // Preload next images for smooth scrolling
                val nextUrls = (trending + banners.mapNotNull { it.image?.url }).take(20)
                com.megan.movies.util.ImageLoader.preload(nextUrls)
                
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Retrying...", Toast.LENGTH_SHORT).show()
                // Retry once
                try {
                    delay(1000)
                    val banners = ApiService.fetchBanners()
                    heroSlider.setBanners(banners)
                    val trending = ApiService.fetchTrending()
                    trendingAdapter.submitList(trending)
                } catch (e2: Exception) {
                    Toast.makeText(this@MainActivity, "Failed to load content", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun searchMovies(query: String) {
        trendingAdapter.showShimmer()
        scope.launch {
            try {
                val results = ApiService.searchMovies(query)
                trendingAdapter.submitList(results)
                Toast.makeText(this@MainActivity, "Found ${results.size} results", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Search failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
