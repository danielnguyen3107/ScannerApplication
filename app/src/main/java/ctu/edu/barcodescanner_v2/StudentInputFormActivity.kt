package ctu.edu.barcodescanner_v2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import ctu.edu.barcodescanner_v2.databinding.ActivityAddNewEventBinding
import ctu.edu.barcodescanner_v2.databinding.ActivityStudentInputFormBinding
import java.util.Date

class StudentInputFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentInputFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentInputFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitStudentEventButton.setOnClickListener {
            // Call a function to save event data to Firestore
            saveEventDataToFirestore()

            val intent = Intent(this, Activity_entry::class.java)
            startActivity(intent)
        }
    }

    private fun saveEventDataToFirestore() {
        // Retrieve event data from EditText fields and create an Event object
        val studentName = binding.nameTextView.text.toString()
        val studentID = binding.idTextView.text.toString()
        val studentMajor = binding.majorTextView.text.toString()
        val studentCourse = binding.courseTextView.text.toString()
        val studentEmail = binding.emailTextView.text.toString()
        // Log the event details
        Log.d("StudentDetails", "Student Name: $studentName")
        Log.d("StudentDetails", "Student ID: $studentID")
        Log.d("StudentDetails", "Student Major: $studentMajor")
        Log.d("StudentDetails", "Student Course: $studentCourse")
        Log.d("StudentDetails", "Student Email: $studentEmail")

        // Create an Event object with the retrieved data
        val student = Student(studentName, studentID, studentMajor, studentCourse, studentEmail )

        // Save the event data to Firestore
        // You need to implement this part based on your Firestore setup
        // Add the event object to Firestore in the "events" collection
        val db = FirebaseFirestore.getInstance()
        db.collection("student")
            .add(student)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Event added with ID: ${documentReference.id}")
                // Clear EditText fields or show a success message
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding event", e)
                // Show an error message
            }
    }
}