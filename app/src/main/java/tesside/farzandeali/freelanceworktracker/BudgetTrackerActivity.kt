package tesside.farzandeali.freelanceworktracker

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BudgetTrackerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetTrackerScreen(
                projectId = SelectedProject.projectItem.id,
                budget = SelectedProject.projectItem.budget.toDouble()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTrackerScreen(projectId: String, budget: Double) {
    val context = LocalContext.current
    val emailId = FreelancerPrefs.getUserEmail(context).replace(".", ",")
    var payments by remember { mutableStateOf(listOf<PaymentEntry>()) }
    var showAddPaymentDialog by remember { mutableStateOf(false) }

    // Fetch Payments
    LaunchedEffect(Unit) {
        val database = FirebaseDatabase.getInstance().reference
            .child("projects")
            .child(emailId)
            .child(projectId)
            .child("payments")

        try {

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val paymentList = mutableListOf<PaymentEntry>()
                    for (child in snapshot.children) {
                        val payment = child.getValue(PaymentEntry::class.java)
                        payment?.let { paymentList.add(it) }
                    }
                    payments = paymentList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle Error
                }
            })
        } catch (e: Exception) {
            Log.e("Test", "Error : ${e.message}")
        }
    }

    val totalPaid = payments.sumOf { it.amount }
    val remaining = budget - totalPaid

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Track Payments",
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddPaymentDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Payment")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
//            Text("Budget: ₹$budget", style = MaterialTheme.typography.titleLarge)
//            Text("Total Paid: ₹$totalPaid", style = MaterialTheme.typography.titleLarge)
//            Text("Remaining: ₹$remaining", style = MaterialTheme.typography.titleLarge)

            BudgetSummaryCard(budget, totalPaid)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Payments:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (payments.isNotEmpty()) {
                payments.forEach { payment ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Date: ${payment.date}")
                            Text("Amount: ₹${payment.amount}")
                            Text("Notes: ${payment.notes}")
                        }
                    }
                }
            } else {
                Text("No payments added yet.")
            }
        }
    }

    if (showAddPaymentDialog) {
        AddPaymentDialog(
            onAdd = { newPayment ->
                val updatedPayments = payments + newPayment
                payments = updatedPayments

                // Save to Firebase
                val database = FirebaseDatabase.getInstance().reference
                    .child("projects")
                    .child(emailId)
                    .child(projectId)
                    .child("payments")

                database.push().setValue(newPayment)
                showAddPaymentDialog = false
            },
            onDismiss = { showAddPaymentDialog = false }
        )
    }
}


@Composable
fun BudgetSummaryCard(budget: Double, totalPaid: Double) {
    val remaining = budget - totalPaid
    val progress = if (budget > 0) (totalPaid / budget).toFloat() else 0f

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Budget Overview",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF0D47A1)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Budget, Paid, Remaining
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Budget", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "₹$budget", fontSize = 18.sp, color = Color.Black)
                }
                Column {
                    Text(text = "Paid", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "₹$totalPaid", fontSize = 18.sp, color = Color(0xFF2E7D32))
                }
                Column {
                    Text(text = "Remaining", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "₹$remaining", fontSize = 18.sp, color = Color(0xFFC62828))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                color = Color(0xFF64B5F6),
                trackColor = Color(0xFFBBDEFB),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(progress * 100).toInt()}% payment cleared",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}


@Composable
fun AddPaymentDialog(onAdd: (PaymentEntry) -> Unit, onDismiss: () -> Unit) {
    var date by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add Payment") },
        text = {
            Column {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (dd/mm/yyyy)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (date.isNotBlank() && amount.isNotBlank()) {
                    onAdd(
                        PaymentEntry(
                            date = date,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            notes = notes
                        )
                    )
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
}


data class PaymentEntry(
    val date: String = "",
    val amount: Double = 0.0,
    val notes: String = ""
)
