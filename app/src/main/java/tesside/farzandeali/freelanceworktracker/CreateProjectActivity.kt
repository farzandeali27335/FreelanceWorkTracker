package tesside.farzandeali.freelanceworktracker

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import java.util.Calendar

class CreateProjectActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateProjectScreen {
                Toast.makeText(this, "Project Added", Toast.LENGTH_SHORT).show()
            }
//            HomeScreen(viewModel = ProjectViewModel(UserDataPrefs.getReaderEmail(this)))
        }
    }
}

// CreateProjectScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    onProjectSaved: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var clientContact by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Low") }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var subTasks by remember { mutableStateOf(listOf<SubTask>()) }
    var showAddSubTaskDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Project") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
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
                label = { Text("Client Contact Info") },
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

            // Start Date Picker
//            OutlinedTextField(
//                value = startDate,
//                onValueChange = {},
//                label = { Text("Start Date") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { showStartDatePicker = true },
//                readOnly = true
//            )

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

            // End Date Picker
//            OutlinedTextField(
//                value = endDate,
//                onValueChange = {},
//                label = { Text("End Date") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { showEndDatePicker = true },
//                readOnly = true
//            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Budget (INR)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Priority Chips
            Text("Priority")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                listOf("Low", "Medium", "High").forEach { level ->
                    FilterChip(
                        selected = priority == level,
                        onClick = { priority = level },
                        label = { Text(level) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showAddSubTaskDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add SubTask")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtask List
//            Text(text = "SubTasks:", style = MaterialTheme.typography.titleMedium)
//            subTasks.forEach { task ->
//                Text("- ${task.title} (Target: ${task.targetDate})")
//            }

            Text(text = "SubTasks:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            subTasks.forEachIndexed { index, task ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "- ${task.title} (Target: ${task.targetdate})",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        // Remove the subtask from the list
                        subTasks = subTasks.toMutableList().also { it.removeAt(index) }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete SubTask",
                            tint = Color.Red
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button (instead of FAB)

            Button(
                onClick = {
                    if (title.isBlank() ||
                        clientName.isBlank() ||
                        clientContact.isBlank() ||
                        description.isBlank() ||
                        startDate.isBlank() ||
                        endDate.isBlank() ||
                        budget.isBlank()
                    ) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    } else if (subTasks.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Please add at least one SubTask",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        saveProjectToFirebase(
                            title,
                            clientName,
                            clientContact,
                            description,
                            startDate,
                            endDate,
                            budget,
                            priority,
                            subTasks,
                            FreelancerPrefs.getUserEmail(context),
                            onSuccess = onProjectSaved
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
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

    if (showAddSubTaskDialog) {
        AddSubTaskDialog(
            onAdd = { subTask ->
                subTasks = subTasks + subTask
                showAddSubTaskDialog = false
            },
            onDismiss = { showAddSubTaskDialog = false }
        )
    }
}

// Reusable DatePicker
@Composable
fun showDatePicker(onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected("$dayOfMonth/${month + 1}/$year")
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Composable
fun AddSubTaskDialog(
    onAdd: (SubTask) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
//    var targetDate by remember { mutableStateOf("") }

    var targetDate by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add SubTask") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("SubTask Title") },
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
                        text = targetDate.ifEmpty { "Target Date" },
                        color = if (targetDate.isEmpty()) Color.Gray else Color.Black
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

            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank() && targetDate.isNotBlank()) {
                    onAdd(SubTask(title = title, targetdate = targetDate))
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )

    if (showStartDatePicker) {
        showDatePicker { date ->
            targetDate = date
            showStartDatePicker = false
        }
    }
}

//data class SubTask(
//    val title: String = "",
//    val targetDate: String = ""
//)

fun saveProjectToFirebase(
    title: String,
    clientName: String,
    clientContact: String,
    description: String,
    startDate: String,
    endDate: String,
    budget: String,
    priority: String,
    subTasks: List<SubTask>,
    currentEmail: String,
    onSuccess: () -> Unit
) {
    val database = FirebaseDatabase.getInstance().reference.child("projects")

    val userEmail = currentEmail.replace(".", "_")

    val userProjectsRef = database.child(userEmail)

    val projectId = userProjectsRef.push().key ?: return

    val project = mapOf(
        "id" to projectId,
        "title" to title,
        "clientName" to clientName,
        "clientContact" to clientContact,
        "description" to description,
        "startDate" to startDate,
        "endDate" to endDate,
        "budget" to budget,
        "priority" to priority,
        "subTasks" to subTasks.map { mapOf("title" to it.title, "targetDate" to it.targetdate) }
    )

    userProjectsRef.child(projectId).setValue(project)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener {
            // Handle error
        }
}



