package com.coco.gitcompose.core.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coco.gitcompose.R

@Composable
fun FilterButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int = R.drawable.ic_expand_24,
    name: String? = null,
    count: Int? = null,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.secondaryContainer
    else MaterialTheme.colorScheme.background
    val contentColor = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
    else MaterialTheme.colorScheme.onPrimaryContainer
    val iconTintColorColor = if (!name.isNullOrEmpty()) contentColor
    else MaterialTheme.colorScheme.onSecondaryContainer

    Row(
        modifier
            .background(backgroundColor, ShapeDefaults.ExtraLarge)
            .clip(ShapeDefaults.ExtraLarge)
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!name.isNullOrEmpty()) {
            Text(text = name, style = MaterialTheme.typography.labelSmall, color = contentColor)
        }

        Image(
            painter = painterResource(id = icon), contentDescription = "Filter $name",
            modifier = Modifier
                .size(20.dp)
                .padding(start = 4.dp),
            colorFilter = ColorFilter.tint(iconTintColorColor)
        )
        if (count != null) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(16.dp)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer, CircleShape)
            )
        }
    }
}