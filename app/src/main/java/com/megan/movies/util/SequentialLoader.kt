package com.megan.movies.util

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SequentialLoader(
    private val scope: CoroutineScope,
    private val onProgress: (section: String, status: LoadStatus) -> Unit
) {
    enum class LoadStatus { LOADING, SUCCESS, ERROR }
    
    private var isCancelled = false
    
    suspend fun loadSequentially(sections: List<Section>) {
        for (section in sections) {
            if (isCancelled) break
            
            onProgress(section.name, LoadStatus.LOADING)
            
            try {
                withTimeout(5000L) {
                    section.loader()
                }
                onProgress(section.name, LoadStatus.SUCCESS)
            } catch (e: TimeoutCancellationException) {
                onProgress(section.name, LoadStatus.ERROR)
            } catch (e: Exception) {
                onProgress(section.name, LoadStatus.ERROR)
            }
            
            // Small delay between calls to avoid rate limiting
            delay(200L)
        }
    }
    
    fun cancel() {
        isCancelled = true
    }
    
    data class Section(
        val name: String,
        val loader: suspend () -> Unit
    )
}
