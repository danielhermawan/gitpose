package com.coco.gitcompose.usecase

import androidx.datastore.core.DataStore
import com.coco.gitcompose.core.remote.SessionTokenManager
import com.coco.gitcompose.core.util.generateGithubAuthUrl
import com.coco.gitcompose.data.GithubToken
import com.coco.gitcompose.datamodel.CurrentUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


interface GithubAuthUseCase {
    fun isLogin(): Flow<Boolean>

    fun getGithubOauthUrl(state: String): String

    suspend fun generateAndSaveAccessToken(code: String)

    suspend fun logout()

}

class DefaultAuthUseCase @Inject constructor(
    private val dataStoreToken: DataStore<GithubToken>,
    private val dataStoreCurrentUser: DataStore<CurrentUser>,
    private val sessionTokenManager: SessionTokenManager,
) : GithubAuthUseCase {
    override fun isLogin(): Flow<Boolean> {
        return dataStoreToken.data.map {
            it.accessToken.isNotEmpty()
        }
    }

    override fun getGithubOauthUrl(state: String): String {
        val githubOauthInfo = sessionTokenManager.getGithubOauthInfo()
        return generateGithubAuthUrl(
            githubOauthInfo.clientId,
            state,
            "repo,gist,notifications,user,project,admin:org"
        )
    }

    override suspend fun generateAndSaveAccessToken(code: String) {
        sessionTokenManager.generateAndSaveAccessToken(code)
    }

    override suspend fun logout() {
        dataStoreToken.updateData {
            it.toBuilder().clear().build()
        }
        dataStoreCurrentUser.updateData {
            it.toBuilder().clear().build()
        }

    }
}