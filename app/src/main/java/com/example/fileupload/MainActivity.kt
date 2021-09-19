package com.example.fileupload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.fileupload.Constants.TOKEN
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), UploadRequestBody.UploadCallback {

    private var sharedPreferenceManager: SharedprefManager = SharedprefManager.getInstance(this);
    private var selectedImage: Uri? = null
    private var data: String = ""

    companion object {
        private val LOCATION_PERMISSION_REQUEST_CODE = 212
        private const val REQUEST_CODE_IMAGE_PICKER = 100
        private const val REQUEST_CODE_CAMERA = 44
        private const val REQUEST_CODE_STORAGE = 122
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // for tool bar
        val toolbar = findViewById(R.id.toolbars) as Toolbar?
        setSupportActionBar(toolbar)

        var userName = sharedPreferenceManager.userName
        if (userName !=null){
            welcome.setText("hey, $userName")
        }

        image_view.setOnClickListener {
            openImageChooser()
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            openCameraWithPermission()
        }

        button_upload.setOnClickListener {
            uploadImage()
        }

        logout_bt.setOnClickListener {
            logout()
        }
    }

    // for action bar and menu_title for three dots!!!!
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout-> {
                logout()
            }
        }
        return true
    }


    private fun logout() {
        sharedPreferenceManager.clear();
        // then go to the Mainactivity
        val intent = Intent(this, LoginPage::class.java)
        // for clearing tasks
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        data = location.latitude.toString() + "," + location.longitude.toString()
                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },
            Looper.myLooper()
        )
    }

    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtils.isLocationEnabled(this) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    this,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!sharedPreferenceManager.isLoggedIn()) {
            logout()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun uploadImage() {
        if (selectedImage == null) {
            layout_root.snackbar("Select an image first")
            return
        }

        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImage!!, "r", null) ?: return
        val file = File(cacheDir, contentResolver.getFileName(selectedImage!!))
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outPutStream = FileOutputStream(file)
        inputStream.copyTo(outPutStream)

        progress_bar.progress = 0
        val body = UploadRequestBody(file, "image", this)

        var headerMap: Map<String, String> =
            mapOf(TOKEN to sharedPreferenceManager.getUserToken())

        MyApi().uploadImage(
            MultipartBody.Part.createFormData("file", file.name, body),
            MultipartBody.Part.createFormData("data", data),
            headerMap
        ).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.code() == 401) {
                    logout()
                }

                progress_bar.progress = 100

                if (response.code() == 200) {
                    layout_root.snackbar(response.body()?.success.toString())
                    image_view.setImageResource(R.drawable.ic_upload)
                    Thread.sleep(300)
                    progress_bar.progress = 0
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                layout_root.snackbar(t.message!!)
            }

        })

    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            val bitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath)
            image_view.setImageBitmap(bitmap)
        } else
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    REQUEST_CODE_IMAGE_PICKER -> {
                        selectedImage = data?.data
                        image_view.setImageURI(selectedImage)
                    }
                }
            }
    }

    override fun onProgressUpdate(percentage: Int) {
        progress_bar.progress = percentage
    }

    private fun openCameraWithPermission() {
        if (PermissionUtils.isAccessCameraAndStorage(this)) {
            openCamera()
        } else if (
            getCameraPermission()) {
            openCamera()
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    selectedImage = FileProvider.getUriForFile(
                        this,
                        "com.example.fileupload.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage)
                    startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
                }
            }
        }
    }

    private fun getCameraPermission(): Boolean {
        if (!PermissionUtils.isAccessCameraAndStorage(this)) {
            PermissionUtils.requestAccessCameraAndStorage(this, REQUEST_CODE_CAMERA)
        }
        return PermissionUtils.isAccessCameraAndStorage(this);
    }


    private lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }

    }
}