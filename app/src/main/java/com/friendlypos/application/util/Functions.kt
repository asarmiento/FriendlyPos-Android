package com.friendlypos.application.util

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import android.webkit.URLUtil
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.friendlypos.R
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Functions {
    @JvmStatic
    fun CreateMessage(context: Context, Tittle: String?, Message: String?) {
        val builder1 = AlertDialog.Builder(context)
        builder1.setTitle(Tittle)
        builder1.setMessage(Message)
        builder1.setCancelable(true)
        builder1.setNeutralButton(
            android.R.string.ok
        ) { dialog, id -> dialog.cancel() }

        val alert11 = builder1.create()
        alert11.show()
    }

    @JvmStatic
    fun createNotification(context: Context, id: Int, title: String?, message: String?) {
        val mNotifyManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.ic_launcher)
        } else {
            mBuilder.setSmallIcon(R.drawable.ic_launcher)
        }
        mBuilder.setTicker(message)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        mBuilder.setContentTitle(title)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentText(message)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.mipmap.ic_launcher
                )
            )
        mNotifyManager.notify(id, mBuilder.build())
    }

    @JvmStatic
    fun checkURL(input: CharSequence): Boolean {
        if (TextUtils.isEmpty(input)) {
            return false
        }
        val URL_PATTERN = Patterns.WEB_URL
        var isURL = URL_PATTERN.matcher(input).matches()
        if (!isURL) {
            val urlString = input.toString() + ""
            if (URLUtil.isValidUrl(urlString)) {
                try {
                    URL(urlString)
                    isURL = true
                } catch (e: Exception) {
                }
            }
        }
        return isURL
    }

    @JvmStatic
    fun paddigTabs(tabs: Long): String {
        var format = ""
        var c = 0
        while (c < tabs) {
            format += "\t"
            c += 2
        }
        return format
    }

    @JvmStatic
    fun getVesionNaveCode(context: Context): String {
        var send = ""
        try {
            send = "Version: " + context.packageManager.getPackageInfo(
                context.packageName,
                0
            ).versionName
        } catch (e: Exception) {
        }
        return send
    }

    @JvmStatic
    val date: String
        get() {
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val cal = Calendar.getInstance()
            return dateFormat.format(cal.time)
        }

    @JvmStatic
    val dateConsecutivo: String
        get() {
            val dateFormat: DateFormat = SimpleDateFormat("ddMMyy")
            val cal = Calendar.getInstance()
            return dateFormat.format(cal.time)
        }

    @JvmStatic
    fun get24Time(): String {
        val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
        val cal = Calendar.getInstance()
        return (dateFormat.format(cal.time)) //16:00:22
    }


    @JvmStatic
    fun doubleToString1(number: Double): String {
        return String.format(Locale.US, "%1$,.2f", number)
    }
}
