package com.meganmovies.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.meganmovies.app.api.Movie

class MovieAdapter(
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.VH>() {
    
    private var movies = listOf<Movie>()
    
    fun submitList(list: List<Movie>) { movies = list; notifyDataSetChanged() }
    
    override fun onCreateViewHolder(parent: ViewGroup, vt: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false))
    }
    
    override fun onBindViewHolder(h: VH, p: Int) {
        val m = movies[p]
        h.title.text = m.title
        h.year.text = m.year?.toString() ?: ""
        h.rating.text = if (m.rating != null) "⭐ ${m.rating}" else ""
        Glide.with(h.itemView).load(m.poster).centerCrop().into(h.poster)
        h.itemView.setOnClickListener { onItemClick(m) }
    }
    
    override fun getItemCount() = movies.size
    
    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val poster: ImageView = v.findViewById(R.id.moviePoster)
        val title: TextView = v.findViewById(R.id.movieTitle)
        val year: TextView = v.findViewById(R.id.movieYear)
        val rating: TextView = v.findViewById(R.id.movieRating)
    }
}
