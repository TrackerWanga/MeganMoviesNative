package com.megan.movies.util

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File

object ImageLoader {
    
    private val options = RequestOptions()
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(ColorDrawable(0xFF1a2232.toInt()))
        .error(ColorDrawable(0xFFe50914.toInt()))
    
    fun load(
        url: String?,
        imageView: ImageView,
        width: Int = 400,
        height: Int = 600
    ) {
        if (url.isNullOrEmpty()) {
            imageView.setImageDrawable(ColorDrawable(0xFFe50914.toInt()))
            return
        }
        
        Glide.with(imageView.context)
            .load(url)
            .apply(options)
            .override(width, height)
            .into(imageView)
    }
    
    fun preload(urls: List<String>) {
        if (urls.isEmpty()) return
        // Glide handles caching automatically, no need for explicit preload
    }
    
    fun clearMemoryCache(context: Context) {
        Glide.get(context).clearMemory()
    }
    
    fun clearDiskCache(context: Context) {
        Thread {
            Glide.get(context).clearDiskCache()
        }.start()
    }
    
    fun getDiskCacheSize(context: Context): Long {
        val cacheDir = File(context.cacheDir, "image_cache")
        return if (cacheDir.exists()) {
            cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        } else 0L
    }
}
