package ctu.edu.barcodescanner_v2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
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
import ctu.edu.barcodescanner_v2.ViewFinderOverlay
import ctu.edu.barcodescanner_v2.databinding.ActivityMainBinding

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
    }

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

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                if (::db.isInitialized) { // Kiểm tra xem db đã được khởi tạo hay chưa
                    val barcodes = detections.detectedItems
                    if (barcodes.size() > 0) {
                        val barcode = barcodes.valueAt(0)
                        val rawValue = barcode.rawValue
                        Log.d("Barcode", "Value: ${barcode.rawValue}")

                        isBarcodeScanned = true // Đặt biến cờ thành true sau khi quét thành công

                        val data = hashMapOf(
                            "eventName" to eventName,
                            "value" to rawValue,
                            "timestamp" to FieldValue.serverTimestamp()
                        )
                        // Thêm dữ liệu vào một collection có tên là "barcodes"
                        db.collection("barcodes")
                            .add(data)
                            .addOnSuccessListener { documentReference ->
                                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
                                Toast.makeText(this@MainActivity, "Barcode scanned successfully!", Toast.LENGTH_SHORT).show()

                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error adding document", e)
                            }

                        // Gỡ bỏ bộ xử lý của BarcodeDetector để ngăn quét tiếp theo
                        barcodeDetector.setProcessor(null)
                    }
                } else {
                    Log.e("Firebase", "Firestore is not initialized")
                }
            }
        })

    }

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
}
