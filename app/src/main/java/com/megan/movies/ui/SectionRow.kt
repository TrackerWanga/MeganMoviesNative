package com.megan.movies.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.megan.movies.R
import com.megan.movies.MovieAdapter
import com.megan.movies.api.Movie
import com.megan.movies.util.ShimmerAdapter

class SectionRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    
    private val titleText: TextView
    private val shimmerRecycler: RecyclerView
    private val movieRecycler: RecyclerView
    private val errorText: TextView
    private val retryButton: View
    
    private var onMovieClick: ((Movie) -> Unit)? = null
    
    init {
        LayoutInflater.from(context).inflate(R.layout.view_section_row, this, true)
        titleText = findViewById(R.id.sectionTitle)
        shimmerRecycler = findViewById(R.id.shimmerRecycler)
        movieRecycler = findViewById(R.id.movieRecycler)
        errorText = findViewById(R.id.errorText)
        retryButton = findViewById(R.id.retryButton)
        
        shimmerRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        shimmerRecycler.adapter = ShimmerAdapter(6)
        
        movieRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }
    
    fun setTitle(title: String) {
        titleText.text = title
    }
    
    fun showShimmer() {
        shimmerRecycler.visibility = View.VISIBLE
        movieRecycler.visibility = View.GONE
        errorText.visibility = View.GONE
        retryButton.visibility = View.GONE
    }
    
    fun showMovies(movies: List<Movie>, onClick: (Movie) -> Unit) {
        shimmerRecycler.visibility = View.GONE
        movieRecycler.visibility = View.VISIBLE
        errorText.visibility = View.GONE
        retryButton.visibility = View.GONE
        
        onMovieClick = onClick
        val adapter = MovieAdapter { movie ->
            onMovieClick?.invoke(movie)
        }
        adapter.submitList(movies)
        movieRecycler.adapter = adapter
    }
    
    fun showError(onRetry: () -> Unit) {
        shimmerRecycler.visibility = View.GONE
        movieRecycler.visibility = View.GONE
        errorText.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
        
        retryButton.setOnClickListener { onRetry() }
    }
}
