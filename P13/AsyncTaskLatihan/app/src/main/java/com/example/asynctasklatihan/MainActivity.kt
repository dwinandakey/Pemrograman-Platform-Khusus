package com.example.asynctasklatihan;
//AsyncTask
//import android.app.ProgressDialog
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.os.AsyncTask
//import android.os.Bundle
//import android.widget.Button
//import android.widget.ImageView
//import androidx.appcompat.app.AppCompatActivity
//import java.io.InputStream
//import java.net.HttpURLConnection
//import java.net.URL
//
//class MainActivity : AppCompatActivity() {
//    private var imageUrl: URL? = null
//    private var inputStream: InputStream? = null
//    private var bitmap: Bitmap? = null
//    private var imageView: ImageView? = null
//    private var button: Button? = null
//    private var progressDialog: ProgressDialog? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        button = findViewById(R.id.asyncTask)
//        imageView = findViewById(R.id.image)
//
//        button?.setOnClickListener {
//            AsyncTaskExample().execute("https://stis.ac.id/media/source/up.png")
//        }
//    }
//
//    private inner class AsyncTaskExample : AsyncTask<String, String, Bitmap?>() {
//        override fun onPreExecute() {
//            super.onPreExecute()
//            progressDialog = ProgressDialog(this@MainActivity).apply {
//                setMessage("Downloading...")
//                isIndeterminate = false
//                setCancelable(false)
//                show()
//            }
//        }
//
//        override fun doInBackground(vararg params: String): Bitmap? {
//            try {
//                imageUrl = URL(params[0])
//                val conn = imageUrl?.openConnection() as HttpURLConnection
//                conn.doInput = true
//                conn.connect()
//                inputStream = conn.inputStream
//
//                val options = BitmapFactory.Options().apply {
//                    inPreferredConfig = Bitmap.Config.RGB_565
//                }
//
//                bitmap = BitmapFactory.decodeStream(inputStream, null, options)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            return bitmap
//        }
//
//        override fun onPostExecute(result: Bitmap?) {
//            super.onPostExecute(result)
//            imageView?.let {
//                progressDialog?.hide()
//                it.setImageBitmap(result)
//            } ?: run {
//                progressDialog?.show()
//            }
//        }
//    }
//}
//concurrent
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.*

class MainActivity : AppCompatActivity() {
    private var imageView: ImageView? = null
    private var button: Button? = null
    private var progressDialog: ProgressDialog? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.asyncTask)
        imageView = findViewById(R.id.image)

        button?.setOnClickListener {
            downloadImage("https://stis.ac.id/media/source/up.png")
        }
    }

    private fun downloadImage(imageUrl: String) {
        // Show Progress Dialog
        progressDialog = ProgressDialog(this).apply {
            setMessage("Downloading...")
            isIndeterminate = false
            setCancelable(false)
            show()
        }

        // Execute network operation in background
        executor.execute {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val input: InputStream = connection.inputStream
                val options = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                val bitmap = BitmapFactory.decodeStream(input, null, options)

                // Update UI on main thread
                mainHandler.post {
                    imageView?.setImageBitmap(bitmap)
                    progressDialog?.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mainHandler.post {
                    progressDialog?.dismiss()
                    // Handle error here
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Shutdown executor when activity is destroyed
        executor.shutdown()
    }
}