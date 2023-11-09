package com.coco.gitcompose.usecase

import androidx.datastore.core.DataStore
import com.coco.gitcompose.core.network.GithubService
import com.coco.gitcompose.datamodel.CurrentUser
import com.coco.gitcompose.datamodel.CurrentUserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.toInstant
import javax.inject.Inject

interface GithubUserUserCase {
    fun getCurrentUserStream(): Flow<CurrentUser>

    suspend fun refreshCurrentUser()
}

class DefaultGithubUserUserCase @Inject constructor(
    private val githubService: GithubService,
    private val currentUserDataStore: DataStore<CurrentUser>
): GithubUserUserCase{
    override fun getCurrentUserStream(): Flow<CurrentUser> {
        return currentUserDataStore.data
    }

    override suspend fun refreshCurrentUser() {
        val currentUser = githubService.getCurrentUser()
        currentUserDataStore.updateData {
            it.toBuilder()
                .setLogin(currentUser.login)
                .setAvatarUrl(currentUser.avatarUrl)
                .setName(currentUser.name)
                .setEmail(currentUser.email)
                .setBio(currentUser.bio)
                .setFollowers(currentUser.followers)
                .setFollowing(currentUser.following)
                .setTotalPrivateRepos(currentUser.totalPrivateRepos)
                .setOwnedPrivateRepos(currentUser.ownedPrivateRepos)
                .setCreatedAt(currentUser.createdAt)
                .setUpdatedAt(currentUser.updatedAt)
                .build()
        }
    }
}