package com.megan.movies.ui

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.megan.movies.R
import com.megan.movies.api.Banner
import com.squareup.picasso.Picasso

class HeroSlider(
    private val viewPager: ViewPager2,
    private val dotsContainer: LinearLayout,
    private val titleText: TextView,
    private val typeText: TextView,
    private val onBannerClick: (Banner) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private var banners = listOf<Banner>()
    private var currentPage = 0
    
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            if (banners.isNotEmpty()) {
                currentPage = (currentPage + 1) % banners.size
                viewPager.setCurrentItem(currentPage, true)
                handler.postDelayed(this, 5000)
            }
        }
    }
    
    fun setBanners(list: List<Banner>) {
        banners = list
        viewPager.adapter = BannerAdapter(banners) { banner ->
            onBannerClick(banner)
        }
        setupDots()
        updateTitle(0)
        
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                updateDots()
                updateTitle(position)
            }
        })
        
        handler.removeCallbacks(autoScrollRunnable)
        if (banners.size > 1) {
            handler.postDelayed(autoScrollRunnable, 5000)
        }
    }
    
    private fun setupDots() {
        dotsContainer.removeAllViews()
        banners.forEachIndexed { index, _ ->
            val dot = View(dotsContainer.context).apply {
                val size = (12 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(
                        (4 * resources.displayMetrics.density).toInt(), 0,
                        (4 * resources.displayMetrics.density).toInt(), 0
                    )
                }
                setBackgroundResource(
                    if (index == 0) R.drawable.dot_active else R.drawable.dot_inactive
                )
            }
            dotsContainer.addView(dot)
        }
    }
    
    private fun updateDots() {
        for (i in 0 until dotsContainer.childCount) {
            val dot = dotsContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == currentPage) R.drawable.dot_active else R.drawable.dot_inactive
            )
        }
    }
    
    private fun updateTitle(position: Int) {
        if (position < banners.size) {
            val banner = banners[position]
            titleText.text = banner.title ?: ""
            typeText.text = if (banner.type == "tv") "TV SERIES" else "MOVIE"
        }
    }
    
    fun startAutoScroll() {
        handler.removeCallbacks(autoScrollRunnable)
        handler.postDelayed(autoScrollRunnable, 5000)
    }
    
    fun stopAutoScroll() {
        handler.removeCallbacks(autoScrollRunnable)
    }
}

class BannerAdapter(
    private val banners: List<Banner>,
    private val onClick: (Banner) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        if (position < banners.size) {
            holder.bind(banners[position], onClick)
        }
    }
    
    override fun getItemCount(): Int = banners.size
    
    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.bannerImage)
        
        fun bind(banner: Banner, onClick: (Banner) -> Unit) {
            val imageUrl = banner.image?.url
            
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.holo_red_dark)
                    .centerCrop()
                    .fit()
                    .into(image)
            } else {
                image.setBackgroundColor(0xFFe50914.toInt())
            }
            
            itemView.setOnClickListener { onClick(banner) }
        }
    }
}
