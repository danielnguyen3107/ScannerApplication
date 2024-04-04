package ctu.edu.barcodescanner_v2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class StudentAdapter(private var studentList: List<Student>) : RecyclerView.Adapter<StudentAdapter.StudentHolder>() {
    private var onItemClickListener: OnStudentItemClickListener? = null
    inner class StudentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val NameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val StudentIDView: TextView = itemView.findViewById(R.id.idTextView)
        val MajorTextView: TextView = itemView.findViewById(R.id.majorTextView)
        val CourseTextView: TextView = itemView.findViewById(R.id.courseTextView)
        val EmailTextView: TextView = itemView.findViewById(R.id.emailTextView)
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
}
