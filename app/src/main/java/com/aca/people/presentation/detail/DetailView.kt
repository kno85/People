package com.aca.people.presentation.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.aca.people.domain.User
import java.io.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailView(navController: NavHostController) {
    val entry = navController.previousBackStackEntry?.savedStateHandle
    val user = entry?.get<Serializable>("user") as User?

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Background Image
        Image(
            painter = rememberAsyncImagePainter(user?.picture?.large),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(shape = MaterialTheme.shapes.medium)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar Image and User Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Info
            Column {
                Text(
                    text = "${user?.name?.first} ${user?.name?.last}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text =  "Email: ${user?.email}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Gender: ${user?.gender}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Age: ${user?.registered?.age}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Date of Register: ${user?.registered?.date}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Phone Number: ${user?.phone}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Country: ${user?.location?.country}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "State: ${user?.location?.state}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "City: ${user?.location?.city}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Postal Code: ${user?.location?.postcode}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Street: ${user?.location?.street}")
            }
        }
    }
}






