package com.megan.movies.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors

object ImageLoader {
    
    private val executor = Executors.newFixedThreadPool(4)
    
    fun load(
        url: String?,
        imageView: ImageView,
        width: Int = 400,
        height: Int = 600,
        placeholder: Int = android.R.color.darker_gray,
        error: Int = android.R.color.holo_red_dark,
        onSuccess: (() -> Unit)? = null,
        onError: (() -> Unit)? = null
    ) {
        if (url.isNullOrEmpty()) {
            imageView.setBackgroundColor(0xFFe50914.toInt())
            onError?.invoke()
            return
        }
        
        // Show placeholder immediately
        imageView.setBackgroundResource(placeholder)
        
        Picasso.get()
            .load(url)
            .resize(width, height)
            .centerCrop()
            .placeholder(placeholder)
            .error(error)
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    imageView.setBackgroundResource(0)
                    onSuccess?.invoke()
                }
                
                override fun onError(e: Exception?) {
                    // Retry once with different size
                    retry(url, imageView, width / 2, height / 2, error, onError)
                }
            })
    }
    
    private fun retry(
        url: String,
        imageView: ImageView,
        width: Int,
        height: Int,
        error: Int,
        onError: (() -> Unit)?
    ) {
        executor.execute {
            try {
                val bitmap = Picasso.get()
                    .load(url)
                    .resize(width, height)
                    .centerCrop()
                    .get()
                
                imageView.post {
                    imageView.setImageBitmap(bitmap)
                    imageView.setBackgroundResource(0)
                }
            } catch (e: Exception) {
                imageView.post {
                    imageView.setBackgroundResource(error)
                    onError?.invoke()
                }
            }
        }
    }
    
    fun preload(urls: List<String>) {
        executor.execute {
            for (url in urls) {
                try {
                    Picasso.get().load(url).fetch()
                } catch (_: Exception) {}
            }
        }
    }
    
    fun clearMemoryCache() {
        Picasso.get().invalidate("memory")
    }
    
    fun clearDiskCache(context: Context) {
        executor.execute {
            try {
                val cacheDir = File(context.cacheDir, "picasso-cache")
                if (cacheDir.exists()) {
                    cacheDir.deleteRecursively()
                }
            } catch (_: IOException) {}
        }
    }
    
    fun getDiskCacheSize(context: Context): Long {
        val cacheDir = File(context.cacheDir, "picasso-cache")
        return if (cacheDir.exists()) {
            cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        } else 0L
    }
}
