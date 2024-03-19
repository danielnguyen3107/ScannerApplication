package ctu.edu.barcodescanner_v2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.firestore.FirebaseFirestore

class AttendanceListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private var eventName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_list)


        // Initialize listView by finding it from the layout
        listView = findViewById(R.id.listView)

        eventName = intent.getStringExtra("eventName")

        // Retrieve data from Firestore
        retrieveStudentData()

    }
    private fun retrieveStudentData() {
        val db = FirebaseFirestore.getInstance()
        val eventsRef = db.collection("barcodes")

        eventsRef.whereEqualTo("eventName", eventName) // Thêm điều kiện where
            .get()
            .addOnSuccessListener { querySnapshot ->
                val studentList = mutableListOf<String>() // List to hold event details strings

                // Iterate through each document and extract event details
                for (doc in querySnapshot.documents) {
                    val eventName = doc.getString("eventName")
                    val timeCheckRaw = doc.getTimestamp("timestamp")
                    val id = doc.getString("value")

                    val timeCheck = timeCheckRaw?.toDate()
                    // Process data and create event details string
                    val studentDetails = "Event: $eventName\nID: $id\nTime Check: $timeCheck"
                    studentList.add(studentDetails)
                    Log.d("StudentDetails", studentDetails)
                }

                // Create an ArrayAdapter to display the list of event details
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, studentList)

                // Set the adapter to the ListView
                listView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Log.e("EventListActivity", "Error retrieving events", exception)
            }
    }

//    private fun retrieveStudentData() {
//        val db = FirebaseFirestore.getInstance()
//        val eventsRef = db.collection("barcodes")
//
//        eventsRef.get()
//            .addOnSuccessListener { querySnapshot ->
//                val studentList = mutableListOf<String>() // List to hold event details strings
//
//                // Iterate through each document and extract event details
//                for (doc in querySnapshot.documents) {
//                    val eventName = doc.getString("eventName")
//                    val timeCheckRaw = doc.getTimestamp("timestamp")
//                    val id = doc.getString("value")
//
//
//                    val timeCheck = timeCheckRaw?.toDate()
//                    // Process data and create event details string
//                    val studentDetails = "Event: $eventName\nID: $id\nTime Check: $timeCheck"
//                    studentList.add(studentDetails)
//                    Log.d("StudentDetails", studentDetails)
//                }
//
//                // Create an ArrayAdapter to display the list of event details
//                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, studentList)
//
//                // Set the adapter to the ListView
//                listView.adapter = adapter
//            }
//            .addOnFailureListener { exception ->
//                // Handle errors
//                Log.e("EventListActivity", "Error retrieving events", exception)
//            }
//    }

}