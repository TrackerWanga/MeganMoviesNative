package com.megan.movies.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.megan.movies.R
import com.megan.movies.api.Banner
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
        holder.titleText.text = banner.title ?: ""
        
        // Load image with Picasso - with error handling
        if (!banner.image?.url.isNullOrEmpty()) {
            Picasso.get()
                .load(banner.image?.url)
                .fit()
                .centerCrop()
                .into(holder.imageView)
        } else {
            holder.imageView.setBackgroundColor(0xFFe50914.toInt())
        }
    }
    
    override fun getItemCount(): Int = banners.size
    
    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.bannerImage)
        val titleText: TextView = view.findViewById(R.id.bannerTitleText)
    }
}
