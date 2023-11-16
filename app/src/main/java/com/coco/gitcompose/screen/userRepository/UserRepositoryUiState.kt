package com.coco.gitcompose.screen.userRepository

import android.os.Parcelable
import com.coco.gitcompose.core.ui.SnackbarState
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRepositoryUiState(
    val snackbarState: SnackbarState? = null
) : Parcelable