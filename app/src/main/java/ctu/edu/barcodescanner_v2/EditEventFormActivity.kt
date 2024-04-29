package ctu.edu.barcodescanner_v2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import ctu.edu.barcodescanner_v2.databinding.ActivityAddNewEventBinding
import ctu.edu.barcodescanner_v2.databinding.ActivityEditEventFormBinding
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditEventFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditEventFormBinding
    private lateinit var db: FirebaseFirestore
    private  lateinit var dayPick: Date
    private lateinit var beginDate: Date
    private lateinit var endDate: Date
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEventFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()


        // Lấy tham chiếu đến các EditText trong layout XML
        val eventNameEditText = findViewById<EditText>(R.id.eventNameEditText)
        val hostEditText = findViewById<EditText>(R.id.hostEditText)
        val locationEditText = findViewById<EditText>(R.id.locationEditText)
        val numberOfMembersEditText = findViewById<EditText>(R.id.numberOfMembersEditText)
        val dateResultTextView = findViewById<TextView>(R.id.dateResult)
        val beginTimeResultTextView = findViewById<TextView>(R.id.beginTimeResult)
        val endTimeResultTextView = findViewById<TextView>(R.id.endTimeResult)
        val submitEventButton = findViewById<Button>(R.id.submitEventButton)



        // Lấy các giá trị từ Intent
        val eventName = intent.getStringExtra("eventName")
        val host = intent.getStringExtra("host")
        val location = intent.getStringExtra("location")
        val numberOfMembers = intent.getLongExtra("numberOfMembers", 0)
        val dayPickTimestamp = intent.getLongExtra("dayPick", 0)
        val beginTimeTimestamp = intent.getLongExtra("beginTime", 0)
        val endTimeTimestamp = intent.getLongExtra("endTime", 0)

        // Tạo đối tượng Date từ timestamp
        dayPick = Date(dayPickTimestamp)
        beginDate = Date(beginTimeTimestamp)
        endDate = Date(endTimeTimestamp)


        // Gán các giá trị vào các EditText
        eventNameEditText.setText(eventName)
        hostEditText.setText(host)
        locationEditText.setText(location)
        numberOfMembersEditText.setText(numberOfMembers.toString())

        // Format date và time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        dateResultTextView.text = dateFormat.format(dayPick)
        beginTimeResultTextView.text = timeFormat.format(beginDate)
        endTimeResultTextView.text = timeFormat.format(endDate)


        // Set click listeners for the buttons using view binding
        binding.pickDate.setOnClickListener {
            showDatePickerDialog()
        }
        binding.pickBeginTime.setOnClickListener {
            showBeginTimePickerDialog()
        }
        binding.pickEndTime.setOnClickListener {
            showEndTimePickerDialog()
        }

        submitEventButton.setOnClickListener {
            // Lấy giá trị từ các EditText và TextView
            val eventName = eventNameEditText.text.toString()
            val host = hostEditText.text.toString()
            val location = locationEditText.text.toString()
            val numberOfMembers = numberOfMembersEditText.text.toString().toLong()

            // Chuyển đổi ngày tháng sang định dạng chuỗi
            val dayPick = dayPick
            val beginTime = beginDate
            val endTime = endDate

            // Lưu giá trị vào Firestore
            saveEventDataToFirestore(eventName, host, location, numberOfMembers, dayPick, beginTime, endTime)

            val intent = Intent(this, EventAttendanceListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveEventDataToFirestore(eventName: String, host: String, location: String, numberOfMembers: Long, dayPick: Date, beginTime: Date, endTime: Date) {
        db.collection("events")
            .whereEqualTo("eventName", eventName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    document.reference.update(
                        "host", host,
                        "location", location,
                        "numberOfMembers", numberOfMembers,
                        "dayPick", dayPick,
                        "beginTime", beginTime,
                        "endTime", endTime
                    )
                        .addOnSuccessListener {
                            Log.d("saveEventFirestore", "Document event đã được cập nhật thành công! $eventName")
                        }
                        .addOnFailureListener { e ->
                            Log.w("saveEventFirestore", "Lỗi khi cập nhật document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("error save", "Lỗi khi truy vấn document", e)
            }
    }
    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // Save the selected date to the 'date' variable
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                dayPick = selectedDate.time

                // Update beginDate and endDate to the selected date with default times
                beginDate = selectedDate.time
                endDate = selectedDate.time

                // Display the selected date in the corresponding TextView
                binding.dateResult.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                Log.d("SelectedDate", "Selected date: ${binding.dateResult.text}")

                // Optionally, you can update the beginTimeResult and endTimeResult TextViews
                val defaultTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.beginTimeResult.text = defaultTimeFormat.format(beginDate)
                binding.endTimeResult.text = defaultTimeFormat.format(endDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }


    private fun showBeginTimePickerDialog() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                binding.beginTimeResult.text = "$hourOfDay:$minute"
                // Set the beginDate variable to the selected time, along with the selected day
                val calendar = Calendar.getInstance().apply {
                    time = dayPick // Set the calendar to the selected day
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                beginDate = calendar.time
                Log.d("SelectedDate", "Selected begin time: $beginDate")
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun showEndTimePickerDialog() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                binding.endTimeResult.text = "$hourOfDay:$minute"
                // Set the endDate variable to the selected time, along with the selected day
                val calendar = Calendar.getInstance().apply {
                    time = dayPick // Set the calendar to the selected day
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                endDate = calendar.time
                Log.d("SelectedDate", "Selected end time: $endDate")
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }
}
