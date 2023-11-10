package com.coco.gitcompose.screen.login

import androidx.annotation.StringRes

data class LoginUiState(
    val loginSuccess: Boolean = false,
    val githubAuthUrl: String? = null,
    @StringRes val userMessage: Int? = null,
    val isLoading: Boolean = false
)