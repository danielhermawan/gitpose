package com.coco.gitcompose.screen.landing

import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.SnackbarState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class LandingUiState(
    val isLogin: Boolean = true,
    val logoutSuccess: Boolean = false,
    val appBarTitle: Int = R.string.landing_tab_title_home,
    val navItems: ImmutableList<NavItem> = emptyList<NavItem>().toImmutableList(),
    val snackbarState: SnackbarState? = null,
    val profilePictureUrl: String? = null
)

enum class LandingNav {
    HOME, NOTIFICATIONS, EXPLORE, PROFILE
}

data class NavItem(
    val id: LandingNav = LandingNav.HOME,
    val appBarTitle: Int = R.string.landing_tab_title_home,
    val icon: Int = R.drawable.ic_home_24,
    val selectedIcon: Int = R.drawable.ic_home_filled_24,
    val label: Int = R.string.landing_tab_title_home,
    val selected: Boolean = false,
)
