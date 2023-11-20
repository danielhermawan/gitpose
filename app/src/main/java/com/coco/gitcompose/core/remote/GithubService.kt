package com.coco.gitcompose.core.remote

import com.coco.gitcompose.core.datamodel.CurrentUserResponse
import com.coco.gitcompose.core.datamodel.RepoDataModel
import com.coco.gitcompose.core.datamodel.RepoSort
import com.coco.gitcompose.core.datamodel.RepoType
import com.coco.gitcompose.core.datamodel.SortBy
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface GithubService {

    companion object {
        const val GITHUB_BASE_URL = "https://api.github.com/"
    }

    @GET("user")
    suspend fun getCurrentUser(): CurrentUserResponse

    @GET("user/repos")
    suspend fun getCurrentUserRepos(
        @Query("sort") sort: String = RepoSort.FULL_NAME.sortName,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1,
        @Query("direction") sortBy: String = SortBy.ASC.sortByName,
        @Query("type") type: String = RepoType.ALL.typeName
    ): List<RepoDataModel>

    @GET("repos/{owner}/{name}")
    suspend fun getRepoDetail(
        @Path("owner") owner: String,
        @Path("name") name: String,
    ): RepoDataModel

}