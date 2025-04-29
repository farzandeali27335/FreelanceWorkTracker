package tesside.farzandeali.freelanceworktracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen(viewModel = ProjectViewModel(FreelancerPrefs.getUserEmail(this)))
        }
    }
}


data class Project(
    val id: String = "",
    val title: String = "",
    val status: String = "",
    val clientName: String = "",
    val clientContact: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val budget: String = "",
    val priority: String = "",
    val subTasks: List<SubTask> = emptyList(),
    val payments: List<PaymentEntry> = emptyList()
)


// ViewModel for managing projects
class ProjectViewModel(private val userEmail: String) : ViewModel() {

    private val database: DatabaseReference = Firebase.database.reference
    private val _projects = mutableStateListOf<Project>()
    val projects: List<Project> get() = _projects

    init {
        loadProjects()
    }

    fun loadProjects() {
        val safeEmail = userEmail.replace(".", "_") // Firebase doesn't allow "." in keys
        database.child("projects").child(safeEmail).get()
            .addOnSuccessListener { snapshot ->
                _projects.clear()
                for (projectSnapshot in snapshot.children) {
                    val project = projectSnapshot.getValue(Project::class.java)
                    project?.let { _projects.add(it) }
                }
            }
    }

    fun addProject(project: Project) {
        val safeEmail = userEmail.replace(".", "_")
        val newProjectRef = database.child("projects").child(safeEmail).push()
        newProjectRef.setValue(project)
    }
}


@Composable
fun HomeScreen(viewModel: ProjectViewModel) {

    val context = LocalContext.current as Activity

    var showAddProjectDialog by remember { mutableStateOf(false) }

    // Observe the lifecycle to reload projects on resume
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                viewModel.loadProjects()  // <-- Call to reload projects
            }
        })
    }

    if (showAddProjectDialog) {
        AddProjectDialog(
            onDismiss = { showAddProjectDialog = false },
            onAddProject = { project ->
                viewModel.addProject(project)
                showAddProjectDialog = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(WindowInsets.systemBars.asPaddingValues())) {
        Row(
            modifier = Modifier
                .background(color = Color.Blue)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        context.startActivity(Intent(context, ProfileActivity::class.java))
                    },
                painter = painterResource(id = R.drawable.baseline_account_circle_36),
                contentDescription = ""
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                "Freelance Work Tracker",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        FreelancerPrefs.setUserSessionActive(context, false)
                        context.startActivity(Intent(context, SignInActivity::class.java))
                        context.finish()
                    },
                painter = painterResource(id = R.drawable.baseline_logout_36),
                contentDescription = ""
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (viewModel.projects.isNotEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(viewModel.projects) { project ->
                        ProjectItem(project)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No Projects added till now",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.startActivity(
                            Intent(
                                context,
                                CreateProjectActivity::class.java
                            )
                        )
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
                text = "Create Project",
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun ProjectItem(project: Project) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = project.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0D47A1)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Client Name
            Text(
                text = "Client: ${project.clientName}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Dates Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Start: ${project.startDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "End: ${project.endDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Budget
            Text(
                text = "Budget: ₹${project.budget}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Priority Chip
            Row(modifier = Modifier.fillMaxWidth()) {

                Box(
                    modifier = Modifier
                        .background(
                            color = when (project.priority.lowercase()) {
                                "high" -> Color(0xFFD32F2F)
                                "medium" -> Color(0xFFFFA000)
                                else -> Color(0xFF388E3C)
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = project.priority.uppercase(),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .clickable {
                            SelectedProject.projectItem = project
                            val intent = Intent(context, ProjectDetailsActivity::class.java)
                            context.startActivity(intent)
                        }
                        .background(
                            color = Color.Black,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "View Project",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

}

@Composable
fun AddProjectDialog(onDismiss: () -> Unit, onAddProject: (Project) -> Unit) {
    var title by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Low") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Project") },
        text = {
            Column {


                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Client Name") })
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date") })
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End Date") })
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Budget") })
                DropdownMenu(
                    expanded = false,
                    onDismissRequest = {},
                    content = {
                        DropdownMenuItem(
                            text = { Text("Low") },
                            onClick = { priority = "Low" }
                        )

                        DropdownMenuItem(
                            text = { Text("Medium") },
                            onClick = { priority = "Medium" }
                        )

                        DropdownMenuItem(
                            text = { Text("High") },
                            onClick = { priority = "High" }
                        )

                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAddProject(
                        Project(
                            title = title,
                            clientName = clientName,
                            startDate = startDate,
                            endDate = endDate,
                            budget = budget.toDouble().toString(),
                            priority = priority
                        )
                    )
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
//    HomeScreen(viewModel = ProjectViewModel())
}
