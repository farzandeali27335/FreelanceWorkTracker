package tesside.farzandeali.freelanceworktracker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }
    }
}

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current as Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF5F5F5)) // Light background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            // App Logo
            Image(
                painter = painterResource(id = R.drawable.freelancer_appicon),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color.LightGray, CircleShape)
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )

            Text(
                text = "Login to your account",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // Email Input
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon"
                        )
                    },
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon"
                        )
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        val customerData = Freelancer(
                            "",
                            email,
                            "",
                            password
                        )
                        customerLogin(customerData, context)
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E88E5) // Nice blue
                )
            ) {
                Text("Login", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Register Now
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Register",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E88E5)
                    ),
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, SignupActivity::class.java))
                        context.finish()
                    }
                )
            }
        }
    }
}


fun customerLogin(customerData: Freelancer, context: Context) {

    val firebaseDatabase = FirebaseDatabase.getInstance()
    val databaseReference = firebaseDatabase.getReference("FreelancerData").child(customerData.emailid.replace(".", ","))

    databaseReference.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val dbData = task.result?.getValue(Freelancer::class.java)
            if (dbData != null) {
                if (dbData.password == customerData.password) {

                    FreelancerPrefs.setUserSessionActive(context, true)
                    FreelancerPrefs.saveUserEmail(context, dbData.emailid)
                    FreelancerPrefs.saveUserEmail(context, dbData.name)

                    Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()

                    context.startActivity(Intent(context, HomeScreenActivity::class.java))
                    (context as Activity).finish()
                } else {
                    Toast.makeText(context, "Seems Incorrect Credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Your account not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(
                context,
                "Something went wrong",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}

data class Freelancer(
    var name: String = "",
    var emailid: String = "",
    var area: String = "",
    var password: String = ""
)