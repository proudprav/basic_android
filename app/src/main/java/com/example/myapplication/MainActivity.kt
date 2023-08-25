package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.MainActivity.Companion.BUFFER_SIZE
import com.example.myapplication.MainActivity.Companion.IMAGE_DIRECTORY
import com.example.myapplication.data.RequiredData
import com.example.myapplication.response.CreateCaptures
import com.example.myapplication.response.TriggerCapture
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import okhttp3.MediaType
import okhttp3.MediaType.Companion.get
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale.getDefault


class MainActivity : ComponentActivity() {

    val apiInterface: APIInterface? = null
    var client: APIInterface? = null
    companion object{
        val BUFFER_SIZE = 1024 * 2
        val IMAGE_DIRECTORY = "/demonuts_upload_gallery"
    }
    @SuppressLint("PermissionLaunchedDuringComposition")
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = APIClient.client.create(APIInterface::class.java)

        setContent {
            var text by remember { mutableStateOf(TextFieldValue("")) }
            var token by remember { mutableStateOf(TextFieldValue("")) }


            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {

                        Button(onClick = { getCredits() }) {
                            Text(text = "getcredits")
                        }
                        Button(onClick = { createCapture(text.text) }) {
                            Text(text = "create capture")
                        }
                        Button(onClick = { uploadCapture() }) {
                            Text(text = "upload capture")
                        }
                        Button(onClick = { triggerCapture() }) {
                            Text(text = "trigger capture")
                        }
                        TextField(value = text, onValueChange = {
                            text = it
                        })
                        Text(text = "Add your Token below")
                        TextField(value = token, onValueChange = {
                            token = it
                            RequiredData.setToken(token.text)
                        })
                    }
                }
            }
        }
    }

    private fun triggerCapture() {
        val triggerCapture = client?.triggerCapture(commonAuthHeader(), RequiredData.slug)
        triggerCapture?.enqueue(object : Callback<TriggerCapture>{
            override fun onResponse(
                call: Call<TriggerCapture>?,
                response: Response<TriggerCapture>?
            ) {
                Toast.makeText(this@MainActivity, "Capture Triggered", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<TriggerCapture>?, t: Throwable?) {

            }
        })
    }

    private fun uploadCapture() {
        openPhotoPicker()
    }

    fun openPhotoPicker() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 || resultCode == 0) {

//            val contentResolver: ContentResolver = this.contentResolver
//            val videoFile = File(data?.data?.path) // Get the file path from URI
            val videoMimeType = contentResolver.getType(data?.data!!)
//            val requestBody = RequestBody.create(videoMimeType?.toMediaTypeOrNull(), videoFile)


            val file = getFileFromVideoUri1(this@MainActivity, data?.data!!)
//            val file4 = File(file)
//            val file = getFileFromContentUri(this@MainActivity,(data?.data!!), false)
//            val file = createVideoOutputPath(this@MainActivity, data?.data!!)
//            val file = getFilePathFromURI1(this@MainActivity,(data?.data!!))
//            val file = File("/data/user/0/com.example.myapplication/cache/20230702_170821.mp4")
            val MEDIA_TYPE: MediaType = "text/plain".toMediaType()

            val requestFile = RequestBody.create(MEDIA_TYPE, file!!)
// MultipartBody.Part is used to send also the actual file name

// MultipartBody.Part is used to send also the actual file name
            val body = MultipartBody.Part.createFormData("file", file?.name, requestFile)

            extracted(this, body)
        }
    }

    private fun extracted(context: Context, body: MultipartBody.Part) {
//        CoroutineScope(Dispatchers.IO).launch {
        client?.uploadCapture(RequiredData.sourceLink, body)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                Toast.makeText(this@MainActivity, "Uploaded", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("Not yet implemented")
            }
        })
