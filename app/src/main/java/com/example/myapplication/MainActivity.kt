package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.CursorLoader
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.ui.theme.MyApplicationTheme
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class MainActivity : ComponentActivity() {

    val apiInterface: APIInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0);
        }
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
//                    Button(onClick = { openPhotoPicker() }) {
//                        Text(text = "open")
//                    }
                ComposeInCooperatingViewNestedScrollInteropSample()
                }
            }
        }
    }


    fun openPhotoPicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 || resultCode == 0) {

            val map = HashMap<String, String>()

            map["Authorization"] = "Bearer public_kW15bMw8zubXpxpDxzMYRUTKaJyV"

            val client = APIClient.client.create(APIInterface::class.java)
            val file = getPathFromURI(data?.data)?.let { File(it) }
//            val file = File("/storage/emulated/0/DCIM/Camera/20230527_030639.jpg")
            val requestFile = RequestBody.create(MediaType.parse("image/jpg"), file)
// MultipartBody.Part is used to send also the actual file name

// MultipartBody.Part is used to send also the actual file name
            val body = MultipartBody.Part.createFormData("file", file!!.name, requestFile)
            val response = client.doGetListResources(body, map)
            response.enqueue(object : Callback<ExampleJson2KtKotlin> {
                override fun onResponse(
                    call: Call<ExampleJson2KtKotlin>?,
                    response: Response<ExampleJson2KtKotlin>?
                ) {
                }

                override fun onFailure(call: Call<ExampleJson2KtKotlin>?, t: Throwable?) {
                }
            })
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun getPathFromURI(uri: Uri?): String? {
        var realPath = ""
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            @SuppressLint("Recycle") val cursor: Cursor? =
                contentResolver.query(uri!!, proj, null, null, null)
            var column_index = 0
            val result = ""
            if (cursor != null) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                realPath = cursor.getString(column_index)
            }
        } else if (Build.VERSION.SDK_INT < 19) {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursorLoader = CursorLoader(this, uri, proj, null, null, null)
            val cursor: Cursor? = cursorLoader.loadInBackground()
            if (cursor != null) {
                val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                realPath = cursor.getString(column_index)
            }
        } else {
            val wholeID = DocumentsContract.getDocumentId(uri)
            // Split at colon, use second item in the array
            val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            val column = arrayOf(MediaStore.Images.Media.DATA)
            // where id is equal to
            val sel = MediaStore.Images.Media._ID + "=?"
            val cursor: Cursor? = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column,
                sel,
                arrayOf(id),
                null
            )
            var columnIndex = 0
            if (cursor != null) {
                columnIndex = cursor.getColumnIndex(column[0])
                if (cursor.moveToFirst()) {
                    realPath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        }
        return realPath
    }

}
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}