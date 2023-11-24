package com.coco.gitcompose.screen.landing

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.SnackbarState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parcelize


@Parcelize
@Stable
data class LandingUiState(
    val isLogin: Boolean = true,
    @StringRes val appBarTitle: Int = R.string.landing_tab_title_home,
    val navItems: List<NavItem> = emptyList<NavItem>().toImmutableList(),
    val snackbarState: SnackbarState? = null,
    val profilePictureUrl: String? = null,
    val selectedTab: LandingNav = LandingNav.HOME,
    val elevateTopBar: Boolean = false
) : Parcelable

enum class LandingNav {
    HOME, NOTIFICATIONS, EXPLORE, PROFILE
}

@Parcelize
data class NavItem(
    val id: LandingNav = LandingNav.HOME,
    @StringRes val appBarTitle: Int = R.string.landing_tab_title_home,
    @DrawableRes val icon: Int = R.drawable.ic_home_24,
    @DrawableRes val selectedIcon: Int = R.drawable.ic_home_filled_24,
    @StringRes val label: Int = R.string.landing_tab_title_home,
    val selected: Boolean = false,
) : Parcelable
