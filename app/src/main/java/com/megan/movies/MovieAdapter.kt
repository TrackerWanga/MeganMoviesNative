package com.megan.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    
    private var movies = listOf<Movie>()
    
    fun submitList(list: List<Movie>) {
        movies = list
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }
    
    override fun getItemCount(): Int = movies.size
    
    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.moviePoster)
        private val title: TextView = itemView.findViewById(R.id.movieTitle)
        private val year: TextView = itemView.findViewById(R.id.movieYear)
        private val rating: TextView = itemView.findViewById(R.id.movieRating)
        
        fun bind(movie: Movie) {
            title.text = movie.title
            year.text = movie.year
            rating.text = "⭐ ${movie.rating}"
            
            // Placeholder color instead of actual image loading
            poster.setBackgroundColor(android.graphics.Color.parseColor("#e50914"))
            
            itemView.setOnClickListener {
                onItemClick(movie)
            }
        }
    }
}
