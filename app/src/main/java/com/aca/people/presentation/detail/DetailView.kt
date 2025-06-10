import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.aca.people.R
import com.aca.people.domain.User
import java.io.Serializable
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailView(navController: NavHostController) {
    val entry = navController.previousBackStackEntry?.savedStateHandle
    val user = entry?.get<Serializable>("user") as User?


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_arrow), // Reemplaza con el ícono de flecha
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) {

        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            user?.picture?.large?.let { pictureUrl ->
                val painter = rememberAsyncImagePainter(pictureUrl)
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            user?.name?.let { name ->
                Text(
                    text = "${name.title ?: ""} ${name.first ?: ""} ${name.last ?: ""}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            user?.email?.let { email ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = null,
                        tint = Color.Blue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(email, style = MaterialTheme.typography.titleLarge, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            user?.phone?.let { phone ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_phone),
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(phone, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            user?.gender?.let { gender ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = null,
                        tint = Color.Magenta,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gender: ${gender.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }}", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            user?.location?.let { location ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Location:", style = MaterialTheme.typography.bodyLarge)
                    }
                    Text("${location.city ?: ""}, ${location.state ?: ""}, ${location.country ?: ""}", style = MaterialTheme.typography.bodyLarge)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_globe),
                            contentDescription = null,
                            tint = Color.Blue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Coordinates: ${location.coordinates?.latitude ?: ""}, ${location.coordinates?.longitude ?: ""}", style = MaterialTheme.typography.bodyMedium)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clock),
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Timezone: ${location.timezone?.description ?: ""} (${location.timezone?.offset ?: ""})", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
