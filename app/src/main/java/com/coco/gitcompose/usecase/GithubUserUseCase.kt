package com.coco.gitcompose.usecase

import androidx.datastore.core.DataStore
import com.coco.gitcompose.core.common.Dispatcher
import com.coco.gitcompose.core.common.GitposeDispatchers
import com.coco.gitcompose.core.network.GithubService
import com.coco.gitcompose.datamodel.CurrentUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GithubUserUseCase {
    suspend fun getLocalCurrentUser(): CurrentUser

    suspend fun getRemoteCurrentUser(): CurrentUser
}

class DefaultGithubUserUserCase @Inject constructor(
    private val githubService: GithubService,
    private val currentUserDataStore: DataStore<CurrentUser>,
    @Dispatcher(GitposeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
): GithubUserUseCase{
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
                    .setTotalPrivateRepos(response.totalPrivateRepos ?: 0)
                    .setOwnedPrivateRepos(response.ownedPrivateRepos ?: 0)
                    .setCreatedAt(response.createdAt)
                    .setUpdatedAt(response.updatedAt)
                    .build()
            }
        }
    }
}