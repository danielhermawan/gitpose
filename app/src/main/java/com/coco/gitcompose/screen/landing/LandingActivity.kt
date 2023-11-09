/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.coco.gitcompose.screen.landing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.theme.Blue50
import com.coco.gitcompose.core.ui.theme.Blue70
import com.coco.gitcompose.core.ui.theme.Blue90
import com.coco.gitcompose.core.ui.theme.GitposeTheme
import com.coco.gitcompose.screen.login.LoginActivity
import com.coco.gitcompose.screen.login.LoginScreen
import com.coco.gitcompose.screen.login.LoginUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.toImmutableList

@AndroidEntryPoint
class LandingActivity : ComponentActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, LandingActivity::class.java)
        }
    }

    private val viewModel: LandingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GitposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    val snackbarHostState = remember { SnackbarHostState() }

                    LandingScreen(
                        uiState = uiState,
                        snackbarHostState = snackbarHostState,
                        onLogoutClick = { viewModel.logout() },
                        onBottomTabClick = { landingNav ->
                            viewModel.onNavigationItemClick(landingNav)
                        }
                    )

                    LaunchedEffect(uiState.isLogin) {
                        if (!uiState.isLogin) {
                            navigateToLogin()
                        }
                    }

                    LaunchedEffect(uiState.logoutSuccess) {
                        if (uiState.logoutSuccess) {
                            navigateToLogin()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToLogin() {
        val intent = LoginActivity.getIntent(this)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    uiState: LandingUiState = LandingUiState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onLogoutClick: () -> Unit = {},
    onBottomTabClick: (LandingNav) -> Unit = {}
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            Surface (
                shadowElevation = 8.dp
            ) {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = uiState.appBarTitle),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = Color(0XFFbdbdbd)
            ) {
                for (navItem in uiState.navItems) {
                    key(navItem.id) {
                        NavigationBarItem(
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Blue50, selectedTextColor = Blue50,
                                indicatorColor =  MaterialTheme.colorScheme.primaryContainer
                            ),
                            selected = navItem.selected,
                            onClick = { onBottomTabClick(navItem.id) },
                            label = {
                                Text(
                                    text = stringResource(id = navItem.label),
                                )
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = if (navItem.selected) navItem.selectedIcon else navItem.icon
                                    ),
                                    contentDescription = stringResource(id = navItem.label)
                                )
                            }
                        )
                    }
                }

            }
        },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = { onLogoutClick() }
            ) {
                Text(text = stringResource(R.string.landing_button_logout))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Login() {
    GitposeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val uiState = LandingUiState(
                navItems = listOf(
                    NavItem(
                        id = LandingNav.HOME, appBarTitle = R.string.landing_tab_title_home,
                        icon = R.drawable.ic_home_24, label = R.string.landing_tab_title_home,
                        selected = true, selectedIcon = R.drawable.ic_home_filled_24
                    ),
                    NavItem(
                        id = LandingNav.NOTIFICATIONS, appBarTitle = R.string.landing_tab_title_notification,
                        icon = R.drawable.ic_notifications_24, label = R.string.landing_tab_title_notification,
                        selected = false, selectedIcon = R.drawable.ic_notifications_filled_24
                    ),
                    NavItem(
                        id = LandingNav.EXPLORE, appBarTitle = R.string.landing_tab_title_explore,
                        icon = R.drawable.ic_explore_24, label = R.string.landing_tab_title_explore,
                        selected = false, selectedIcon = R.drawable.ic_explore_filled_24px
                    ),
                    NavItem(
                        id = LandingNav.PROFILE, appBarTitle = R.string.landing_tab_title_profile,
                        icon = R.drawable.ic_person_24, label = R.string.landing_tab_title_profile,
                        selected = false, selectedIcon = R.drawable.ic_person_filled_24
                    )
                ).toImmutableList()
            )
            LandingScreen(uiState = uiState)
        }
    }
}

//todo: make generic snackbar handling, appbar and custom scaffold
//todo: make state saved automaticly in saved state
//todo: Move github service to build config
//todo: Clean up interceptor
//todo: create good navigation
