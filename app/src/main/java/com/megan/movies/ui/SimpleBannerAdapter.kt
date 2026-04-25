package com.megan.movies.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.megan.movies.R
import com.megan.movies.api.Banner
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class SimpleBannerAdapter(
    private val banners: List<Banner>
) : RecyclerView.Adapter<SimpleBannerAdapter.BannerViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner_simple, parent, false)
        return BannerViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]
        holder.titleText.text = banner.title ?: "Loading..."
        
        val imageUrl = banner.image?.url
        
        if (imageUrl.isNullOrEmpty()) {
            // No image URL - show red background
            holder.imageView.setBackgroundColor(Color.parseColor("#e50914"))
            holder.imageView.setImageDrawable(null)
        } else {
            // Set placeholder immediately
            holder.imageView.setBackgroundColor(Color.parseColor("#1a2232"))
            
            // Load image with Picasso
            Picasso.get()
                .load(imageUrl)
                .fit()
                .centerCrop()
                .into(holder.imageView, object : Callback {
                    override fun onSuccess() {
                        // Image loaded successfully
                        holder.imageView.setBackgroundColor(Color.TRANSPARENT)
                    }
                    
                    override fun onError(e: Exception?) {
                        // Image failed to load - show red fallback
                        holder.imageView.setBackgroundColor(Color.parseColor("#e50914"))
                        holder.imageView.setImageDrawable(null)
                    }
                })
        }
    }
    
    override fun getItemCount(): Int = banners.size
    
    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.bannerImage)
        val titleText: TextView = view.findViewById(R.id.bannerTitleText)
    }
}
