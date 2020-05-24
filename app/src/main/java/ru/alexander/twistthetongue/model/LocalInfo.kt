package ru.alexander.twistthetongue.model

import com.google.auth.oauth2.AccessToken

object LocalInfo {
    const val AUTH_KEY_GOOGLE = ""
    const val PREF_ACCESS_TOKEN_VALUE = "access_token_value"
    const val PREF_ACCESS_TOKEN_EXPIRATION_TIME = "access_token_expiration_time"
    var token : AccessToken? = null
}