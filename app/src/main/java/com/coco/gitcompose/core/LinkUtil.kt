package com.coco.gitcompose.core

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams

import androidx.browser.customtabs.CustomTabsIntent
import com.coco.gitcompose.core.ui.theme.Black80


fun Context.openChromeTab(url: String) {
    val intent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams.Builder()
                .setToolbarColor(Black80.hashCode())
                .build()
        )
        .build()
    intent.launchUrl(this, Uri.parse(url))
}