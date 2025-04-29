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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen()
        }
    }
}


@Composable
fun RegisterScreen() {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val genderOptions = listOf("Male", "Female", "Other")
    var selectedGender by remember { mutableStateOf("Male") }

    val context = LocalContext.current as Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Soft background
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
                text = "Hey,\nRegister Now!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Full Name Input
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Full Name Icon"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gender Selection
            Text(
                text = "Select Gender",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                genderOptions.forEach { gender ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        RadioButton(
                            selected = (gender == selectedGender),
                            onClick = { selectedGender = gender }
                        )
                        Text(
                            text = gender,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email Input
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
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
                    modifier = Modifier.fillMaxWidth()
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
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Input
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confirm Password Icon"
                        )
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Register Button
            Button(
                onClick = {
                    if (fullName.isEmpty()) {
                        Toast.makeText(context, "Enter FullName", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (email.isEmpty()) {
                        Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password.isEmpty()) {
                        Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val customerData = Freelancer(
                        fullName,
                        email,
                        selectedGender,
                        password
                    )

                    customerRegistration(customerData, context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text("Register", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Link
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E88E5)
                    ),
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, SignInActivity::class.java))
                        context.finish()
                    }
                )
            }
        }
    }
}


fun customerRegistration(customerData: Freelancer, context: Context) {

    val firebaseDatabase = FirebaseDatabase.getInstance()
    val databaseReference = firebaseDatabase.getReference("FreelancerData")

    databaseReference.child(customerData.emailid.replace(".", ","))
        .setValue(customerData)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "You Registered Successfully", Toast.LENGTH_SHORT)
                    .show()

                context.startActivity(Intent(context, SignInActivity::class.java))
                (context as Activity).finish()

            } else {
                Toast.makeText(
                    context,
                    "Registration Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        .addOnFailureListener { _ ->
            Toast.makeText(
                context,
                "Something went wrong",
                Toast.LENGTH_SHORT
            ).show()
        }
}
