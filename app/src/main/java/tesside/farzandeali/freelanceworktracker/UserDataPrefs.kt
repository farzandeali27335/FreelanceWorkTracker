package tesside.farzandeali.freelanceworktracker

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.OutputStream

object FreelancerPrefs {

    private const val PREFS_NAME = "FREELANCE_TRACKER_PREFS"
    private const val KEY_SESSION_ACTIVE = "FREELANCER_SESSION_ACTIVE"
    private const val KEY_USER_NAME = "FREELANCER_USER_NAME"
    private const val KEY_USER_EMAIL = "FREELANCER_USER_EMAIL"
    private const val KEY_USER_PHOTO = "FREELANCER_USER_PHOTO"

    fun setUserSessionActive(context: Context, isActive: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SESSION_ACTIVE, isActive).apply()
    }

    fun isUserSessionActive(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SESSION_ACTIVE, false)
    }

    fun saveUserName(context: Context, name: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_NAME, "") ?: ""
    }

    fun saveUserEmail(context: Context, email: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_EMAIL, "freelancer@example.com") ?: "freelancer@example.com"
    }

    fun saveUserPhoto(context: Context, photoUrl: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_PHOTO, photoUrl).apply()
    }

    fun getUserPhoto(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_PHOTO, "") ?: ""
    }
}


fun generateProjectReportPDF(
    context: Context,
    project: Project,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            textSize = 24f
            color = Color.BLACK
        }

        val sectionHeaderPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            textSize = 18f
            color = Color.DKGRAY
        }

        val contentPaint = Paint().apply {
            textSize = 14f
            color = Color.BLACK
        }

        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 2f
        }

        var yPosition = 50f

        fun drawLine() {
            yPosition += 10f
            canvas.drawLine(40f, yPosition, 555f, yPosition, linePaint)
            yPosition += 20f
        }

        fun drawText(text: String, paint: Paint) {
            canvas.drawText(text, 40f, yPosition, paint)
            yPosition += paint.textSize + 12f
        }

        // 1. Main Title
        drawText("Freelance Project Report", titlePaint)
        drawLine()

        // 2. Project Details
        drawText("Project Details", sectionHeaderPaint)

        drawText("Title: ${project.title}", contentPaint)
        drawText("Client Name: ${project.clientName}", contentPaint)
        drawText("Client Contact: ${project.clientContact}", contentPaint)
        drawText("Description: ${project.description}", contentPaint)
        drawText("Start Date: ${project.startDate}", contentPaint)
        drawText("End Date: ${project.endDate}", contentPaint)
        drawText("Budget: ₹${project.budget}", contentPaint)
        drawText("Priority: ${project.priority}", contentPaint)

        drawLine()

        // 3. SubTasks
        if (!project.subTasks.isNullOrEmpty()) {
            drawText("Subtasks", sectionHeaderPaint)

            project.subTasks.forEachIndexed { index, subtask ->
                drawText("${index + 1}. ${subtask.title} [${subtask.status}]", contentPaint)
            }

            drawLine()
        }

        // 4. Payments
        if (!project.payments.isNullOrEmpty()) {
            drawText("Payments", sectionHeaderPaint)

            project.payments.forEachIndexed { index, payment ->
                drawText("${index + 1}. ₹${payment.amount} on ${payment.date} (${payment.notes})", contentPaint)
            }

            drawLine()
        } else {
            drawText("No Payments recorded yet", contentPaint)
            drawLine()
        }

        pdfDocument.finishPage(page)

        // 5. Save to Downloads with Project Name and Timestamp
        val outputStream = ByteArrayOutputStream()
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()

        val resolver = context.contentResolver
        val currentTime = System.currentTimeMillis()
        val fileName = "${project.title}_${currentTime}.pdf"

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            val os: OutputStream? = resolver.openOutputStream(uri)
            os?.use { it.write(outputStream.toByteArray()) }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            // Open the Downloads folder after saving
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                data = uri
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            }
//            context.startActivity(intent)

            onSuccess(fileName) // 📄 Success
        } else {
            onFailure("Failed to create file in Downloads")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        onFailure(e.message ?: "Error generating PDF")
    }
}
