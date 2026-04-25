package com.meganmovies.app.ui

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
import com.bumptech.glide.Glide
import com.meganmovies.app.R
import com.meganmovies.app.api.Banner

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
    
    fun setBanners(list: List<Banner>) {
        banners = list
        viewPager.adapter = BannerPagerAdapter(banners) { onBannerClick(it) }
        setupDots()
        updateTitle(0)
        
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position; updateDots(); updateTitle(position)
            }
        })
        
        if (banners.size > 1) {
            handler.postDelayed(object : Runnable {
                override fun run() {
                    currentPage = (currentPage + 1) % banners.size
                    viewPager.setCurrentItem(currentPage, true)
                    handler.postDelayed(this, 5000)
                }
            }, 5000)
        }
    }
    
    private fun setupDots() {
        dotsContainer.removeAllViews()
        banners.indices.forEach { i ->
            val dot = View(dotsContainer.context).apply {
                val size = (10 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply { setMargins(4, 0, 4, 0) }
                setBackgroundResource(if (i == 0) R.drawable.dot_active else R.drawable.dot_inactive)
            }
            dotsContainer.addView(dot)
        }
    }
    
    private fun updateDots() {
        for (i in 0 until dotsContainer.childCount)
            dotsContainer.getChildAt(i).setBackgroundResource(
                if (i == currentPage) R.drawable.dot_active else R.drawable.dot_inactive)
    }
    
    private fun updateTitle(pos: Int) {
        banners.getOrNull(pos)?.let {
            titleText.text = it.title ?: ""
            typeText.text = if (it.type == "tv") "TV SERIES" else "MOVIE"
        }
    }
}

class BannerPagerAdapter(
    private val banners: List<Banner>,
    private val onClick: (Banner) -> Unit
) : RecyclerView.Adapter<BannerPagerAdapter.VH>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, vt: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_banner_simple, parent, false))
    }
    
    override fun onBindViewHolder(h: VH, p: Int) {
        val b = banners[p]
        Glide.with(h.itemView).load(b.image?.url).centerCrop().into(h.image)
        h.itemView.setOnClickListener { onClick(b) }
    }
    
    override fun getItemCount() = banners.size
    
    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val image: ImageView = v.findViewById(R.id.bannerImage)
    }
}
