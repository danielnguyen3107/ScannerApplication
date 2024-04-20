package ctu.edu.barcodescanner_v2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.io.IOException
import ctu.edu.barcodescanner_v2.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.math.log
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    private lateinit var cameraView: SurfaceView
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var viewFinderOverlay: ViewFinderOverlay

    private lateinit var db: FirebaseFirestore
    private var isBarcodeScanned = false

    //
    private var eventName: String? = null

    private var timer: Timer? = null

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        eventName = intent.getStringExtra("eventName")
        Log.d("MainActivity", "Received eventName: $eventName")
        // FIREBASE SET UP
        FirebaseApp.initializeApp(this)

        // SET UP DATABASE FIRESTORE
        // Define db
        db = Firebase.firestore

        if (db != null) {
            Log.d("Firestore", "Firestore initialized successfully")
        } else {
            Log.e("Firestore", "Failed to initialize Firestore")
        }


        cameraView = findViewById(R.id.camera_preview)
        viewFinderOverlay = findViewById(R.id.view_finder_overlay)

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            setupCamera()
        }


        binding.viewFinderOverlay.post {
            binding.viewFinderOverlay.setViewFinder()
        }

        binding.buttonNext.setOnClickListener {
            // Khi người dùng click vào Button, cho phép quét mã vạch lại
            isBarcodeScanned = false
            // Đặt lại Processor của barcodeDetector để tiếp tục quét
            Log.d("Reset", "Check 1")
        }
    }

    override fun onResume() {
        super.onResume()
        binding.buttonNext.setOnClickListener {
            // Khi người dùng click vào Button, cho phép quét mã vạch lại
            isBarcodeScanned = false
            Log.d("Check scan condition", isBarcodeScanned.toString())
            // Đặt lại Processor của barcodeDetector để tiếp tục quét
            barcodeDetector.setProcessor(createBarcodeProcessor())
            Log.d("Reset", "Check 1")
        }

    }
    override fun onPause() {
        super.onPause()
    }


    // Camera setting
    private fun setupCamera() {
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setAutoFocusEnabled(true)
            .build()

        cameraView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    cameraSource.start(cameraView.holder)
                } catch (e: IOException) {
                    Log.e("CameraSource", "Error starting camera source: ${e.message}")
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })

    }

    private fun createBarcodeProcessor(): Detector.Processor<Barcode>{
        return object: Detector.Processor<Barcode>{
            override fun release(){
            }
            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                if (::db.isInitialized) { // Kiểm tra xem db đã được khởi tạo hay chưa
                    if(!isBarcodeScanned ){
                        val barcodes = detections.detectedItems
                        if (barcodes.size() > 0 ) {
                            val barcode = barcodes.valueAt(0)
                            val rawValue = barcode.rawValue
                            val studentId = barcode.rawValue // Define studentID for email sending
                            Log.d("Barcode", "Value: ${barcode.rawValue}")

                            isBarcodeScanned = true // Đặt biến cờ thành true sau khi quét thành công

                            // Init data sending to Firestore
                            val data = hashMapOf(
                                "eventName" to eventName,
                                "value" to rawValue,
                                "timestamp" to FieldValue.serverTimestamp()
                            )
                            // Add data to collection named "barcodes"
                            db.collection("barcodes")
                                .add(data)
                                .addOnSuccessListener { documentReference ->
                                    Log.d("Firestore","DocumentSnapshot added with ID: ${documentReference.id}"
                                    )
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Barcode scanned successfully!",
                                        Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firestore", "Error adding document", e)
                                    }

                            // Setting EMAIL SENDING
                            // Gọi hàm để lấy địa chỉ email từ Firestore
                            getEmailFromFirestore(studentId, eventName) { email ->
                                Log.d(
                                    "Firestore",
                                    "Email for student ID $studentId: $email"
                                ) // Log ra giá trị email
                                if (email != null) {
                                    // Nếu có địa chỉ email, gửi email thông báo
                                    val subject = "Barcode Scanned Successfully"
                                    val body = "Barcode value: $studentId"
                                } else {
                                    // Nếu không tìm thấy địa chỉ email trong Firestore, thông báo lỗi
                                    Log.e("Firestore", "Email not found for student ID: $studentId")
                                    }
                                }
                            }
                        } else {
                            Log.e("Firebase", "Firestore is not initialized")
                        }
                    } // Check condition scan
            }
        }
    }


    // Check camera permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to use the scanner", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Email sending
        // 1-  Hàm để lấy địa chỉ email từ Firestore dựa trên ID quét từ mã barcode
    fun getEmailFromFirestore(studentId: String, eventName: String?, callback: (String?) -> Unit) {
        val db = Firebase.firestore
        db.collection("student")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id // Lấy ra ID của tài liệu
                    Log.d("FirestoreEmail", "Document ID: $id")
                    val studentDocumentId = document.getString("id")
                    Log.d("FirestoreEmail", "Student Document ID: $studentDocumentId")
                    if (studentId == studentDocumentId) {
                        // Lấy ra giá trị của trường id trong tài liệu
                        val studentDocumentId = document.getString("id")
                        Log.d("FirestoreStudentID", "Student Document ID is DETECTED: $studentDocumentId")
                        val studentEmail = document.getString("email")
                        Log.d("FirestoreStudentEmail", "Student Document Email is DETECTED: $studentEmail")

                        // Gọi hàm sendEmail nếu email được tìm thấy
                        if (studentEmail != null) {
                            val subject = "Check in Event"
                            val body = "You have check in $eventName event successfully"
                            sendEmail(studentEmail, subject, body)
                        } else {
                            Log.e("FirestoreStudentEmail", "Email is null for student ID: $studentId")
                        }
                        // Kết thúc vòng lặp khi đã tìm thấy email
                        return@addOnSuccessListener
                    }
                }
                // Trường hợp không tìm thấy tài liệu với studentId tương ứng
                Log.d("FirestoreEmail", "Document not found for student ID: $studentId")
                callback(null)
            }
            .addOnFailureListener { e ->
                // Xử lý khi truy vấn không thành công
                Log.e("Firestore", "Error getting documents", e)
                callback(null)
            }
    }

        // 2-  Email sending
    private fun sendEmail(recipientEmail: String, subject: String, body: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val senderEmail = "tuanb2017091@student.ctu.edu.vn"
                val senderPassword = "2fm#2FTA"

                val host = "smtp.gmail.com"
                val port = "587"

                val props = Properties()
                props["mail.smtp.auth"] = "true"
                props["mail.smtp.starttls.enable"] = "true"
                props["mail.smtp.host"] = host
                props["mail.smtp.port"] = port

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(senderEmail, senderPassword)
                    }
                })

                val message = MimeMessage(session)
                message.setFrom(InternetAddress(senderEmail))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
                message.subject = subject


                message.setText(body)

                withContext(Dispatchers.IO) {
                    Transport.send(message)
                }
                Log.d("SendEmail", "Email sent successfully!")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SendEmail", "Error sending email: ${e.message}")
            }
        }
    }




}
