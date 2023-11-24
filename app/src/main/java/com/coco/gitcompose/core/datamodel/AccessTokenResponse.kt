package com.coco.gitcompose.core.datamodel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccessTokenAppResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "expires_in") val expiredIn: Long = 0,
    @Json(name = "refresh_token") val refreshToken: String = "",
    @Json(name = "refresh_token_expires_in") val refreshTokenExpiredIn: Long = 0,
)