package com.jaro.wblconnection

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
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
import com.parse.ParseACL
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Registration_with_QR_code : AppCompatActivity() {

    private lateinit var previewView: androidx.camera.view.PreviewView
    private lateinit var qrCodeInformation: TextView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var qrCodeFinal: String
    private lateinit var signUpButton: Button
    private lateinit var backButton: Button
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var discardQrButton: Button
    private var qrCodeDetected = false



    @SuppressLint("SetTextI18n")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "A kameraengedély szükséges a QR-kód beolvasásához",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration_with_qr_code)

        previewView = findViewById(R.id.previewView)
        qrCodeInformation = findViewById(R.id.TextViewQrCodeInformation)
        qrCodeInformation.text = "Kód nem látható"
        signUpButton = findViewById(R.id.buttonSignUp)

        cameraExecutor = Executors.newSingleThreadExecutor()

        discardQrButton = findViewById(R.id.buttonDiscardQr)

        discardQrButton.setOnClickListener {
            qrCodeFinal = "" // töröljük a QR-kód értékét

            qrCodeInformation.text = "Kód eldobva, olvass be újat"
            qrCodeInformation.setTextColor(ContextCompat.getColor(this, R.color.yellow))
            Toast.makeText(this, "Kód eldobva, olvass be újatt", Toast.LENGTH_LONG).show()
        }


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        backButton = findViewById<Button>(R.id.buttonBack)
        backButton.setOnClickListener {
            finish()
        }

        signUpButton.setOnClickListener {
            ParseUser.logOut()
            checkQrAndSaveUser()
        }
    }

    private fun checkQrAndSaveUser() {
        if (!::qrCodeFinal.isInitialized || qrCodeFinal.isEmpty()) {
            Toast.makeText(this, "kérlek előbb olvass be Qr kódot", Toast.LENGTH_LONG).show()
            return
        }

        val parts = qrCodeFinal.split(":")
        if (parts.size < 2) {
            qrCodeInformation.text = "⚠️ Hibás QR-kód formátum"
            return
        }

        val objectId = parts[1]
        val query = ParseQuery.getQuery<ParseObject>("QrCode")

        query.getInBackground(objectId) { obj, e ->
            if (e == null && obj != null) {
                if (obj.get("Used") == false) {
                    qrCodeInformation.text = "Ez a QR-kód szabad"
                    saveUser(obj)
                } else {
                    qrCodeInformation.text = "Ez a QR-kód már felhasználva"
                }
            } else {
                qrCodeInformation.text = "Ismeretlen hiba"
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun saveUser(obj: ParseObject) {

        val username = findViewById<EditText>(R.id.editTextLicense).text.toString().trim().toUpperCase()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
        val confirmPassword = findViewById<EditText>(R.id.editTextPasswordAgain).text.toString()
        val emailAddress = findViewById<EditText>(R.id.editEmailAddress).text.toString()


        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || emailAddress.isEmpty()) {
            Toast.makeText(this, "Minden mező kitöltése kötelező", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "A jelszavak nem egyeznek", Toast.LENGTH_LONG).show()
            return
        }

        val user = ParseUser()
        user.username = username
        user.setPassword(password)
        user.email = emailAddress
        val acl = ParseACL()
        acl.publicReadAccess = true
        acl.publicWriteAccess = true

        // Apply ACL to user object
        user.acl = acl

        user.signUpInBackground { e ->
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
                    val barcode = barcodes.firstOrNull()

                    if (barcode != null) {
                        val qrText = barcode.rawValue ?: return@addOnSuccessListener

                        if (!::qrCodeFinal.isInitialized || qrText != qrCodeFinal) {
                            qrCodeFinal = qrText
                            qrCodeDetected = true // beállítjuk, hogy volt kód
                            validateQrCode(qrText)
                        }
                    } else {
                        if (!qrCodeDetected) {
                            qrCodeInformation.text = "Kód nem látható"
                            qrCodeInformation.setTextColor(ContextCompat.getColor(this, R.color.yellow))
                        }
                        // különben NE írja felül az infót, ha már van beolvasott kód
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


    private fun validateQrCode(code: String) {
        val query = ParseQuery.getQuery<ParseObject>("QrCode")
        val objectId = code.split(":").getOrNull(1)

        if (objectId == null) {
            qrCodeInformation.text = "Ismeretlen QR-kód"
            return
        }

        query.getInBackground(objectId) { obj, e ->
            when {
                e != null || obj == null -> {
                    qrCodeInformation.text = "Ismeretlen QR-kód"
                    qrCodeInformation.setTextColor(ContextCompat.getColor(this, R.color.yellow))
                }
                obj.getBoolean("Used") -> {
                    qrCodeInformation.text = "QR-kód már felhasználva"
                    qrCodeInformation.setTextColor(ContextCompat.getColor(this, R.color.red))
                }
                else -> {
                    qrCodeInformation.text = "QR-kód szabad"
                    qrCodeInformation.setTextColor(ContextCompat.getColor(this, R.color.green))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()
    }
}

