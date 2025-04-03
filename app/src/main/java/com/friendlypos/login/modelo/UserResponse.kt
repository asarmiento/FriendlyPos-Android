package com.friendlypos.login.modelo

/**
 * Created by DelvoM on 25/09/2017.
 */
class UserResponse(
    @JvmField var token_type: String,
    @JvmField var expires_in: String,
    @JvmField var access_token: String,
    @JvmField var refresh_token: String
)
