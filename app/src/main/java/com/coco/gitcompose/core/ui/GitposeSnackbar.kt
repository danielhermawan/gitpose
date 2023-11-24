package com.coco.gitcompose.core.ui

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.coco.gitcompose.core.ui.theme.Green50
import kotlinx.parcelize.Parcelize

enum class MessageType {
    SUCCESS, INFO, ERROR
}

@Parcelize
data class SnackbarState(
    @StringRes val message: Int,
    val messageType: MessageType = MessageType.INFO
) : Parcelable

class GitposeSnackbarVisuals(
    override val message: String,
    val messageType: MessageType = MessageType.INFO,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration
    = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
    override val withDismissAction: Boolean = duration == SnackbarDuration.Indefinite
) : SnackbarVisuals

@Composable
fun SnackbarHostState.handleSnackbarState(
    snackbarStateState: SnackbarState?,
    onSnackbarMessageShown: () -> Unit = {}
) {
    snackbarStateState?.let { snackBarMessage ->
        val snackbarText = stringResource(snackBarMessage.message)
        LaunchedEffect(snackBarMessage) {
            showSnackbar(
                GitposeSnackbarVisuals(
                    message = snackbarText, messageType = snackBarMessage.messageType
                )
            )
            onSnackbarMessageShown()
        }
    }
}

@Composable
fun GitposeSnackbarHost(
    modifier: Modifier = Modifier,
    hostState: SnackbarHostState = remember {
        SnackbarHostState()
    }
) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        val visual = (data.visuals as? GitposeSnackbarVisuals)

        val containerColor: Color = when (visual?.messageType) {
            MessageType.ERROR -> MaterialTheme.colorScheme.error
            MessageType.SUCCESS -> Green50
            else -> MaterialTheme.colorScheme.inverseSurface
        }
        Snackbar(snackbarData = data, containerColor = containerColor)
    }
}