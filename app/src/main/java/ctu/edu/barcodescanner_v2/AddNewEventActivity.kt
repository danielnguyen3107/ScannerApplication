package ctu.edu.barcodescanner_v2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ctu.edu.barcodescanner_v2.databinding.ActivityAddNewEventBinding
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.*
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

import ctu.edu.barcodescanner_v2.Event
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

class AddNewEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewEventBinding
    private  lateinit var dayPick: Date
    private lateinit var beginDate: Date
    private lateinit var endDate: Date
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Set up click listener for the submit button
        binding.submitEventButton.setOnClickListener {


            // Call a function to save event data to Firestore
            saveEventDataToFirestore()

            val intent = Intent(this, Activity_entry::class.java)
            startActivity(intent)
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

    private fun saveEventDataToFirestore() {
        // Retrieve event data from EditText fields and create an Event object
        val eventName = binding.eventNameEditText.text.toString()
        val host = binding.hostEditText.text.toString()
        val location = binding.locationEditText.text.toString()
        val numberOfMembers = binding.numberOfMembersEditText.text.toString().toLong()

        // Log the event details
        Log.d("EventDetails", "Event Name: $eventName")
        Log.d("EventDetails", "Host: $host")
        Log.d("EventDetails", "Location: $location")
        Log.d("EventDetails", "Number of Members: $numberOfMembers")
        Log.d("EventDetails", "Begin Date: $beginDate")
        Log.d("EventDetails", "End Date: $endDate")


        // Create an Event object with the retrieved data
        val event = Event(eventName, host, location, numberOfMembers, dayPick ,beginDate, endDate)

        // Save the event data to Firestore
        // You need to implement this part based on your Firestore setup
        // Add the event object to Firestore in the "events" collection
        val db = FirebaseFirestore.getInstance()
        db.collection("events")
            .add(event)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Event added with ID: ${documentReference.id}")
                // Clear EditText fields or show a success message
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding event", e)
                // Show an error message
            }
    }




}