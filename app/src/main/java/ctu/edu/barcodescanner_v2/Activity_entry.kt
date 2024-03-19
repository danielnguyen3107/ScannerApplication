package ctu.edu.barcodescanner_v2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.FileWriter

class Activity_entry : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        val currentEventButton = findViewById<Button>(R.id.currentEventButton)
        val addEventButton = findViewById<Button>(R.id.addEventButton)
        val eventAttendanceListButton = findViewById<Button>(R.id.eventAttendanceListButton)
        currentEventButton.setOnClickListener {
            startActivity(Intent(this, EventListActivity::class.java))
        }

        addEventButton.setOnClickListener {
            startActivity(Intent(this, AddNewEventActivity::class.java))
        }

        eventAttendanceListButton.setOnClickListener{
            startActivity(Intent(this, EventAttendanceListActivity::class.java))
        }

        val data = listOf(
            listOf("Name", "Age", "City"),
            listOf("John Doe", 30, "London"),
            listOf("Jane Doe", 25, "New York")
        )

        val file = File(filesDir, "data.csv")
        val writer = FileWriter(file)
// Write your data to the writer
        writer.close()

    }
}