package com.coco.gitcompose.screen.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coco.gitcompose.R
import com.coco.gitcompose.core.ui.theme.Blue90
import com.coco.gitcompose.core.ui.theme.GitposeTheme
import com.coco.gitcompose.core.util.goToGithubPrivacyPage
import com.coco.gitcompose.core.util.goToGithubTermPage
import com.coco.gitcompose.core.util.openChromeTab
import com.coco.gitcompose.screen.landing.LandingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    private val viewModel: LoginViewModel by viewModels()

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
                        SnackbarHostState ()
                    }

                    LoginScreen(
                        uiState = uiState,
                        snackbarHostState = snackbarHostState,
                        onLoginClick = { viewModel.loginGithub() },
                        onPrivacyClick = {
                            goToGithubPrivacyPage()
                        },
                        onTermClick = {
                            goToGithubTermPage()
                        })

                    uiState.userMessage?.let { userMessage ->
                        val snackbarText = stringResource(userMessage)
                        LaunchedEffect(userMessage) {
                            snackbarHostState.showSnackbar(snackbarText)
                            viewModel.onUserMessageShown()
                        }
                    }

                    LaunchedEffect(uiState.githubAuthUrl) {
                        uiState.githubAuthUrl?.let { authUrl ->
                            openChromeTab(authUrl)
                        }
                    }

                    LaunchedEffect(uiState.loginSuccess) {
                        if (uiState.loginSuccess) {
                            navigateToLanding()
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.data

        val code = uri?.getQueryParameter("code")
        val state = uri?.getQueryParameter("state")
        if (code != null && state != null) {
            viewModel.getAccessToken(code, state)
        }
    }

    private fun navigateToLanding() {
        val intent = LandingActivity.getIntent(this)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginUiState = LoginUiState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onLoginClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onTermClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.github_logo),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(160.dp),
                contentDescription = stringResource(id = R.string.login_github_logo))

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(dimensionResource(id = R.dimen.horizontal_margin)),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    onClick = { onLoginClick() }
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                    } else {
                        Text(text = stringResource(id = R.string.login_sign_in_button), style = MaterialTheme.typography.titleMedium)
                    }

                }

                Text(
                    text = stringResource(R.string.login_sign_info_text_info),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )

                val annotatedText = buildAnnotatedString {
                    append("By signing in you accept our ")

                    pushStringAnnotation(
                        tag = "TERM",
                        annotation = "https://docs.github.com/en/site-policy/github-terms/github-terms-of-service"
                    )
                    withStyle(
                        style = SpanStyle(
                            color = Blue90, textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Terms of use")
                    }
                    pop()

                    append(" and ")

                    pushStringAnnotation(
                        tag = "PRIVACY",
                        annotation = "https://help.github.com/en/github/site-policy/github-privacy-statement"
                    )
                    withStyle(
                        style = SpanStyle(
                            color = Blue90, textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Privacy policy")
                    }
                    pop()
                }


                ClickableText(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                ) { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "TERM", start = offset, end = offset
                    ).firstOrNull()?.let {
                        onTermClick()
                    }

                    annotatedText.getStringAnnotations(
                        tag = "PRIVACY", start = offset, end = offset
                    ).firstOrNull()?.let {
                        onPrivacyClick()
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
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
            LoginScreen(uiState = LoginUiState())
        }
    }
}