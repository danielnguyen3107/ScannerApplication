package ctu.edu.barcodescanner_v2

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class StudentAdapter(private var studentList: List<Student>, private val firestore: FirebaseFirestore) : RecyclerView.Adapter<StudentAdapter.StudentHolder>() {
    private var onItemClickListener: OnStudentItemClickListener? = null
    private val TAG = "StudentAdapter"
    inner class StudentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val NameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val StudentIDView: TextView = itemView.findViewById(R.id.idTextView)
        val MajorTextView: TextView = itemView.findViewById(R.id.majorTextView)
        val CourseTextView: TextView = itemView.findViewById(R.id.courseTextView)
        val EmailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)


        init {
            deleteButton.setOnClickListener {
                Log.d("DeleteBtn", "Click")
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_layout, parent, false)
        return StudentHolder(view)
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: StudentHolder, position: Int) {
        holder.NameTextView.text = studentList[position].name
        holder.StudentIDView.text = studentList[position].id
        holder.MajorTextView.text = studentList[position].major
        holder.CourseTextView.text = studentList[position].course
        holder.EmailTextView.text = studentList[position].email



        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(studentList[position])
        }
    }
    fun setOnItemClickListener(listener: OnStudentItemClickListener) {
        onItemClickListener = listener
    }
    private fun deleteDocument(studentId: String) {
        firestore.collection("student")
            .whereEqualTo("id", studentId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d(TAG, "Document đã được xóa thành công! $studentId")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Lỗi khi xóa document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Lỗi khi truy vấn document", e)
            }
    }

    fun onDeleteClick(position: Int) {
        Log.d("OnDeleteClick func", "Document đã được xóa thành công!")

        val studentId = studentList[position].id
        deleteDocument(studentId)

        // Xóa sinh viên khỏi danh sách và cập nhật giao diện
        studentList = studentList.filterIndexed { index, _ -> index != position }
        notifyDataSetChanged()
    }

}
