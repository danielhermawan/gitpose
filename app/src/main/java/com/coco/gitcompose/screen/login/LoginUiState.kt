package com.coco.gitcompose.screen.login

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginUiState(
    val loginSuccess: Boolean = false,
    val githubAuthUrl: String? = null,
    @StringRes val userMessage: Int? = null,
    val isLoading: Boolean = false
) : Parcelable