package com.coco.gitcompose.usecase

import com.coco.gitcompose.core.common.Dispatcher
import com.coco.gitcompose.core.common.GitposeDispatchers
import com.coco.gitcompose.core.datamodel.SearchRepoResponse
import com.coco.gitcompose.core.remote.GithubService
import com.coco.gitcompose.core.util.DateUtil
import com.coco.gitcompose.core.util.getId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import javax.inject.Inject

interface GithubSearchRepository {
    suspend fun getTrendingRepository(
        day: Int = 1,
        perPage: Int = 30,
        page: Int = 1,
    ): SearchRepoResponse

    suspend fun starRepo(owner: String, repo: String, star: Boolean)
}

class DefaultGithubSearchRepository @Inject constructor(
    private val githubService: GithubService,
    @Dispatcher(GitposeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val dateUtil: DateUtil
) : GithubSearchRepository {
    override suspend fun getTrendingRepository(
        day: Int,
        perPage: Int,
        page: Int
    ): SearchRepoResponse {
        return withContext(ioDispatcher) {
            val pushed = dateUtil.getTimeBefore(day, DateTimeUnit.DAY)
            val created = dateUtil.getTimeBefore(1, DateTimeUnit.MONTH)
            val searchRepoResponse =
                githubService.searchRepo("pushed:>$pushed created:>$created", page = page)
            val deferred = mutableListOf<Deferred<Pair<String, Boolean>>>()
            for (repo in searchRepoResponse.items) {
                deferred.add(async {
                    val starred =
                        githubService.isRepoStarred(repo.owner.login, repo.name).code() == 204
                    Pair(repo.getId(), starred)
                })
            }
            val mapStarred = deferred.awaitAll().toMap()
            searchRepoResponse.items.forEach { repoDataModel ->
                repoDataModel.isStarred = mapStarred[repoDataModel.getId()] ?: false
            }
            searchRepoResponse
        }
    }

    override suspend fun starRepo(owner: String, repo: String, star: Boolean) {
        withContext(ioDispatcher) {
            if (star) {
                githubService.starRepo(owner, repo)
            } else {
                githubService.unstarRepo(owner, repo)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTrendingRepositoryFlow(perPage: Int, page: Int): Flow<SearchRepoResponse> {
        val pushed = dateUtil.getTimeBefore(7, DateTimeUnit.DAY)
        val created = dateUtil.getTimeBefore(1, DateTimeUnit.MONTH)
        val query = "pushed:>$pushed created:>$created"
        return flowTrending(query, page).flatMapConcat { response ->
            flowOf(
                flowOf(*response.items.toTypedArray()).flatMapMerge { repo ->
                    flowIsStar(repo.owner.login, repo.name).zip(flowOf(repo)) { starred, r ->
                        Pair(r.getId(), starred)
                    }
                }.toList().toMap()
            ).map { mapStarred ->
                response.items.forEach {
                    it.isStarred = mapStarred[it.getId()] ?: false
                }
                response
            }
        }
    }

    private fun flowTrending(query: String, page: Int) = flow {
        emit(githubService.searchRepo(query, page = page))
    }

    private fun flowIsStar(owner: String, repo: String) = flow {
        val isStarred = githubService.isRepoStarred(owner, repo).code() == 204
        emit(isStarred)
    }

}