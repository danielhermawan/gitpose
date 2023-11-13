package com.coco.gitcompose.screen.landing.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.SnackbarState
import com.coco.gitcompose.core.ui.theme.GitposeTheme
import com.coco.gitcompose.screen.landing.LandingNav

@Composable
fun ProfileTab(
    modifier: Modifier = Modifier,
    selectedTab: LandingNav = LandingNav.PROFILE,
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit = {},
    onSnackbarEvent: (SnackbarState?) -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (selectedTab == LandingNav.PROFILE) {
        ProfileScreen(
            modifier = modifier,
            uiState =  uiState,
            onLogoutClick = {
                viewModel.logout()
            }

        )

        LaunchedEffect(uiState.snackbarState) {
            onSnackbarEvent(uiState.snackbarState)
        }

        LaunchedEffect(uiState.logoutSuccess) {
            if (uiState.logoutSuccess) {
                onLogoutSuccess()
            }
        }

        LaunchedEffect(selectedTab) {
            viewModel.onTabSelected()
        }
    }

}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit = {},
    uiState: ProfileUiState = ProfileUiState()
) {
    Box(
        modifier = modifier
    ) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = { onLogoutClick() }
        ) {
            Text(text = stringResource(R.string.landing_button_logout))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    GitposeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileTab()
        }
    }
}