//        }
    }

    private fun getFileFromContentUri(context: Context, contentUri: Uri, uniqueName: Boolean): File {
        // Preparing Temp file name
        val fileExtension = getFileExtension(context, contentUri) ?: ""
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", getDefault()).format(Date())
        val fileName = "temp.mp4"
        // Creating Temp file
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()
        // Initialize streams
        var oStream: FileOutputStream? = null
        var inputStream: InputStream? = null

        try {
            oStream = FileOutputStream(tempFile)
            inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let { copy(inputStream, oStream) }
            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Close streams
            inputStream?.close()
            oStream?.close()
        }

        return tempFile
    }

    @Throws(IOException::class)
    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }

    private fun commonAuthHeader(): Map<String, String> {
        val hashMap = HashMap<String, String>()
        hashMap["Authorization"] = RequiredData.authKey
        return hashMap
    }

    private fun createCapture(title: String) {
        val createCapture = client?.createCapture(commonAuthHeader(), title = title)
        createCapture?.enqueue(object : Callback<CreateCaptures>{
            override fun onFailure(call: Call<CreateCaptures>?, t: Throwable?) {
                TODO("Not yet implemented")
            }

            override fun onResponse(
                call: Call<CreateCaptures>?,
                response: Response<CreateCaptures>?
            ) {
                RequiredData.sourceLink = response?.body()?.signedUrls?.source!!
                RequiredData.slug = response?.body()?.capture?.slug.toString()
                Toast.makeText(this@MainActivity, "Capture Created", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun getCredits() {
//        val credits = client?.getCredits(commonAuthHeader())
//        credits?.enqueue(object : Callback<CreditsResponse>{
//            override fun onResponse(
//                call: Call<CreditsResponse>?,
//                response: Response<CreditsResponse>?
//            ) {
//                Toast.makeText(this@MainActivity, "Remaining credits ${response?.body()?.remaining}", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onFailure(call: Call<CreditsResponse>?, t: Throwable?) {
//                TODO("Not yet implemented")
//            }
//        }
//        )
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_VIDEO),
                0);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MANAGE_DOCUMENTS),
                0);
        }

//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                0);
//        }
//
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                0);
//        }
//
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS)
//            != PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MANAGE_DOCUMENTS),
//                0);
//        }
    }



//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 0 || resultCode == 0) {
//
//            val map = HashMap<String, String>()
//
//            map["Authorization"] = "Bearer public_kW15bMw8zubXpxpDxzMYRUTKaJyV"
//
//            val client = APIClient.client.create(APIInterface::class.java)
//            val file = getPathFromURI(data?.data)?.let { File(it) }
////            val file = File("/storage/emulated/0/DCIM/Camera/20230527_030639.jpg")
//            val requestFile = RequestBody.create(MediaType.parse("image/jpg"), file)
//// MultipartBody.Part is used to send also the actual file name
//
//// MultipartBody.Part is used to send also the actual file name
//            val body = MultipartBody.Part.createFormData("file", file!!.name, requestFile)
//            val response = client.doGetListResources(body, map)
//            response.enqueue(object : Callback<ExampleJson2KtKotlin> {
//                override fun onResponse(
//                    call: Call<ExampleJson2KtKotlin>?,
//                    response: Response<ExampleJson2KtKotlin>?
//                ) {
//                }
//
//                override fun onFailure(call: Call<ExampleJson2KtKotlin>?, t: Throwable?) {
//                }
//            })
//        }
//    }


    private fun plainTextHeader(): Map<String, String> {
        val hashMap = HashMap<String, String>()
        hashMap["Content-Type"] = "text/plain"
        return hashMap
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
            val column = arrayOf(MediaStore.Video.Media.DATA)
            // where id is equal to
            val sel = MediaStore.Video.Media._ID + "=?"
            val cursor: Cursor? = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
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

//fun getFilePathFromUri(context: Context, uri: Uri, uniqueName: Boolean): String =
//    if (uri.path?.contains("file://") == true) uri.path!!
//    else getFileFromContentUri(context, uri, uniqueName).path


private fun getFileExtension(context: Context, uri: Uri): String? =
    if (uri.scheme == ContentResolver.SCHEME_CONTENT)
        MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
    else uri.path?.let { MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(it)).toString()) }


fun getFilePathFromURI(context: Context, contentUri: Uri?): String? {
    //copy file and send new file path
    val wallpaperDirectory: File = File(
        "${Environment.getExternalStorageDirectory()}${IMAGE_DIRECTORY}"
    )
    // have the object build the directory structure, if needed.
    if (!wallpaperDirectory.exists()) {
        wallpaperDirectory.mkdirs()
    }
    val copyFile = File(
        wallpaperDirectory.toString() + File.separator + Calendar.getInstance()
            .getTimeInMillis() + ".mp4"
    )
    // create folder if not exists
    copy(context, contentUri, copyFile)
    Log.d("vPath--->", copyFile.absolutePath)
    return copyFile.absolutePath
}

