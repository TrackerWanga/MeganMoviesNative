package com.megan.movies

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.megan.movies.api.ApiService
import com.megan.movies.api.Movie
import com.megan.movies.ui.SectionRow
import com.megan.movies.ui.SimpleBannerAdapter
import com.megan.movies.util.SequentialLoader
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var currentPage = 0
    private var banners = listOf<com.megan.movies.api.Banner>()
    
    // Section rows
    private lateinit var trendingRow: SectionRow
    private lateinit var actionRow: SectionRow
    private lateinit var horrorRow: SectionRow
    private lateinit var romanceRow: SectionRow
    private lateinit var adventureRow: SectionRow
    private lateinit var animeRow: SectionRow
    private lateinit var kdramaRow: SectionRow
    private lateinit var cdramaRow: SectionRow
    private lateinit var turkishRow: SectionRow
    private lateinit var sadramaRow: SectionRow
    private lateinit var blackshowsRow: SectionRow
    private lateinit var hotshortsRow: SectionRow
    private lateinit var smartstartRow: SectionRow
    private lateinit var upcomingRow: SectionRow
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_full)
        
        initSectionRows()
        showAllShimmer()
        startSequentialLoading()
    }
    
    private fun initSectionRows() {
        trendingRow = findViewById(R.id.trendingRow)
        actionRow = findViewById(R.id.actionRow)
        horrorRow = findViewById(R.id.horrorRow)
        romanceRow = findViewById(R.id.romanceRow)
        adventureRow = findViewById(R.id.adventureRow)
        animeRow = findViewById(R.id.animeRow)
        kdramaRow = findViewById(R.id.kdramaRow)
        cdramaRow = findViewById(R.id.cdramaRow)
        turkishRow = findViewById(R.id.turkishRow)
        sadramaRow = findViewById(R.id.sadramaRow)
        blackshowsRow = findViewById(R.id.blackshowsRow)
        hotshortsRow = findViewById(R.id.hotshortsRow)
        smartstartRow = findViewById(R.id.smartstartRow)
        upcomingRow = findViewById(R.id.upcomingRow)
        
        trendingRow.setTitle("🔥 Trending Now")
        actionRow.setTitle("💥 Action Movies")
        horrorRow.setTitle("😱 Horror Movies")
        romanceRow.setTitle("💕 Romance Movies")
        adventureRow.setTitle("🗺️ Adventure Movies")
        animeRow.setTitle("🎌 Anime")
        kdramaRow.setTitle("🇰🇷 K-Drama")
        cdramaRow.setTitle("🇨🇳 C-Drama")
        turkishRow.setTitle("🇹🇷 Turkish Drama")
        sadramaRow.setTitle("🇿🇦 South African Drama")
        blackshowsRow.setTitle("✊🏾 Black Shows")
        hotshortsRow.setTitle("🔥 Hot Short TV")
        smartstartRow.setTitle("🧸 Smart Start")
        upcomingRow.setTitle("📅 Coming Soon")
    }
    
    private fun showAllShimmer() {
        listOf(trendingRow, actionRow, horrorRow, romanceRow, adventureRow,
            animeRow, kdramaRow, cdramaRow, turkishRow, sadramaRow,
            blackshowsRow, hotshortsRow, smartstartRow, upcomingRow
        ).forEach { it.showShimmer() }
    }
    
    private fun startSequentialLoading() {
        val loader = SequentialLoader(scope) { section, status ->
            // Update section row based on status
            when (section) {
                "trending" -> handleSectionStatus(trendingRow, status)
                "action" -> handleSectionStatus(actionRow, status)
                "horror" -> handleSectionStatus(horrorRow, status)
                "romance" -> handleSectionStatus(romanceRow, status)
                "adventure" -> handleSectionStatus(adventureRow, status)
                "anime" -> handleSectionStatus(animeRow, status)
                "kdrama" -> handleSectionStatus(kdramaRow, status)
                "cdrama" -> handleSectionStatus(cdramaRow, status)
                "turkish" -> handleSectionStatus(turkishRow, status)
                "sadrama" -> handleSectionStatus(sadramaRow, status)
                "blackshows" -> handleSectionStatus(blackshowsRow, status)
                "hotshorts" -> handleSectionStatus(hotshortsRow, status)
                "smartstart" -> handleSectionStatus(smartstartRow, status)
                "upcoming" -> handleSectionStatus(upcomingRow, status)
            }
        }
        
        val sections = listOf(
            SequentialLoader.Section("banners") {
                banners = ApiService.fetchBanners()
                withContext(Dispatchers.Main) {
                    setupBannerSlider()
                }
            },
            SequentialLoader.Section("trending") {
                val data = ApiService.fetchTrending()
                withContext(Dispatchers.Main) {
                    trendingRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("action") {
                val data = ApiService.fetchActionMovies()
                withContext(Dispatchers.Main) {
                    actionRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("horror") {
                val data = ApiService.fetchHorrorMovies()
                withContext(Dispatchers.Main) {
                    horrorRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("romance") {
                val data = ApiService.fetchRomanceMovies()
                withContext(Dispatchers.Main) {
                    romanceRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("adventure") {
                val data = ApiService.fetchAdventureMovies()
                withContext(Dispatchers.Main) {
                    adventureRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("anime") {
                val data = ApiService.fetchAnime()
                withContext(Dispatchers.Main) {
                    animeRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("kdrama") {
                val data = ApiService.fetchKDrama()
                withContext(Dispatchers.Main) {
                    kdramaRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("cdrama") {
                val data = ApiService.fetchCDrama()
                withContext(Dispatchers.Main) {
                    cdramaRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("turkish") {
                val data = ApiService.fetchTurkishDrama()
                withContext(Dispatchers.Main) {
                    turkishRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("sadrama") {
                val data = ApiService.fetchSADrama()
                withContext(Dispatchers.Main) {
                    sadramaRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("blackshows") {
                val data = ApiService.fetchBlackShows()
                withContext(Dispatchers.Main) {
                    blackshowsRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("hotshorts") {
                val data = ApiService.fetchHotShorts()
                withContext(Dispatchers.Main) {
                    hotshortsRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("smartstart") {
                val data = ApiService.fetchSmartStart()
                withContext(Dispatchers.Main) {
                    smartstartRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            SequentialLoader.Section("upcoming") {
                val data = ApiService.fetchUpcoming()
                withContext(Dispatchers.Main) {
                    upcomingRow.showMovies(data) { movie ->
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        
        scope.launch {
            loader.loadSequentially(sections)
        }
    }
    
    private fun handleSectionStatus(row: SectionRow, status: SequentialLoader.LoadStatus) {
        when (status) {
            SequentialLoader.LoadStatus.SUCCESS -> {} // Already handled in section
            SequentialLoader.LoadStatus.ERROR -> row.showError {
                // Retry logic here
            }
            SequentialLoader.LoadStatus.LOADING -> row.showShimmer()
        }
    }
    
    private fun setupBannerSlider() {
        val viewPager = findViewById<ViewPager2>(R.id.heroViewPager)
        val dotsContainer = findViewById<LinearLayout>(R.id.dotsContainer)
        val titleText = findViewById<TextView>(R.id.bannerTitle)
        
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
    }
    
    private fun setupDots(container: LinearLayout, count: Int) {
        container.removeAllViews()
        for (i in 0 until count) {
            val dot = android.view.View(this).apply {
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
