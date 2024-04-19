package ctu.edu.barcodescanner_v2

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class Activity_entry : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        val currentEventButton = findViewById<Button>(R.id.currentEventButton)
        val manageEventButton = findViewById<Button>(R.id.manageEventButton)
        val studentManageButton = findViewById<Button>(R.id.studentManagementButton)
        currentEventButton.setOnClickListener {
            startActivity(Intent(this, EventListActivity::class.java))
        }

        manageEventButton.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_manage_event)

            // Set background cho dialog
            dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            val addEventButton = dialog.findViewById<Button>(R.id.addEventButton)
            val eventAttendanceListButton = dialog.findViewById<Button>(R.id.eventAttendanceListButton)

            // Xử lý sự kiện khi nhấn các nút trong dialog
            addEventButton.setOnClickListener { // Khi nhấn vào button này sẽ mở form thêm sự kiện
                startActivity(Intent(this, AddNewEventActivity::class.java))
                dialog.dismiss() // Đóng dialog sau khi xử lý xong
            }

            eventAttendanceListButton.setOnClickListener {// Khi nhấn vào button này sẽ mở danh sách
                startActivity(Intent(this,  EventAttendanceListActivity::class.java))
                dialog.dismiss() // Đóng dialog sau khi xử lý xong
            }

            dialog.show()
        }


        studentManageButton.setOnClickListener{
            startActivity(Intent(this, StudentManagementActivity::class.java))
        }



    }






}