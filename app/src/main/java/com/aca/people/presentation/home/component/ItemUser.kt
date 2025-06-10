import android.view.View.OnClickListener
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aca.people.domain.User
import java.util.Locale

@Composable
fun ItemUser(
    user: User,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick), shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            user.picture?.large?.let { pictureUrl ->
                val painter = rememberAsyncImagePainter(pictureUrl)
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                user.name?.let { name ->
                    Text(
                        text = "${name.first} ${name.last}",
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

            Spacer(modifier = Modifier.weight(1f))

            Column {
                user.gender?.let { gender ->
                    Text(
                        text = gender.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
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
    }
}
