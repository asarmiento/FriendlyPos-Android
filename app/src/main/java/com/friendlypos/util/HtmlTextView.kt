package com.friendlypos.util

import android.content.Context
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import android.text.Spanned

/**
 * Una implementación simple de un TextView que puede mostrar HTML
 * Reemplazo para la biblioteca org.sufficientlysecure.htmltextview.HtmlTextView
 */
class HtmlTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        // Permitir que los enlaces sean clickeables
        movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Establece el texto HTML
     */
    fun setHtmlFromString(html: String, imageGetter: Any? = null) {
        val spanned: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, null, null)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(html)
        }
        
        text = spanned
    }
} 