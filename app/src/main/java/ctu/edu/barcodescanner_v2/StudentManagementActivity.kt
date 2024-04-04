package ctu.edu.barcodescanner_v2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class StudentManagementActivity : AppCompatActivity(), OnStudentItemClickListener {
    private lateinit var adapter: StudentAdapter
    private lateinit var studentList: MutableList<Student>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_management)

        val studentManageButton = findViewById<Button>(R.id.addNewStudentButton)

        studentManageButton.setOnClickListener{
            startActivity(Intent(this, StudentInputFormActivity::class.java))
        }


        val recyclerView = findViewById<RecyclerView>(R.id.rvStudentList)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Initialize eventList with an empty list
        studentList = mutableListOf<Student>()



        // Retrieve events first, then set the adapter with the retrieved data
        retrieveEventsFromFirestore()
    }

    override fun onItemClick(student: Student) {
        val intent = Intent(this, Activity_entry::class.java)
        startActivity(intent)
    }
    private fun retrieveEventsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val eventsRef = db.collection("student")

        eventsRef.get()
            .addOnSuccessListener { querySnapshot ->
                // Xóa dữ liệu cũ
                studentList.clear()

                // Lặp qua tất cả các tài liệu và thêm vào danh sách sự kiện
                for (doc in querySnapshot.documents) {
                    val studentName = doc.getString("name")
                    val studentID = doc.getString("id")
                    val studentMajor = doc.getString("major")
                    val studentCourse = doc.getString("course")
                    val studentEmail = doc.getString("email")



                    if (studentName != null && studentID != null && studentMajor != null &&
                        studentCourse != null && studentEmail != null) {

                        val student = Student(studentName, studentID, studentMajor, studentCourse, studentEmail)
                        studentList.add(student)

                        // In sự kiện ra màn hình console (để kiểm tra)
                        Log.d("StudentDetails", "Student: $student")
                    }
                }

                // Cập nhật adapter sau khi đã lấy dữ liệu
                adapter = StudentAdapter(studentList)
                adapter.setOnItemClickListener(this)
                val recyclerView = findViewById<RecyclerView>(R.id.rvStudentList)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Xử lý khi có lỗi xảy ra
                Log.e("EventListActivity", "Error retrieving events", exception)
            }
    }

}