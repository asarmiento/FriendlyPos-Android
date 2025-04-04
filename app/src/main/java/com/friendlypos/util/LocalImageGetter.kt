package com.friendlypos.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import androidx.core.content.ContextCompat

/**
 * Una implementación simple de ImageGetter para cargar imágenes desde recursos locales
 */
class LocalImageGetter(private val context: Context? = null) : Html.ImageGetter {
    override fun getDrawable(source: String?): Drawable? {
        if (context == null || source == null) return null
        
        try {
            // Intentar cargar la imagen desde los recursos
            val resourceId = context.resources.getIdentifier(
                source, "drawable", context.packageName
            )
            
            if (resourceId > 0) {
                val drawable = ContextCompat.getDrawable(context, resourceId)
                drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                return drawable
            }
        } catch (e: Exception) {
            // Manejar error silenciosamente
        }
        
        return null
    }
} 