fun copy(context: Context, srcUri: Uri?, dstFile: File?) {
    try {
        val inputStream = context.contentResolver.openInputStream(srcUri!!) ?: return
        val outputStream: OutputStream = FileOutputStream(dstFile)
        copystream(inputStream, outputStream)
        inputStream.close()
        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

@Throws(java.lang.Exception::class, IOException::class)
fun copystream(input: InputStream?, output: OutputStream?): Int {
    val buffer = ByteArray(BUFFER_SIZE)
    val `in` = BufferedInputStream(input, BUFFER_SIZE)
    val out = BufferedOutputStream(output, BUFFER_SIZE)
    var count = 0
    var n = 0
    try {
        while (`in`.read(buffer, 0, BUFFER_SIZE).also { n = it } != -1) {
            out.write(buffer, 0, n)
            count += n
        }
        out.flush()
    } finally {
        try {
            out.close()
        } catch (e: IOException) {
            Log.e(e.message, e.toString())
        }
        try {
            `in`.close()
        } catch (e: IOException) {
            Log.e(e.message, e.toString())
        }
    }
    return count
}
fun getFilePathFromUri(context: Context, uri: Uri): String {
    var filePath = ""
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        val columnIndex: Int = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        it.moveToFirst()
        filePath = it.getString(columnIndex)
    }
    return filePath
}

fun uriToFile(context: Context,uri: Uri): File {
    val filePath = getFilePathFromUri(context,uri)
    return File(filePath)
}




fun uriToFile1(context: Context, uri: Uri): File? {
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    val contentResolver: ContentResolver = context.contentResolver
    val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val filePath = it.getString(columnIndex)
            return File(filePath)
        }
    }
    return null
}
private fun createVideoOutputPath(context: Context, uri: Uri): String {
    val contentResolver = context.contentResolver

    /** Represent the videos collection */


    /** Represents the data values of the video to be saved */
    val contentValues = ContentValues()

    // Adding the file title to the content values
    contentValues.put(
        MediaStore.Video.Media.TITLE,
        "VID_" + System.currentTimeMillis() + ".mp4"
    )
    // Adding the file display name to the content values
    contentValues.put(
        MediaStore.Video.Media.DISPLAY_NAME,
        "VID_" + System.currentTimeMillis() + ".mp4"
    )
    /** Represents the uri of the inserted video */
    val videoUri = contentResolver.insert(uri, contentValues)!!
    // Opening a stream on to the content associated with the video content uri
    contentResolver.openOutputStream(videoUri)

    /** Represents the file path of the video uri */
    val outputPath = getUriRealPath(contentResolver, videoUri)!!
    // Deleting the video uri to create it later with the actual video
    contentResolver.delete(videoUri, null, null)
    return outputPath
}

private fun getUriRealPath(contentResolver: ContentResolver, uri: Uri): String {
    var filePath = ""
    val cursor = contentResolver.query(uri, null, null, null, null)
    if (cursor != null) {
        if (cursor.moveToFirst()) {
            var columnName = MediaStore.Images.Media.DATA
            when (uri) {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> {
                    columnName = MediaStore.Images.Media.DATA
                }
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI -> {
                    columnName = MediaStore.Video.Media.DATA
                }
            }
            val filePathColumnIndex = cursor.getColumnIndex(columnName)
            filePath = cursor.getString(filePathColumnIndex)
        }
        cursor.close()
    }
    return filePath
}

private fun getVideoFilePathFromMediaStore(contentResolver: ContentResolver, uri: Uri): String? {
    var filePath: String? = null
    val projection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME)

    val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndex(MediaStore.Video.Media._ID)
            val videoId = it.getLong(columnIndex)
            val columnDisplayName = it.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
            val displayName = it.getString(columnDisplayName)

            val contentUri = Uri.withAppendedPath(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoId.toString()
            )
            val projectionData = arrayOf(MediaStore.Video.Media.DATA)

            contentResolver.query(contentUri, projectionData, null, null, null)?.use { dataCursor ->
                if (dataCursor.moveToFirst()) {
                    val columnIndexData = dataCursor.getColumnIndex(MediaStore.Video.Media.DATA)
                    filePath = dataCursor.getString(columnIndexData)
                }
            }
        }
    }

    return filePath
}

fun getFileFromVideoUri1(context: Context, videoUri: Uri): File? {
    val contentResolver: ContentResolver = context.contentResolver
    val videoFile: File?
    var inputStream: InputStream? = null
    var outputStream: FileOutputStream? = null

    try {
        val videoFileName = getFileName(context, videoUri)
        val cacheDir = context.cacheDir
        videoFile = File(cacheDir, videoFileName)

        inputStream = contentResolver.openInputStream(videoUri)
        outputStream = FileOutputStream(videoFile)

        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var bytesRead: Int

        while (inputStream?.read(buffer).also { bytesRead = it!! } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }

        return videoFile
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            inputStream?.close()
            outputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    return null
}

@SuppressLint("Range")
private fun getFileName(context: Context, uri: Uri): String {
    var fileName: String? = null

    if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
    }

    if (fileName == null) {
        fileName = uri.path?.substringAfterLast('/')
    }

    return fileName ?: "video_file"
}