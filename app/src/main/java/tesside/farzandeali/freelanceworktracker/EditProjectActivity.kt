package tesside.farzandeali.freelanceworktracker

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

class EditProjectActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val userEmail = FreelancerPrefs.getUserEmail(this)

            val updateMail = userEmail.replace(".", "_")

            EditProjectScreen(
                project = SelectedProject.projectItem,
                onProjectUpdated = {
                    updateProjectInFirebase(it, updateMail, onSuccess = {
                        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show()
                    }, onFailure = {
                        Toast.makeText(this, "Failed to update project", Toast.LENGTH_SHORT).show()
                    })
                },
                onCancel = {
                    finish()
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProjectScreen(
    project: Project,
    onProjectUpdated: (Project) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(project.title) }
    var clientName by remember { mutableStateOf(project.clientName) }
    var clientContact by remember { mutableStateOf(project.clientContact) }
    var description by remember { mutableStateOf(project.description) }
    var startDate by remember { mutableStateOf(project.startDate) }
    var endDate by remember { mutableStateOf(project.endDate) }
    var budget by remember { mutableStateOf(project.budget) }
    var priority by remember { mutableStateOf(project.priority) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Project") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Cancel")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Project Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = clientName,
                onValueChange = { clientName = it },
                label = { Text("Client Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = clientContact,
                onValueChange = { clientContact = it },
                label = { Text("Client Contact") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(50.dp)
                    .background(Color.LightGray, MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = startDate.ifEmpty { "Start Date" },
                    color = if (startDate.isEmpty()) Color.Gray else Color.Black
                )
                Icon(
                    imageVector = Icons.Default.DateRange, // Replace with your desired icon
                    contentDescription = "Date Icon",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(24.dp)
                        .clickable {
                            showStartDatePicker = true
                        },
                    tint = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(50.dp)
                    .background(Color.LightGray, MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = endDate.ifEmpty { "End Date" },
                    color = if (endDate.isEmpty()) Color.Gray else Color.Black
                )
                Icon(
                    imageVector = Icons.Default.DateRange, // Replace with your desired icon
                    contentDescription = "Date Icon",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(24.dp)
                        .clickable {
                            showEndDatePicker = true
                        },
                    tint = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Budget") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = priority,
                onValueChange = { priority = it },
                label = { Text("Priority (Low/Medium/High)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isBlank() ||
                        clientName.isBlank() ||
                        clientContact.isBlank() ||
                        description.isBlank() ||
                        startDate.isBlank() ||
                        endDate.isBlank() ||
                        budget.isBlank() ||
                        priority.isBlank()
                    ) {
                        val updatedProject = project.copy(
                            title = title,
                            clientName = clientName,
                            clientContact = clientContact,
                            description = description,
                            startDate = startDate,
                            endDate = endDate,
                            budget = budget,
                            priority = priority
                        )
                        onProjectUpdated(updatedProject)
                    } else {
                        Toast.makeText(context, "Fields Missing", Toast.LENGTH_SHORT).show()
                    }

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }

    if (showStartDatePicker) {
        showDatePicker { date ->
            startDate = date
            showStartDatePicker = false
        }
    }
    if (showEndDatePicker) {
        showDatePicker { date ->
            endDate = date
            showEndDatePicker = false
        }
    }
}


fun updateProjectInFirebase(
    project: Project,
    email: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    try {

        Log.e("Test", "Sent Mail : $email")

        val database = FirebaseDatabase.getInstance().reference
            .child("projects")
            .child(email)

        database.child(project.id).setValue(project)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Unknown error") }
    } catch (e: Exception) {
        Log.e("Test", "Error : ${e.message}")
    }
}

