package tesside.farzandeali.freelanceworktracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase

class ProjectDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProjectDetailsScreen(project = SelectedProject.projectItem,
                onBackClick = {
                    finish()
                }, onEditClick = {
                    val intent = Intent(this, EditProjectActivity::class.java)
                    startActivity(intent)
                })
        }
    }
}

object SelectedProject {
    var projectItem = Project()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(project: Project, onBackClick: () -> Unit, onEditClick: () -> Unit) {

    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Project Details",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEditClick() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Project")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Project Title
            Text(
                text = project.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0D47A1)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    text = "Download\nReport",
                    onClick = {
                        generateProjectReportPDF(
                            context,
                            SelectedProject.projectItem,
                            onSuccess = {
                                Toast.makeText(context, "Report Downloaded", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            onFailure = {
                                Toast.makeText(
                                    context,
                                    "Failed to download report",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                )

                Column {


                    ActionButton(
                        text = "Update\nStatus",
                        onClick = { expanded = true }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf(
                            "Not Started", "In Progress", "On Hold",
                            "Partially Completed", "Completed", "Cancelled"
                        ).forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    expanded = false
                                    updateProjectStatusInFirebase(
                                        projectId = SelectedProject.projectItem.id,
                                        email = FreelancerPrefs.getUserEmail(context),
                                        newStatus = status,
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Project status updated",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            (context as Activity).finish()
                                        },
                                        onFailure = {
                                            Toast.makeText(
                                                context,
                                                "Failed to update: $it",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            )
                        }
                    }
                }

                // Budget Tracker
                ActionButton(
                    text = "Track\nPayments",
                    onClick = {
                        context.startActivity(Intent(context, BudgetTrackerActivity::class.java))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Project Status + Delete
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status: ${project.status}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.weight(1f))

                if (project.status == "Completed") {
                    ActionButton(
                        text = "Delete Project",
                        onClick = {
                            deleteProjectFromFirebase(
                                projectId = SelectedProject.projectItem.id,
                                userEmail = FreelancerPrefs.getUserEmail(context),
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Deleted Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    (context as Activity).finish()
                                },
                                onFailure = {
                                    Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        },
                        backgroundColor = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Project Info
            InfoSection(title = "Client Name", value = project.clientName)
            InfoSection(title = "Client Contact", value = project.clientContact)
            InfoSection(title = "Description", value = project.description)
            InfoSection(title = "Start Date", value = project.startDate)
            InfoSection(title = "End Date", value = project.endDate)
            InfoSection(title = "Budget", value = "£${project.budget}")
            InfoSection(title = "Priority", value = project.priority)


            Spacer(modifier = Modifier.height(24.dp))

            // Subtasks Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subtasks",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.weight(1f))
                ActionButton(
                    text = "Manage",
                    onClick = {
                        context.startActivity(Intent(context, ManageSubTaskActivity::class.java))
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                ActionButton(
                    text = "Timeline",
                    onClick = {
                        context.startActivity(Intent(context, TimeLineActivity::class.java))
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subtasks List
            if (project.subTasks.isNotEmpty()) {
                project.subTasks.forEach { subTask ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Title: ${subTask.title}", fontWeight = FontWeight.Medium)
                            Text(text = "Target Date: ${subTask.targetdate}")
                            Text(text = "Status: ${subTask.status}")
                        }
                    }
                }
            } else {
                Text(
                    text = "No Subtasks Available",
                    fontStyle = FontStyle.Italic,
                    color = Color.Gray
                )
            }
        }

    }


}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color = colorResource(id = R.color.black)
) {
    Text(
        text = text,
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}


@Composable
fun InfoSection(title: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}


fun deleteProjectFromFirebase(
    projectId: String,
    userEmail: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val encodedEmail = userEmail.replace(".", "_")
    val database = FirebaseDatabase.getInstance().reference
        .child("projects")
        .child(encodedEmail)
        .child(projectId)

    database.removeValue()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Failed to delete project")
        }
}


fun addSubtaskToProject(
    project: Project,
    subTask: SubTask,
    emailId: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val database = FirebaseDatabase.getInstance().reference
//    val emailId = UserDataPrefs.getReaderEmail(GlobalAppContext.getContext()).replace(".", "_")

    val projectRef = database.child("projects").child(emailId).child(project.id)

    // Add new subtask to existing list
    val updatedSubtasks = project.subTasks.toMutableList()
    updatedSubtasks.add(subTask)

    val updatedData = mapOf(
        "subTasks" to updatedSubtasks.map {
            mapOf(
                "title" to it.title,
                "targetdate" to it.targetdate,
                "status" to it.status
            )
        }
    )

    projectRef.updateChildren(updatedData)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it.message ?: "Failed to add subtask") }
}

fun updateProjectStatusInFirebase(
    projectId: String,
    email: String,
    newStatus: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val database = FirebaseDatabase.getInstance().reference
    val projectRef = database.child("projects").child(email.replace(".", "_")).child(projectId)

    val updates = mapOf<String, Any>(
        "status" to newStatus
    )

    projectRef.updateChildren(updates)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Unknown error occurred")
        }
}


//Column(
//modifier = Modifier
//.fillMaxSize()
//.padding(paddingValues)
//.verticalScroll(rememberScrollState())
//.padding(16.dp)
//) {
//    Text(text = project.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
//
//    Spacer(modifier = Modifier.height(6.dp))
//
//    Row {
//
//        Text(
//            modifier = Modifier
//                .clickable {
//                    generateProjectReportPDF(
//                        context,
//                        SelectedProject.projectItem,
//                        onSuccess = {
//                            Toast
//                                .makeText(context, "Report Downloaded", Toast.LENGTH_SHORT)
//                                .show()
//                        },
//                        onFailure = {
//                            Toast
//                                .makeText(
//                                    context,
//                                    "Failed to download report",
//                                    Toast.LENGTH_SHORT
//                                )
//                                .show()
//
//                        })
//                }
//                .background(
//                    color = colorResource(id = R.color.black),
//                    shape = RoundedCornerShape(6.dp)
//                )
//                .border(
//                    width = 1.dp,
//                    color = colorResource(id = R.color.black),
//                    shape = RoundedCornerShape(6.dp)
//                )
//                .padding(horizontal = 6.dp, vertical = 4.dp),
//            text = "Download Report",
//            textAlign = TextAlign.Center,
//            color = Color.White,
//            fontSize = 14.sp,
//            fontWeight = FontWeight.Bold
//        )
//
//
//        Column {
//
//            Text(
//                modifier = Modifier
//                    .clickable {
//                        expanded = true
//                    }
//                    .background(
//                        color = colorResource(id = R.color.black),
//                        shape = RoundedCornerShape(6.dp)
//                    )
//                    .border(
//                        width = 1.dp,
//                        color = colorResource(id = R.color.black),
//                        shape = RoundedCornerShape(6.dp)
//                    )
//                    .padding(horizontal = 6.dp, vertical = 4.dp),
//                text = "Update Status",
//                textAlign = TextAlign.Center,
//                color = Color.White,
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Bold
//            )
//
//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false }
//            ) {
//                listOf(
//                    "Not Started",
//                    "In Progress ",
//                    "On Hold",
//                    "Partially Completed",
//                    "Completed",
//                    "Cancelled"
//                ).forEach { status ->
//                    DropdownMenuItem(
//                        text = { Text(status) },
//                        onClick = {
//                            expanded = false
////                            onStatusChange(subtask.copy(status = status))
//
//                            updateProjectStatusInFirebase(
//                                projectId = SelectedProject.projectItem.id,
//                                email = UserDataPrefs.getReaderEmail(context),
//                                newStatus = status,
//                                onSuccess = {
//                                    Toast.makeText(
//                                        context,
//                                        "Project status updated successfully",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                },
//                                onFailure = { error ->
//                                    Toast.makeText(
//                                        context,
//                                        "Failed to update status: $error",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            )
//
//
//                        }
//                    )
//                }
//            }
//
//        }
//    }
//
//    Row {
//        Text(text = "Project Status: ${project.status}")
//
//        if (project.status == "Completed")
//            Text(
//                modifier = Modifier
//                    .clickable {
//
//                        deleteProjectFromFirebase(
//                            projectId = SelectedProject.projectItem.id,
//                            userEmail = UserDataPrefs.getReaderEmail(context),
//                            onSuccess = {
//                                Toast
//                                    .makeText(
//                                        context,
//                                        "Project deleted successfully",
//                                        Toast.LENGTH_SHORT
//                                    )
//                                    .show()
//                                (context as Activity).finish()
//                            },
//                            onFailure = {
//                                Toast
//                                    .makeText(
//                                        context,
//                                        "Failed to delete project",
//                                        Toast.LENGTH_LONG
//                                    )
//                                    .show()
//                                (context as Activity).finish()
//                            }
//                        )
//
//                    }
//                    .background(
//                        color = colorResource(id = R.color.black),
//                        shape = RoundedCornerShape(6.dp)
//                    )
//                    .border(
//                        width = 1.dp,
//                        color = colorResource(id = R.color.black),
//                        shape = RoundedCornerShape(6.dp)
//                    )
//                    .padding(horizontal = 6.dp, vertical = 4.dp),
//                text = "Delete Project",
//                textAlign = TextAlign.Center,
//                color = Color.White,
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Bold
//            )
//    }
//
//    Spacer(modifier = Modifier.height(12.dp))
//
//    Text(text = "Client Name: ${project.clientName}")
//    Text(text = "Client Contact: ${project.clientContact}")
//    Spacer(modifier = Modifier.height(8.dp))
//
//    Text(text = "Description:", fontWeight = FontWeight.SemiBold)
//    Text(text = project.description)
//    Spacer(modifier = Modifier.height(8.dp))
//
//    Text(text = "Start Date: ${project.startDate}")
//    Text(text = "End Date: ${project.endDate}")
//    Spacer(modifier = Modifier.height(8.dp))
//
//    Row(verticalAlignment = Alignment.CenterVertically) {
//
//
//        Text(text = "Budget: £${project.budget}")
//
//        Spacer(modifier = Modifier.weight(1f))
//
//
//        Text(
//            modifier = Modifier
//                .clickable {
//                    context.startActivity(
//                        Intent(
//                            context,
//                            BudgetTrackerActivity::class.java
//                        )
//                    )
//                }
//                .background(
//                    color = colorResource(id = R.color.black),
//                    shape = RoundedCornerShape(6.dp)
//                )
//                .border(
//                    width = 1.dp,
//                    color = colorResource(id = R.color.black),
//                    shape = RoundedCornerShape(6.dp)
//                )
//                .padding(horizontal = 6.dp, vertical = 4.dp),
//            text = "Track Payments",
//            textAlign = TextAlign.Center,
//            color = Color.White,
//            fontSize = 14.sp,
//            fontWeight = FontWeight.Bold
//        )
//    }
//
//    Text(text = "Priority: ${project.priority}")
//    Spacer(modifier = Modifier.height(16.dp))
//
//    Row(verticalAlignment = Alignment.CenterVertically) {
//
//        Text(text = "Subtasks", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        Text(
//            modifier = Modifier
//                .clickable {
//                    context.startActivity(
//                        Intent(
//                            context,
//                            ManageSubTaskActivity::class.java
//                        )
//                    )
//                }
//                .background(
//                    color = colorResource(id = R.color.black),
//                    shape = RoundedCornerShape(6.dp)
//                )
//                .border(
//                    width = 1.dp,
//                    color = colorResource(id = R.color.black),
//                    shape = RoundedCornerShape(6.dp)
//                )
//                .padding(horizontal = 6.dp, vertical = 4.dp),
//            text = "Manage",
//            textAlign = TextAlign.Center,
//            color = Color.White,
//            fontSize = 14.sp,
//            fontWeight = FontWeight.Bold
//        )
//
//        Spacer(modifier = Modifier.width(12.dp))
//
//        Text(
//            modifier = Modifier
//                .clickable {
//                    context.startActivity(
//                        Intent(
//                            context,
//                            TimeLineActivity::class.java
//                        )
//                    )
//                }
//                .background(
//                    color = colorResource(id = R.color.black),
//                    shape = RoundedCornerShape(6.dp)
//                )
//                .border(
//                    width = 1.dp,
//                    color = colorResource(id = R.color.black),
//                    shape = RoundedCornerShape(6.dp)
//                )
//                .padding(horizontal = 6.dp, vertical = 4.dp),
//            text = "Timeline",
//            textAlign = TextAlign.Center,
//            color = Color.White,
//            fontSize = 14.sp,
//            fontWeight = FontWeight.Bold
//        )
//
//
//    }
//
//    Spacer(modifier = Modifier.height(8.dp))
//
//    if (project.subTasks.isNotEmpty()) {
//        project.subTasks.forEach { subTask ->
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 4.dp),
//                elevation = CardDefaults.cardElevation(4.dp)
//            ) {
//                Column(modifier = Modifier.padding(12.dp)) {
//                    Text(text = "Title: ${subTask.title}", fontWeight = FontWeight.Medium)
//                    Text(text = "Target Date: ${subTask.targetdate}")
//                    Text(text = "Status: ${subTask.status}")
//                }
//            }
//        }
//    } else {
//        Text(text = "No Subtasks", fontStyle = FontStyle.Italic)
//    }
//}