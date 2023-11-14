package com.coco.gitcompose.core.network

import com.coco.gitcompose.datamodel.CurrentUserResponse
import com.coco.gitcompose.datamodel.RepoDataModel
import com.coco.gitcompose.datamodel.RepoSort
import retrofit2.http.GET
import retrofit2.http.Query


interface GithubService {

    companion object {
        const val GITHUB_BASE_URL = "https://api.github.com/"
    }

    @GET("user")
    suspend fun getCurrentUser(): CurrentUserResponse

    @GET("user/repos")
    suspend fun getCurrentUserRepos(
        @Query("sort") sort: RepoSort = RepoSort.full_name,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): List<RepoDataModel>

}