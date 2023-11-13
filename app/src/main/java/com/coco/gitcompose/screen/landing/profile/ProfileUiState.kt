package com.coco.gitcompose.screen.landing.profile

import com.coco.gitcompose.core.ui.SnackbarState

data class ProfileUiState(
    val snackbarState: SnackbarState? = null,
    val logoutSuccess: Boolean = false,
    val name: String = "",
    val username: String = "",
    val followers: Int = 0,
    val totalRepo: Int = 0,
    val totalOrganizations: Int = 0,
    val totalStar: Int = 0
    )