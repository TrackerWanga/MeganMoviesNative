package com.megan.movies

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    
    private lateinit var movieAdapter: MovieAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupRecyclerView()
        loadMovies()
        
        Toast.makeText(this, "Welcome to Megan Movies!", Toast.LENGTH_SHORT).show()
    }
    
    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.movieList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        
        movieAdapter = MovieAdapter { movie ->
            Toast.makeText(this, "Clicked: ${movie.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = movieAdapter
    }
    
    private fun loadMovies() {
        val movies = listOf(
            Movie("The Boys", "2019", "8.6", "https://image.tmdb.org/t/p/w500/example.jpg"),
            Movie("Invincible", "2021", "8.7", "https://image.tmdb.org/t/p/w500/example.jpg"),
            Movie("Hoppers", "2026", "6.2", "https://image.tmdb.org/t/p/w500/example.jpg"),
            Movie("Project Hail Mary", "2026", "8.4", "https://image.tmdb.org/t/p/w500/example.jpg")
        )
        movieAdapter.submitList(movies)
    }
}
