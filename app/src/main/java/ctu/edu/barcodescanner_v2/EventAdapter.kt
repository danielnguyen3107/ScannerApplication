package ctu.edu.barcodescanner_v2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class EventAdapter(private var eventList: List<Event>) : RecyclerView.Adapter<EventAdapter.EventHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
    inner class EventHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        val hostTextView: TextView = itemView.findViewById(R.id.hostTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val membersTextView: TextView = itemView.findViewById(R.id.membersTextView)
        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        val beginTimeTextView: TextView = itemView.findViewById(R.id.beginTimeTextView)
        val endTimeTextView: TextView = itemView.findViewById(R.id.endTimeTextView)
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
}
