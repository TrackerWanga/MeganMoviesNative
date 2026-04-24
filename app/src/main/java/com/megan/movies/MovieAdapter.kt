package com.megan.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.megan.movies.api.Movie
import com.megan.movies.util.ImageLoader

class MovieAdapter(
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    companion object {
        private const val VIEW_TYPE_MOVIE = 0
        private const val VIEW_TYPE_SHIMMER = 1
    }
    
    private var movies = listOf<Movie>()
    private var isLoading = true
    
    fun submitList(list: List<Movie>) {
        movies = list
        isLoading = false
        notifyDataSetChanged()
    }
    
    fun showShimmer() {
        isLoading = true
        notifyDataSetChanged()
    }
    
    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_SHIMMER else VIEW_TYPE_MOVIE
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SHIMMER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_shimmer, parent, false)
                ShimmerViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_movie, parent, false)
                MovieViewHolder(view)
            }
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MovieViewHolder -> {
                if (position < movies.size) {
                    holder.bind(movies[position])
                }
            }
        }
    }
    
    override fun getItemCount(): Int = if (isLoading) 6 else movies.size
    
    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.moviePoster)
        private val title: TextView = itemView.findViewById(R.id.movieTitle)
        private val year: TextView = itemView.findViewById(R.id.movieYear)
        private val rating: TextView = itemView.findViewById(R.id.movieRating)
        
        fun bind(movie: Movie) {
            title.text = movie.title
            year.text = movie.year?.toString() ?: ""
            rating.text = if (movie.rating != null) "⭐ ${movie.rating}" else ""
            
            ImageLoader.load(
                url = movie.poster,
                imageView = poster,
                width = 300,
                height = 450,
                placeholder = android.R.color.darker_gray,
                error = 0xFFe50914.toInt(),
                onSuccess = { poster.setBackgroundResource(0) },
                onError = { poster.setBackgroundColor(0xFFe50914.toInt()) }
            )
            
            itemView.setOnClickListener { onItemClick(movie) }
        }
    }
    
    inner class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
