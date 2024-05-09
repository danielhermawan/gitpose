package com.coco.gitcompose.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    snackbarState: SnackbarState?,
    onSnackbarShown: () -> Unit,
    topBar: @Composable () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    Scaffold(
        snackbarHost = {
            GitposeSnackbarHost(
                hostState = snackbarHostState
            )
        },
        topBar = {
            topBar()
        },
        modifier = modifier
    ) { padding ->
        content(Modifier.padding(padding))
    }

    snackbarHostState.handleSnackbarState(
        snackbarStateState = snackbarState,
        onSnackbarMessageShown = { onSnackbarShown() }
    )
}

@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    snackbarState: SnackbarState?,
    topBarTitle: String,
    onSnackbarShown: () -> Unit,
    onBackPressed: () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    BaseScreen(
        modifier = modifier,
        snackbarState = snackbarState,
        onSnackbarShown = onSnackbarShown,
        topBar = {
            GitposeTopAppBar(
                modifier = modifier,
                title = topBarTitle,
                onBackPressed = onBackPressed
            )
        },
        content = content
    )
}
