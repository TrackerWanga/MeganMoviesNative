package com.meganmovies.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.meganmovies.app.R
import com.meganmovies.app.api.Banner

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
        
        Glide.with(holder.itemView.context)
            .load(banner.image?.url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(holder.imageView)
    }
    
    override fun getItemCount(): Int = banners.size
    
    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.bannerImage)
        val titleText: TextView = view.findViewById(R.id.bannerTitleText)
    }
}
