package com.coco.gitcompose.usecase

import com.coco.gitcompose.core.common.Dispatcher
import com.coco.gitcompose.core.common.GitposeDispatchers
import com.coco.gitcompose.core.database.UserRepositoryDao
import com.coco.gitcompose.core.database.UserRepositoryEntity
import com.coco.gitcompose.core.datamodel.RepoDataModel
import com.coco.gitcompose.core.datamodel.RepoOwnerDataModel
import com.coco.gitcompose.core.datamodel.RepoParentDataModel
import com.coco.gitcompose.core.datamodel.RepoSort
import com.coco.gitcompose.core.datamodel.SortBy
import com.coco.gitcompose.core.remote.GithubService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import javax.inject.Inject


interface GithubRepositoryUseCase {

    fun getStreamCurrentUserRepository(
        sort: RepoSort = RepoSort.FULL_NAME,
        sortBy: SortBy = SortBy.DESC
    ): Flow<List<RepoDataModel>>

    suspend fun getRemoteCurrentUserRepository(
        sort: RepoSort = RepoSort.FULL_NAME,
        perPage: Int = 30,
        page: Int = 1,
        sortBy: SortBy = SortBy.DESC,
        savedInCache: Boolean = false,
        replaceCache: Boolean = true
    ): List<RepoDataModel>
}

class DefaultGithubRepositoryUseCase @Inject constructor(
    private val githubService: GithubService,
    private val userRepositoryDao: UserRepositoryDao,
    @Dispatcher(GitposeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : GithubRepositoryUseCase {
    override fun getStreamCurrentUserRepository(
        sort: RepoSort,
        sortBy: SortBy
    ): Flow<List<RepoDataModel>> {
        val userRepositories =
            if (sortBy == SortBy.DESC) userRepositoryDao.getUserRepositoriesDesc(sort.sortName) else userRepositoryDao.getUserRepositoriesAsc(
                sort.sortName
            )
        return userRepositories
            .map { entities ->
                mapToDataModel(entities)
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun getRemoteCurrentUserRepository(
        sort: RepoSort,
        perPage: Int,
        page: Int,
        sortBy: SortBy,
        savedInCache: Boolean,
        replaceCache: Boolean
    ): List<RepoDataModel> {
        return withContext(ioDispatcher) {
            val currentUserRepos =
                githubService.getCurrentUserRepos(sort.sortName, perPage, page, sortBy.sortByName)
                    .map { repo ->
                        if (repo.fork) {
                            githubService.getRepoDetail(repo.owner.login, repo.name)
                        } else {
                            repo
                        }
                    }
            if (savedInCache) {
                val entities = mapToEntity(currentUserRepos)
                if (replaceCache) {
                    userRepositoryDao.insertRepository(entities)
                } else {
                    userRepositoryDao.replaceDataInTransaction(entities)
                }
            }
            currentUserRepos
        }
    }

    private fun mapToEntity(repos: List<RepoDataModel>): List<UserRepositoryEntity> {
        return repos.map { repo ->
            var topics = ""

            repo.topics.forEachIndexed { index, s ->
                topics += if (index == 0) {
                    s
                } else {
                    ",s"
                }
            }
            UserRepositoryEntity(
                id = repo.id,
                name = repo.name,
                fullName = repo.fullName,
                private = repo.private,
                forkedFrom = repo.parent?.fullName,
                starCount = repo.stargazersCount,
                language = repo.language,
                ownerName = repo.owner.avatarUrl,
                created = Instant.parse(repo.createdAt).epochSeconds,
                updated = Instant.parse(repo.updatedAt).epochSeconds,
                pushed = Instant.parse(repo.pushedAt).epochSeconds,
                htmlUrl = repo.htmlUrl,
                description = repo.description,
                fork = repo.fork,
                forksCount = repo.forksCount,
                watchersCount = repo.watchersCount,
                defaultBranch = repo.defaultBranch,
                openIssueCount = repo.openIssueCount,
                isTemplate = repo.isTemplate,
                topics = topics,
                visibility = repo.visibility,
                ownerLogin = repo.owner.login,
                ownerAvatarUrl = repo.owner.avatarUrl,
                parentFullName = repo.parent?.fullName
            )
        }
    }

    private fun mapToDataModel(entities: List<UserRepositoryEntity>): List<RepoDataModel> {
        return entities.map { entity ->
            RepoDataModel(
                id = entity.id,
                name = entity.name,
                fullName = entity.fullName,
                private = entity.private,
                htmlUrl = entity.htmlUrl,
                description = entity.description,
                fork = entity.fork,
                language = entity.language,
                forksCount = entity.forksCount,
                stargazersCount = entity.starCount,
                watchersCount = entity.watchersCount,
                defaultBranch = entity.defaultBranch,
                openIssueCount = entity.openIssueCount,
                isTemplate = entity.isTemplate,
                topics = entity.topics.split(","),
                visibility = entity.visibility,
                owner = RepoOwnerDataModel(
                    login = entity.ownerLogin,
                    avatarUrl = entity.ownerAvatarUrl
                ),
                createdAt = Instant.fromEpochMilliseconds(entity.created).toString(),
                updatedAt = Instant.fromEpochMilliseconds(entity.updated).toString(),
                pushedAt = Instant.fromEpochMilliseconds(entity.pushed).toString(),
                parent = entity.parentFullName?.let { parentFullName ->
                    RepoParentDataModel(
                        fullName = parentFullName
                    )
                }
            )
        }
    }

}