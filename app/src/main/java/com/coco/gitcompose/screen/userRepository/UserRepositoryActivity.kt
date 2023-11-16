package com.coco.gitcompose.screen.userRepository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {

}