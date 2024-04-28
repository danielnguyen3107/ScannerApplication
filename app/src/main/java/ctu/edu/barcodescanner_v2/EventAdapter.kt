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

class EventAdapter(private var eventList: List<Event>, private val firestore: FirebaseFirestore) : RecyclerView.Adapter<EventAdapter.EventHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
    private val tag = "EventAdapter"
    inner class EventHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        val hostTextView: TextView = itemView.findViewById(R.id.hostTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val membersTextView: TextView = itemView.findViewById(R.id.membersTextView)
        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        val beginTimeTextView: TextView = itemView.findViewById(R.id.beginTimeTextView)
        val endTimeTextView: TextView = itemView.findViewById(R.id.endTimeTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        init {
            deleteButton.setOnClickListener {
                Log.d("DeleteBtn in Event list", "Click")
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_layout, parent, false)
        return EventHolder(view)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        holder.eventNameTextView.text = eventList[position].eventName
        holder.hostTextView.text = eventList[position].host
        holder.locationTextView.text = eventList[position].location
        holder.membersTextView.text = eventList[position].numberOfMembers.toString()

        // Format dayPick, beginTime, and endTime into strings
        val dateFormatDay = SimpleDateFormat("yyyy-MM-dd") // Định dạng ngày tháng năm
        val dateFormatTime = SimpleDateFormat("HH:mm") // Định dạng giờ phút

        val dayPickString = dateFormatDay.format(eventList[position].dayPick)
        val beginTimeString = dateFormatTime.format(eventList[position].beginTime)
        val endTimeString = dateFormatTime.format(eventList[position].endTime)

        holder.dayTextView.text = dayPickString
        holder.beginTimeTextView.text = beginTimeString
        holder.endTimeTextView.text = endTimeString

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(eventList[position])
        }
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    private fun deleteDocument(eventName: String) {
        Log.d(tag, "Check function deleteDocument $eventName")
        firestore.collection("events")
            .whereEqualTo("eventName", eventName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d(tag, "Document đã được xóa thành công! $eventName")
                        }
                        .addOnFailureListener { e ->
                            Log.w(tag, "Lỗi khi xóa document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Lỗi khi truy vấn document", e)
            }
    }

    fun onDeleteClick(position: Int) {
        Log.d("OnDeleteClick func", "Document đã được xóa thành công!")

        val eventName = eventList[position].eventName
        deleteDocument(eventName)

        // Xóa sinh viên khỏi danh sách và cập nhật giao diện
        eventList = eventList.filterIndexed { index, _ -> index != position }
        notifyDataSetChanged()
    }
}
