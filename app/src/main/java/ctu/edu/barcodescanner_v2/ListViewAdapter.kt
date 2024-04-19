package ctu.edu.barcodescanner_v2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ListViewAdapter(private val context: AttendanceListActivity,
                      private var attendanceDetailList: List<AttendanceDetail>) :
    ArrayAdapter<AttendanceDetail>(context, 0, attendanceDetailList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            val inflater = LayoutInflater.from(context)
            itemView = inflater.inflate(R.layout.list_item_student, parent, false)
        }

        // Thiết lập dữ liệu cho mỗi item (ví dụ: id, timeCheck)
        val attendanceDetail = getItem(position)
        val idTextView = itemView?.findViewById<TextView>(R.id.text_id)
        val timeCheckTextView = itemView?.findViewById<TextView>(R.id.text_timeCheck)

        idTextView?.text = attendanceDetail?.IDStudent // Lấy phần ID

        // Lấy thời gian từ attendanceDetail và chuyển đổi thành chuỗi
        val timeCheckString = attendanceDetail?.timeCheck?.toString()
        timeCheckTextView?.text = timeCheckString // Lấy phần Time Check


        return itemView!!
    }
}