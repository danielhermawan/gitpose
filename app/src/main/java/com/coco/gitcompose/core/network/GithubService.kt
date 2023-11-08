package com.coco.gitcompose.core.network

import com.coco.gitcompose.datamodel.CurrentUserResponse
import retrofit2.http.GET


interface GithubService {

    companion object {
        const val GITHUB_BASE_URL = "https://api.github.com/"
    }

    @GET("user")
    suspend fun getCurrentUser(): CurrentUserResponse


}