package com.coco.gitcompose.core.network

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.core.DataStore
import com.coco.gitcompose.data.GithubToken
import com.coco.gitcompose.screen.login.LoginActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GithubTokenInterceptor @Inject constructor(
    private val dataStoreToken: DataStore<GithubToken>,
    private val sessionTokenManager: SessionTokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder: Request.Builder = chain.request().newBuilder()
        requestBuilder.header("Accept", "application/vnd.github+json")

        if (chain.request().url.host.contains("api.github.com")) {
            runBlocking {
                val githubToken = dataStoreToken.data.first()
                requestBuilder.header("Authorization", "Bearer ${githubToken.accessToken}")
            }
        }

        val response = chain.proceed(requestBuilder.build())

        return if (response.code == 401) {
            try {
                runBlocking {
                    val newToken = sessionTokenManager.refreshAndSaveAccessToken()
                    requestBuilder.header("Authorization", "Bearer ${newToken.accessToken}")
                    response.close()
                    chain.proceed(requestBuilder.build())
                }
            } catch (exception: Exception) {
                Log.e(GithubTokenInterceptor::class.java.name, exception.message, exception)
                response
            }
        } else {
            response
        }
    }

}