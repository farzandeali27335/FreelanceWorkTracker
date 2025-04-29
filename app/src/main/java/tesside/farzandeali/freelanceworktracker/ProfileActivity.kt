package tesside.farzandeali.freelanceworktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProfileScreen(FreelancerPrefs.getUserName(this), FreelancerPrefs.getUserEmail(this))
        }
    }
}

@Composable
fun ProfileScreen(userName: String, userEmail: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Profile Icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFF2196F3))
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Icon",
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Name
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // User Email
        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Divider(color = Color.Gray, thickness = 1.dp)

        Spacer(modifier = Modifier.height(20.dp))

        // Contact Us
        Text(
            text = "Contact Us",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Farzand e Ali",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            modifier = Modifier.align(Alignment.Start)
        )

        Text(
            text = "Email: E4383983@live.tees.ac.uk",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Divider(color = Color.Gray, thickness = 1.dp)

        Spacer(modifier = Modifier.height(20.dp))

        // About Us
        Text(
            text = "About Us",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome to the Freelancer Work Tracker App – your ultimate tool for managing freelance projects and staying on top of deadlines.\n\n" +
                    "Developed by Farzand e Ali, this app is specifically designed to help freelancers streamline their workflow, track time, and manage multiple projects effortlessly.\n\n" +
                    "Thank you for choosing us to help you manage your freelance journey!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            modifier = Modifier.align(Alignment.Start)
        )
    }
}
