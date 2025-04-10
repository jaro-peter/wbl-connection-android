package com.jaro.wblconnection

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.ui.text.toUpperCase
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Registration_with_QR_code : AppCompatActivity() {

    private lateinit var previewView: androidx.camera.view.PreviewView
    private lateinit var txtResult: TextView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var qrCodeFinal : String
    private lateinit var signUpButton : Button
    private lateinit var backButton: Button
    private var cameraProvider: ProcessCameraProvider? = null



    @SuppressLint("SetTextI18n")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                txtResult.text = getString(R.string.camera_permission_is_required)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration_with_qr_code)

        previewView = findViewById(R.id.previewView)
        txtResult = findViewById(R.id.txtResult)
        signUpButton = findViewById(R.id.buttonSignUp)



            cameraExecutor = Executors.newSingleThreadExecutor()

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startCamera()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }

        /*signUpButton.setOnClickListener {
            if (validateInput()) {
                val intent = Intent(this, RegistrationSuccessful::class.java)
                startActivity(intent)
            }
        }*/
        backButton = findViewById<Button>(R.id.buttonBack)
        backButton.setOnClickListener {
            finish() // ← Ez bezárja az aktuális Activity-t, és visszalép az előzőre
        }

        signUpButton.setOnClickListener {

            checkQrAndSaveUser()
        }

    }
    private fun checkQrAndSaveUser(){
        val query = ParseQuery.getQuery<ParseObject>("QrCode")

// Az objektum lekérdezése objectId alapján
        query.getInBackground(qrCodeFinal.split(':')[1]) { obj, e ->
            if (e == null && obj != null) {
                if (obj.get("Used")==false){
                    saveUser(obj)

                }
                    else {
                    Toast.makeText(this, "Ez a QR code használt", Toast.LENGTH_SHORT).show()}
            } else {
                // Hiba történt
                Toast.makeText(this, e?.message ?: "Ismeretlen hiba", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun saveUser(obj: ParseObject){

        val username = findViewById<EditText>(R.id.editTextLicense).text.toString().trim().toUpperCase()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
        val confirmPassword = findViewById<EditText>(R.id.editTextPasswordAgain).text.toString()

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Minden mező kitöltése kötelező", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "A jelszavak nem egyeznek", Toast.LENGTH_SHORT).show()
            return
        }

        val user = ParseUser()
        user.username = username
        user.setPassword(password)

        user.signUpInBackground {e ->
            if (e == null) {
                obj.put("Used", true)
                obj.put("UserId", user.objectId)
                obj.save()
                Toast.makeText(this, "✅ Sikeres regisztráció", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, RegistrationSuccessful::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "❌ Hiba: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun startCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                // Osztályszintű változóba mentjük
                cameraProvider = cameraProviderFuture.get()

                // Lokális változó, hogy elkerüljük a smart cast hibát
                val provider = cameraProvider ?: return@addListener

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { image ->
                            scanQRCode(image)
                        }
                    }

                provider.unbindAll()

                provider.bindToLifecycle(
                    this as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

            } catch (e: Exception) {
                Log.e("CameraX", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }


    @androidx.annotation.OptIn(ExperimentalGetImage::class)
        private fun scanQRCode(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val inputImage =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val scanner = BarcodeScanning.getClient()

                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        barcodes.firstOrNull()?.rawValue?.let { qrText ->

                            qrCodeFinal = qrText
                            txtResult.text = getString(R.string.qr_code_scanning_successful)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("QRScanner", "QR Code scanning failed", e)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }


        private fun validateInput(): Boolean {

            val licensePlate = findViewById<EditText>(R.id.editTextLicense).text.toString().trim()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.editTextPasswordAgain).text.toString()

            if (licensePlate.isEmpty()) {
                Toast.makeText(this, "A rendszám mező nem lehet üres", Toast.LENGTH_SHORT).show()
                return false
            }

            if (!licensePlate.contains("-")) {
                Toast.makeText(
                    this,
                    "A rendszámnak tartalmaznia kell egy kötőjelet (pl. ABC-123)",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }

            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "A jelszónak legalább 6 karakter hosszúnak kell lennie",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "A jelszavak nem egyeznek", Toast.LENGTH_SHORT).show()
                return false
            }

            return true
        }


        override fun onDestroy() {
            super.onDestroy()

            cameraProvider?.unbindAll()
            cameraExecutor.shutdown()
        }
    }

