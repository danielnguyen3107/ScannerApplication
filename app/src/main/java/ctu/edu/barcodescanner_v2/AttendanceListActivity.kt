package ctu.edu.barcodescanner_v2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class AttendanceListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private var eventName: String? = null
    private lateinit var attendanceDetailList: MutableList<AttendanceDetail>
    private lateinit var adapter: ListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_list)

        listView = findViewById(R.id.listView)
        eventName = intent.getStringExtra("eventName")

        // SET TITLE
        // Truy xuất TextView có ID là listTitle
        val listTitleTextView = findViewById<TextView>(R.id.titleList)
        // Truyền giá trị eventName vào TextView
        listTitleTextView.text = eventName


        // Initialize Attendance adapter
        attendanceDetailList = mutableListOf()

        // Initialize custom adapter
        adapter = ListViewAdapter(this, attendanceDetailList)

        // Set the adapter to the ListView
        listView.adapter = adapter

        // Retrieve data from Firestore
        retrieveStudentData()
    }

    private fun retrieveStudentData() {
        val db = FirebaseFirestore.getInstance()
        val eventsRef = db.collection("barcodes")

        eventsRef.whereEqualTo("eventName", eventName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot.documents) {
                    val eventName = doc.getString("eventName")
                    val timeCheckRaw = doc.getTimestamp("timestamp")
                    val id = doc.getString("value")

                    val timeCheck = timeCheckRaw?.toDate()

                    if (eventName != null && id != null && timeCheck != null) {
                        val attendanceDetail = AttendanceDetail(eventName, id, timeCheck)
                        attendanceDetailList.add(attendanceDetail)
                    }
                }
                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("EventListActivity", "Error retrieving events", exception)
            }
    }
}
