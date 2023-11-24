package com.coco.gitcompose.core.util

import android.content.Context

fun Context.goToGithubPrivacyPage() {
    openChromeTab("https://help.github.com/en/github/site-policy/github-privacy-statement")
}

fun Context.goToGithubTermPage() {
    openChromeTab("https://docs.github.com/en/site-policy/github-terms/github-terms-of-service")
}

fun generateGithubAuthUrl(clientId: String, state: String, scope: String) =
    "https://github.com/login/oauth/authorize?client_id=${clientId}&state=$state&scope=$scope"

