package tesside.farzandeali.freelanceworktracker

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TimeLineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TimelineScreen(project = SelectedProject.projectItem)
        }
    }
}

@Composable
fun TimelineScreen(project: Project) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Blue)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        (context as Activity).finish()
                    },
                painter = painterResource(id = R.drawable.baseline_arrow_back_36),
                contentDescription = ""
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                "Timeline View",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )


        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {


            Text(
                text = "Timeline View - ${project.title}",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            if (project.subTasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No subtasks available",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(project.subTasks.sortedBy { it.targetdate }) { subTask ->
                        TimelineItem(subTask)
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineItem(subTask: SubTask) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.Blue, CircleShape)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(40.dp)
                    .background(Color.Gray)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = subTask.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Target: ${subTask.targetdate}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Status: ${subTask.status}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
        }
    }
}
