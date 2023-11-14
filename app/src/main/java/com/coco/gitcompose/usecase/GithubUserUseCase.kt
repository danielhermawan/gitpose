package com.coco.gitcompose.usecase

import androidx.datastore.core.DataStore
import com.coco.gitcompose.core.common.Dispatcher
import com.coco.gitcompose.core.common.GitposeDispatchers
import com.coco.gitcompose.core.network.GithubService
import com.coco.gitcompose.datamodel.CurrentUser
import com.coco.gitcompose.datamodel.RepoDataModel
import com.coco.gitcompose.datamodel.RepoSort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GithubUserUseCase {
    suspend fun getLocalCurrentUser(): CurrentUser

    suspend fun getRemoteCurrentUser(): CurrentUser

    fun getStreamCurrentUser(refresh: Boolean): Flow<CurrentUser>

    suspend fun getRemoteCurrentUserRepository(
        sort: RepoSort = RepoSort.full_name, perPage: Int = 30, page: Int = 1
    ): List<RepoDataModel>

    suspend fun refreshCurrentUser()
}

class DefaultGithubUserUseCase @Inject constructor(
    private val githubService: GithubService,
    private val currentUserDataStore: DataStore<CurrentUser>,
    @Dispatcher(GitposeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : GithubUserUseCase {
    private val _currentUser = MutableStateFlow<CurrentUser?>(null)

    override suspend fun getLocalCurrentUser(): CurrentUser {
        return currentUserDataStore.data.first()
    }

    override suspend fun getRemoteCurrentUser(): CurrentUser {
        return withContext(ioDispatcher) {
            val response = githubService.getCurrentUser()
            currentUserDataStore.updateData {
                it.toBuilder()
                    .setLogin(response.login)
                    .setAvatarUrl(response.avatarUrl)
                    .setName(response.name)
                    .setEmail(response.email ?: "")
                    .setBio(response.bio ?: "")
                    .setFollowers(response.followers)
                    .setFollowing(response.following)
                    .setTotalPrivateRepos(response.totalPrivateRepos)
                    .setOwnedPrivateRepos(response.ownedPrivateRepos)
                    .setCreatedAt(response.createdAt)
                    .setUpdatedAt(response.updatedAt)
                    .setPublicRepos(response.publicRepos)
                    .build()
            }
        }
    }

    override suspend fun refreshCurrentUser() {
        val newCurrentUser = getRemoteCurrentUser()
        _currentUser.update {
            newCurrentUser.toBuilder().setBio(System.currentTimeMillis().toString()).build()
        }
    }

    override fun getStreamCurrentUser(refresh: Boolean): Flow<CurrentUser> {
        return _currentUser.onStart {
            val localCurrentUser = getLocalCurrentUser()
            _currentUser.update {
                localCurrentUser
            }
            emit(localCurrentUser)
            if (refresh) {
                refreshCurrentUser()
            }
        }.filterNotNull()
    }

    override suspend fun getRemoteCurrentUserRepository(
        sort: RepoSort,
        perPage: Int,
        page: Int
    ): List<RepoDataModel> {
        return withContext(ioDispatcher) {
            githubService.getCurrentUserRepos(sort, perPage, page)
        }
    }
}