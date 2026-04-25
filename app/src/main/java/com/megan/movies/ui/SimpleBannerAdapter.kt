package com.megan.movies.ui

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.megan.movies.R
import com.megan.movies.api.Banner

class SimpleBannerAdapter(
    private val banners: List<Banner>
) : RecyclerView.Adapter<SimpleBannerAdapter.BannerViewHolder>() {
    
    private val options = RequestOptions()
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(ColorDrawable(0xFF1a2232.toInt()))
        .error(ColorDrawable(0xFFe50914.toInt()))
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner_simple, parent, false)
        return BannerViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]
        holder.titleText.text = banner.title ?: ""
        
        val imageUrl = banner.image?.url
        
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .apply(options)
            .into(holder.imageView)
    }
    
    override fun getItemCount(): Int = banners.size
    
    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.bannerImage)
        val titleText: TextView = view.findViewById(R.id.bannerTitleText)
    }
}
