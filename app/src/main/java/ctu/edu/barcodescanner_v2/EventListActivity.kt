package ctu.edu.barcodescanner_v2

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TabHost
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date


class EventListActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var adapter: EventAdapter
    private lateinit var eventList: MutableList<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        val recyclerView = findViewById<RecyclerView>(R.id.rvEventList)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Initialize eventList with an empty list
        eventList = mutableListOf<Event>()

        // Retrieve events first, then set the adapter with the retrieved data
        retrieveEventsFromFirestore()

    }


    override fun onItemClick(event: Event) {
        val currentTime = Calendar.getInstance().time
        if (currentTime.after(event.beginTime) && currentTime.before(event.endTime)) {
            // Thời gian hiện tại nằm trong khoảng thời gian của sự kiện
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("eventName", event.eventName)
            startActivity(intent)
        } else {
            // Thời gian hiện tại không nằm trong khoảng thời gian của sự kiện, hiển thị thông báo
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Thông báo")
                .setMessage("Sự kiện này đang không hoạt động.")
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }


    private fun retrieveEventsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val eventsRef = db.collection("events")
        eventsRef.get()
            .addOnSuccessListener { querySnapshot ->
                // Xóa dữ liệu cũ
                eventList.clear()
                val currentTime = Date()
                // Lặp qua tất cả các tài liệu và thêm vào danh sách sự kiện
                for (doc in querySnapshot.documents) {
                    val beginTimeTimestamp = doc.getTimestamp("beginTime")
                    val dayPickTimestamp = doc.getTimestamp("dayPick")
                    val endTimeTimestamp = doc.getTimestamp("endTime")
                    val eventName = doc.getString("eventName")
                    val eventHost = doc.getString("host")
                    val eventLocation = doc.getString("location")
                    val eventMembers = doc.getLong("numberOfMembers")

                    // Xử lý dữ liệu
                    val dayPick = dayPickTimestamp?.toDate()
                    val beginTime = beginTimeTimestamp?.toDate()
                    val endTime = endTimeTimestamp?.toDate()

                    if (eventName != null && eventHost != null && eventLocation != null &&
                        eventMembers != null && dayPick != null && beginTime != null && endTime != null &&
                        currentTime.before(endTime) ) {

                        val event = Event(eventName, eventHost, eventLocation, eventMembers, dayPick, beginTime, endTime)
                        eventList.add(event)

                        // In sự kiện ra màn hình console (để kiểm tra)
                        Log.d("EventDetails", "Event: $event")
                    }
                }

                // Cập nhật adapter sau khi đã lấy dữ liệu
                adapter = EventAdapter(eventList, db)
                adapter.setOnItemClickListener(this)
                val recyclerView = findViewById<RecyclerView>(R.id.rvEventList)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Xử lý khi có lỗi xảy ra
                Log.e("EventListActivity", "Error retrieving events", exception)
            }
    }
}