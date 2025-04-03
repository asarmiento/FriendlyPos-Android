package com.friendlypos.login.modelo

import com.google.gson.Gson
import okhttp3.ResponseBody
import java.io.IOException

/**
 * Created by DelvoM on 25/09/2017.
 */
class UserError(var error: String, var message: String) {
    companion object {
        fun fromResponseBody(responseBody: ResponseBody): UserError? {
            val gson: Gson = Gson()
            try {
                return gson.fromJson<UserError>(responseBody.string(), UserError::class.java)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }
    }
}
