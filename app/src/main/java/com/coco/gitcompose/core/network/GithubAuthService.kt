package com.coco.gitcompose.core.network

import com.coco.gitcompose.datamodel.AccessTokenAppResponse
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface GithubAuthService {
    companion object {
        const val BASE_URL = "https://github.com/"
    }

    @POST("login/oauth/access_token")
    @Headers("Accept: application/vnd.github+json")
    suspend fun generateAccessToken(@Query("client_id") clientId: String,
                                    @Query("client_secret") clientSecret: String,
                                    @Query("code") code: String): AccessTokenAppResponse

    @POST("login/oauth/access_token")
    @Headers("Accept: application/vnd.github+json")
    suspend fun refreshAccessToken(@Query("client_id") clientId: String,
                                   @Query("client_secret") clientSecret: String,
                                   @Query("grant_type") grantType: String,
                                   @Query("refresh_token") refreshToken: String): AccessTokenAppResponse
}