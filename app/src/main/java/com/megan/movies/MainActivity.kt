package com.megan.movies

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.megan.movies.api.ApiService
import com.megan.movies.api.Movie
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var movieAdapter: MovieAdapter
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupRecyclerView()
        loadMovies()
        loadBanners()
    }
    
    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.movieList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        
        movieAdapter = MovieAdapter { movie ->
            Toast.makeText(this, "Clicked: ${movie.title}", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to movie detail screen
        }
        recyclerView.adapter = movieAdapter
    }
    
    private fun loadMovies() {
        scope.launch {
            try {
                val movies = ApiService.fetchTrending()
                movieAdapter.submitList(movies)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Failed to load movies", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun loadBanners() {
        scope.launch {
            try {
                val banners = ApiService.fetchBanners()
                // TODO: Set up banner carousel
                Toast.makeText(this@MainActivity, "Loaded ${banners.size} banners", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Silent fail for banners
            }
        }
    }
    
    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
