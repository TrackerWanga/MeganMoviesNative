package com.megan.movies.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.megan.movies.R

class ShimmerAdapter(private val itemCount: Int = 6) :
    RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shimmer, parent, false)
        return ShimmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) {
        holder.startShimmer()
    }

    override fun getItemCount(): Int = itemCount

    class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val shimmerPoster = itemView.findViewById<View>(R.id.shimmerPoster)
        private val shimmerTitle = itemView.findViewById<View>(R.id.shimmerTitle)
        private val shimmerYear = itemView.findViewById<View>(R.id.shimmerYear)

        fun startShimmer() {
            val anim = AnimationUtils.loadAnimation(itemView.context, R.anim.shimmer)
            shimmerPoster.startAnimation(anim)
            shimmerTitle.startAnimation(anim)
            shimmerYear.startAnimation(anim)
        }
    }
}
