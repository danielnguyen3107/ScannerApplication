package ctu.edu.barcodescanner_v2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
            // Kiểm tra validateForm()
            if (validateForm()) {
                // Nếu form hợp lệ, tiến hành lưu dữ liệu và chuyển hướng Intent
                saveEventDataToFirestore()
                val intent = Intent(this, Activity_entry::class.java)
                startActivity(intent)
            } else {
                // Nếu form không hợp lệ, hiển thị thông báo lỗi
                Toast.makeText(this, "Bạn đã nhập thiếu, vui lòng kiểm tra !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveEventDataToFirestore() {
        // Retrieve event data from EditText fields and create an Event object
        val studentName = binding.nameEditTextView.text.toString()
        val studentID = binding.idEditTextView.text.toString()
        val studentMajor = binding.majorEditTextView.text.toString()
        val studentCourse = binding.courseEditTextView.text.toString()
        val studentEmail = binding.emailEditTextView.text.toString()
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

    // Validate form
    private fun validateForm(): Boolean {
        val studentName = binding.nameEditTextView.text.toString()
        val studentID = binding.idEditTextView.text.toString()
        val studentMajor = binding.majorEditTextView.text.toString()
        val studentCourse = binding.courseEditTextView.text.toString()
        val studentEmail = binding.emailEditTextView.text.toString()

        // Kiểm tra xem tất cả các trường có được điền đầy đủ không
        return studentName.isNotEmpty() && studentID.isNotEmpty() && studentMajor.isNotEmpty() && studentCourse.isNotEmpty() && studentEmail.isNotEmpty()
    }
}