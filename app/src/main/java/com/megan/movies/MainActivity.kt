package com.megan.movies

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
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
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    
    // Section rows
    private lateinit var trendingRow: SectionRowWrapper
    private lateinit var popularMoviesRow: SectionRowWrapper
    private lateinit var popularSeriesRow: SectionRowWrapper
    private lateinit var actionRow: SectionRowWrapper
    private lateinit var horrorRow: SectionRowWrapper
    private lateinit var romanceRow: SectionRowWrapper
    private lateinit var adventureRow: SectionRowWrapper
    private lateinit var animeRow: SectionRowWrapper
    private lateinit var kdramaRow: SectionRowWrapper
    private lateinit var upcomingRow: SectionRowWrapper
    
    // Search
    private lateinit var searchInput: EditText
    private lateinit var searchContainer: LinearLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_full)
        
        initViews()
        setupSearch()
        startSequentialLoading()
    }
    
    private fun initViews() {
        // Hero slider
        val viewPager = findViewById<ViewPager2>(R.id.heroViewPager)
        val dotsContainer = findViewById<LinearLayout>(R.id.dotsContainer)
        val bannerTitle = findViewById<TextView>(R.id.bannerTitle)
        val bannerType = findViewById<TextView>(R.id.bannerType)
        heroSlider = HeroSlider(viewPager, dotsContainer, bannerTitle, bannerType) { banner ->
            openBannerDetail(banner)
        }
        
        // Search
        searchInput = findViewById(R.id.searchInput)
        searchContainer = findViewById(R.id.searchContainer)
        
        // Section rows
        trendingRow = SectionRowWrapper(findViewById(R.id.trendingRow), "Trending Now")
        popularMoviesRow = SectionRowWrapper(findViewById(R.id.popularMoviesRow), "Popular Movies")
        popularSeriesRow = SectionRowWrapper(findViewById(R.id.popularSeriesRow), "Popular Series")
        actionRow = SectionRowWrapper(findViewById(R.id.actionRow), "Action Movies")
        horrorRow = SectionRowWrapper(findViewById(R.id.horrorRow), "Horror Movies")
        romanceRow = SectionRowWrapper(findViewById(R.id.romanceRow), "Romance Movies")
        adventureRow = SectionRowWrapper(findViewById(R.id.adventureRow), "Adventure Movies")
        animeRow = SectionRowWrapper(findViewById(R.id.animeRow), "Anime")
        kdramaRow = SectionRowWrapper(findViewById(R.id.kdramaRow), "K-Drama")
        upcomingRow = SectionRowWrapper(findViewById(R.id.upcomingRow), "Upcoming")
        
        // Footer
        findViewById<TextView>(R.id.footerLink)?.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://movies.megan.qzz.io")))
        }
    }
    
    private fun setupSearch() {
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = searchInput.text.toString().trim()
                if (query.isNotBlank()) performSearch(query)
                true
            } else false
        }
    }
    
    private fun startSequentialLoading() {
        // Show shimmer in all rows first
        listOf(trendingRow, popularMoviesRow, popularSeriesRow, actionRow,
            horrorRow, romanceRow, adventureRow, animeRow, kdramaRow, upcomingRow
        ).forEach { it.showShimmer() }
        
        scope.launch {
            // 1. Load banners first (most important)
            loadBanners()
            
            // 2. Load trending
            loadSection("trending") {
                val data = ApiService.fetchTrending()
                trendingRow.showMovies(data.filter { it.type == "movie" || it.type == "tv" }) { openMovieDetail(it) }
                // Also split into popular movies/series
                popularMoviesRow.showMovies(data.filter { it.type == "movie" }.take(10)) { openMovieDetail(it) }
                popularSeriesRow.showMovies(data.filter { it.type == "tv" }.take(10)) { openMovieDetail(it) }
            }
            
            // 3. Load action
            loadSection("action") {
                val data = ApiService.fetchActionMovies()
                actionRow.showMovies(data) { openMovieDetail(it) }
            }
            
            // 4. Load horror
            loadSection("horror") {
                val data = ApiService.fetchHorrorMovies()
                horrorRow.showMovies(data) { openMovieDetail(it) }
            }
            
            // 5. Load romance
            loadSection("romance") {
                val data = ApiService.fetchRomanceMovies()
                romanceRow.showMovies(data) { openMovieDetail(it) }
            }
            
            // 6. Load adventure
            loadSection("adventure") {
                val data = ApiService.fetchAdventureMovies()
                adventureRow.showMovies(data) { openMovieDetail(it) }
            }
            
            // 7. Load anime
            loadSection("anime") {
                val data = ApiService.fetchAnime()
                animeRow.showMovies(data) { openMovieDetail(it) }
            }
            
            // 8. Load K-Drama
            loadSection("kdrama") {
                val data = ApiService.fetchKDrama()
                kdramaRow.showMovies(data) { openMovieDetail(it) }
            }
            
            // 9. Load upcoming
            loadSection("upcoming") {
                val data = ApiService.fetchUpcoming()
                upcomingRow.showMovies(data) { openMovieDetail(it) }
            }
        }
    }
    
    private suspend fun loadBanners() {
        try {
            withTimeout(5000L) {
                val banners = ApiService.fetchBanners()
                withContext(Dispatchers.Main) {
                    heroSlider.setBanners(banners)
                }
            }
        } catch (e: Exception) {
            // Banners failed - that's OK, other sections will still load
        }
    }
    
    private fun loadSection(name: String, loader: suspend () -> Unit) {
        scope.launch {
            try {
                withTimeout(5000L) {
                    loader()
                }
            } catch (e: TimeoutCancellationException) {
                // Timeout - show error state
                showSectionError(name)
            } catch (e: Exception) {
                // Other error - show error state
                showSectionError(name)
            }
        }
    }
    
    private fun showSectionError(sectionName: String) {
        // Find the section row and show error
        val row = when (sectionName) {
            "trending" -> trendingRow
            "action" -> actionRow
            "horror" -> horrorRow
            "romance" -> romanceRow
            "adventure" -> adventureRow
            "anime" -> animeRow
            "kdrama" -> kdramaRow
            "upcoming" -> upcomingRow
            else -> null
        }
        row?.showError()
    }
    
    private fun performSearch(query: String) {
        scope.launch {
            try {
                withTimeout(5000L) {
                    val results = ApiService.searchMovies(query)
                    withContext(Dispatchers.Main) {
                        // Show search results in trending row
                        trendingRow.showMovies(results) { openMovieDetail(it) }
                        // Hide other rows
                        listOf(popularMoviesRow, popularSeriesRow, actionRow,
                            horrorRow, romanceRow, adventureRow, animeRow, kdramaRow, upcomingRow
                        ).forEach { it.hide() }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Search failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun openBannerDetail(banner: Banner) {
        // Navigate to detail page (to be built later)
        Toast.makeText(this, banner.title, Toast.LENGTH_SHORT).show()
    }
    
    private fun openMovieDetail(movie: Movie) {
        // Navigate to detail page (to be built later)  
        Toast.makeText(this, "${movie.title} (${movie.year})", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}

// Helper class to manage a section row
class SectionRowWrapper(
    private val container: View,
    private val title: String
) {
    private val titleView: TextView = container.findViewById(R.id.sectionTitle)
    private val recycler: RecyclerView = container.findViewById(R.id.movieRecycler)
    private val shimmer: RecyclerView = container.findViewById(R.id.shimmerRecycler)
    private val errorView: View = container.findViewById(R.id.errorLayout)
    
    init {
        titleView.text = title
        recycler.layoutManager = LinearLayoutManager(container.context, LinearLayoutManager.HORIZONTAL, false)
        shimmer.layoutManager = LinearLayoutManager(container.context, LinearLayoutManager.HORIZONTAL, false)
    }
    
    fun showShimmer() {
        recycler.visibility = View.GONE
        shimmer.visibility = View.VISIBLE
        errorView.visibility = View.GONE
    }
    
    fun showMovies(movies: List<Movie>, onClick: (Movie) -> Unit) {
        recycler.visibility = View.VISIBLE
        shimmer.visibility = View.GONE
        errorView.visibility = View.GONE
        
        val adapter = MovieAdapter(onClick)
        adapter.submitList(movies)
        recycler.adapter = adapter
    }
    
    fun showError() {
        recycler.visibility = View.GONE
        shimmer.visibility = View.GONE
        errorView.visibility = View.VISIBLE
    }
    
    fun hide() {
        container.visibility = View.GONE
    }
}
