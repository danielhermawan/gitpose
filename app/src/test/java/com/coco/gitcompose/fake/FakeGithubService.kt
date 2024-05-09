package com.coco.gitcompose.fake

import com.coco.gitcompose.core.datamodel.CurrentUserResponse
import com.coco.gitcompose.core.datamodel.RepoDataModel
import com.coco.gitcompose.core.datamodel.SearchRepoResponse
import com.coco.gitcompose.core.remote.GithubService
import retrofit2.Response

class FakeGithubService(
    val dummyTrendingRepo: MutableList<RepoDataModel> = createDummyPublicRepos(),
    val dummyStarredRepo: MutableSet<String> = createDummyStarredRepo(3)
) : GithubService {

    override suspend fun getCurrentUser(): CurrentUserResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentUserRepos(
        sort: String,
        perPage: Int,
        page: Int,
        sortBy: String,
        type: String
    ): List<RepoDataModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getRepoDetail(owner: String, name: String): RepoDataModel {
        TODO("Not yet implemented")
    }

    override suspend fun searchRepo(
        search: String,
        sort: String,
        order: String,
        page: Int,
        perPage: Int
    ): SearchRepoResponse {
        return SearchRepoResponse(dummyTrendingRepo.size, false, dummyTrendingRepo.take(perPage))
    }

    override suspend fun isRepoStarred(owner: String, name: String): Response<*> {
        if (dummyStarredRepo.contains("${owner}/${name}")) {
            return Response.success(204, "");
        } else {
            return Response.success(201, "");
        }
    }

    override suspend fun starRepo(owner: String, name: String): Response<*> {
        TODO("Not yet implemented")
    }

    override suspend fun unstarRepo(owner: String, name: String): Response<*> {
        TODO("Not yet implemented")
    }
}