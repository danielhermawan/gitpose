package com.coco.gitcompose.core.remote

import androidx.datastore.core.DataStore
import com.coco.gitcompose.BuildConfig
import com.coco.gitcompose.core.common.Dispatcher
import com.coco.gitcompose.core.common.GitposeDispatchers
import com.coco.gitcompose.core.datamodel.AccessTokenAppResponse
import com.coco.gitcompose.core.datamodel.GithubOauthInfo
import com.coco.gitcompose.data.GithubToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionTokenManager @Inject constructor(
    private val dataStoreToken: DataStore<GithubToken>,
    private val githubAuthService: GithubAuthService,
    @Dispatcher(GitposeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    fun getGithubOauthInfo(): GithubOauthInfo {
        //todo: Secret should be saved in server backend instead in local properties
        return GithubOauthInfo(
            BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET
        )
    }

    suspend fun generateAndSaveAccessToken(code: String): GithubToken {
        return withContext(ioDispatcher) {
            val tokenResponse = generateAccessToken(code)
            saveAccessToken { currentToken ->
                currentToken.toBuilder()
                    .setAccessToken(tokenResponse.accessToken)
                    .setExpiredIn(tokenResponse.expiredIn)
                    .setRefreshToken(tokenResponse.refreshToken)
                    .setRefreshTokenExpiresIn(tokenResponse.refreshTokenExpiredIn)
                    .build()
            }
        }
    }

    suspend fun refreshAndSaveAccessToken(): GithubToken {
        return withContext(ioDispatcher) {
            val githubToken = dataStoreToken.data.first()
            val tokenResponse = refreshAccessToken(githubToken.refreshToken)
            saveAccessToken { currentToken ->
                currentToken.toBuilder()
                    .setAccessToken(tokenResponse.accessToken)
                    .setExpiredIn(tokenResponse.expiredIn)
                    .setRefreshToken(tokenResponse.refreshToken)
                    .setRefreshTokenExpiresIn(tokenResponse.refreshTokenExpiredIn)
                    .build()
            }
        }
    }

    private suspend fun generateAccessToken(code: String): AccessTokenAppResponse {
        val githubOauthInfo = getGithubOauthInfo();
        return githubAuthService.generateAccessToken(
            githubOauthInfo.clientId,
            githubOauthInfo.clientSecret,
            code
        )
    }

    private suspend fun refreshAccessToken(refreshToken: String): AccessTokenAppResponse {
        val githubOauthInfo = getGithubOauthInfo();
        return githubAuthService.refreshAccessToken(
            githubOauthInfo.clientId,
            githubOauthInfo.clientSecret,
            "refresh_token",
            refreshToken
        )
    }

    private suspend fun saveAccessToken(
        updateCallback: (GithubToken) -> GithubToken
    ): GithubToken {
        return dataStoreToken.updateData {
            updateCallback(it)
        }
    }
}