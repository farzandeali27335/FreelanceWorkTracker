package tesside.farzandeali.freelanceworktracker

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class ManageSubTaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val userEmail = FreelancerPrefs.getUserEmail(this).replace(".", "_")

//            ManageSubtasksScreen(project = SelectedProject.projectItem) {
//                updateSubtaskStatusInFirebase(SelectedProject.projectItem.id,userEmail,it)
//            }

            var project = SelectedProject.projectItem

            ManageSubtasksScreen(
                project = project,
                onSubtaskUpdated = { updatedSubtask ->
                    val updatedList = project.subTasks.map {
                        if (it.id == updatedSubtask.id) updatedSubtask else it
                    }

                    // Update locally
                    project = project.copy(subTasks = updatedList)

                    // Update Firebase
                    updateSubtaskStatusInFirebase(
                        projectId = project.id,
                        userEmail = FreelancerPrefs.getUserEmail(this),
                        updatedSubtasks = updatedList,
                        onSuccess = {
                            Toast.makeText(this, "Subtask status updated", Toast.LENGTH_SHORT)
                                .show()
                        },
                        onFailure = {
                            Toast.makeText(this, "Failed: $it", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSubtasksScreen(
    project: Project,
    onSubtaskUpdated: (SubTask) -> Unit
) {
    var subtasks by remember { mutableStateOf(project.subTasks) }

//    var subTasks by remember { mutableStateOf(listOf<SubTask>()) }
    var showAddSubTaskDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current


    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sub Tasks Details",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (subtasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No Subtasks available",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(subtasks) { subtask ->
                        SubtaskItem(subtask = subtask, onStatusChange = { updatedSubtask ->
                            // Update the local list
                            subtasks = subtasks.map {
                                if (it.id == updatedSubtask.id) updatedSubtask else it
                            }
                            onSubtaskUpdated(updatedSubtask)
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showAddSubTaskDialog = true

                    }
                    .background(
                        color = colorResource(id = R.color.black),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.black),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                text = "Add SubTask",
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showAddSubTaskDialog) {
        AddSubTaskDialog(
            onAdd = { subTask ->
//                subTasks = subTasks + subTask

                addSubtaskToProject(
                    project,
                    subTask,
                    FreelancerPrefs.getUserEmail(context).replace(".", "_"),
                    onSuccess = {
                        Toast.makeText(context, "SubTask Added Successfully", Toast.LENGTH_SHORT)
                            .show()
                        (context as Activity).finish()
                    },
                    onFailure = {})
                showAddSubTaskDialog = false

            },
            onDismiss = { showAddSubTaskDialog = false }
        )
    }
}

@Composable
fun SubtaskItem(
    subtask: SubTask,
    onStatusChange: (SubTask) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = subtask.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Target Date: ${subtask.targetdate}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Status: ${subtask.status}")

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedButton(
                onClick = { expanded = true }
            ) {
                Text("Change Status")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("Not Started", "In Progress", "Completed").forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status) },
                        onClick = {
                            expanded = false
                            onStatusChange(subtask.copy(status = status))
                        }
                    )
                }
            }
        }
    }
}

data class SubTask(
    val id: String = UUID.randomUUID().toString(), // Unique ID for subtasks
    val title: String = "",
    val targetdate: String = "",
    val status: String = "Not Started" // default status
)


fun updateSubtaskStatusInFirebase(
    projectId: String,
    userEmail: String,
    updatedSubtasks: List<SubTask>,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val database = FirebaseDatabase.getInstance().reference
    val emailKey = userEmail.replace(".", "_") // Firebase cannot have dots in keys

    val updatedSubtaskMaps = updatedSubtasks.map {
        mapOf(
            "id" to it.id,
            "title" to it.title,
            "targetdate" to it.targetdate,
            "status" to it.status
        )
    }

    database.child("projects")
        .child(emailKey)
        .child(projectId)
        .child("subTasks")
        .setValue(updatedSubtaskMaps)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it.message ?: "Unknown error") }
}
