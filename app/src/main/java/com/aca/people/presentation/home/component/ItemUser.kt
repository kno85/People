package com.aca.people.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aca.people.R
import com.aca.people.domain.User
import java.util.Locale

@Composable
fun ItemUser(
    user: User,
    onClick: () -> Unit,
    isFavorite: Boolean = false,
    hasNote: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(pictureUrl = user.picture?.large)
            Spacer(modifier = Modifier.width(16.dp))
            UserMainInfo(user = user, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            UserBadges(user = user, hasNote = hasNote)
            if (onToggleFavorite != null) {
                FavoriteActionButton(
                    isFavorite = isFavorite,
                    onClick = onToggleFavorite
                )
            }
        }
    }
}

@Composable
private fun FavoriteActionButton(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = stringResource(
                id = if (isFavorite) R.string.cd_remove_favorite else R.string.cd_add_favorite
            ),
            tint = if (isFavorite) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun UserAvatar(pictureUrl: String?) {
    pictureUrl?.let { url ->
        val painter = rememberAsyncImagePainter(url)
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
    }
}

@Composable
private fun UserMainInfo(user: User, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        user.name?.let { name ->
            Text(
                text = "${name.first.orEmpty()} ${name.last.orEmpty()}".trim(),
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = user.email ?: "Email no disponible",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = user.phone ?: "Teléfono no disponible",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun UserBadges(user: User, hasNote: Boolean) {
    Column(horizontalAlignment = Alignment.End) {
        if (hasNote) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = stringResource(id = R.string.cd_has_note),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(4.dp)
                    .size(16.dp)
            )
        }
        user.gender?.let { gender ->
            val formatted = gender.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            Text(
                text = formatted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(4.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .clip(RoundedCornerShape(5.dp))
            )
        }

        user.location?.let { location ->
            Text(
                text = location.city ?: "",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(4.dp)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                    .clip(RoundedCornerShape(5.dp))
            )
        }
    }
}

