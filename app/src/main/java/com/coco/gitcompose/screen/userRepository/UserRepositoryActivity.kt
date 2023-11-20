package com.coco.gitcompose.screen.userRepository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.GitposeSnackbarHost
import com.coco.gitcompose.core.ui.handleSnackbarState
import com.coco.gitcompose.core.ui.theme.GitposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserRepositoryActivity : ComponentActivity() {
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, UserRepositoryActivity::class.java)
        }
    }

    private val viewModel: UserRepositoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GitposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    val snackbarHostState = remember {
                        SnackbarHostState()
                    }

                    UserRepositoryScreen(
                        uiState = uiState,
                        snackbarHostState = snackbarHostState,
                    )

                    snackbarHostState.handleSnackbarState(
                        snackbarStateState = uiState.snackbarState,
                        onSnackbarMessageShown = { viewModel.onSnackbarMessageShown() }
                    )
                }
            }
        }
    }
}

@Composable
fun UserRepositoryScreen(
    modifier: Modifier = Modifier,
    uiState: UserRepositoryUiState = UserRepositoryUiState(),
    showTopElevation: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = {
            GitposeSnackbarHost(
                hostState = snackbarHostState
            )
        },
        topBar = {
            UserRepositoryAppBar(
                ownerName = uiState.loginName
            )
        },
        modifier = modifier
    ) { padding ->
        Surface(
            modifier = modifier.padding(padding),
            shadowElevation = if (showTopElevation) 8.dp else 0.dp
        ) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRepositoryAppBar(
    modifier: Modifier = Modifier,
    ownerName: String = ""
) {
    TopAppBar(
        title = {
            Text(
                text = ownerName,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
            IconButton(onClick = { /* todo: Implement search */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_24),
                    contentDescription = "Search Action",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        },
        modifier = Modifier
    )
}