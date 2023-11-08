package com.coco.gitcompose.screen.login

data class LoginUiState(
    val loginSuccess: Boolean = false,
    val githubAuthUrl: String? = null,
    val userMessage: Int? = null,
    val isLoading: Boolean = false
)