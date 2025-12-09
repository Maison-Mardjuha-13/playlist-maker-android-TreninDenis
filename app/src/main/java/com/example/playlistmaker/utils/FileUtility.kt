package com.example.playlistmaker.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object FileUtility {

    suspend fun saveImageToInternalStorage(
        context: Context,
        uri: Uri
    ): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)

                val fileName = "cover_${UUID.randomUUID()}.jpg"
                val file = File(context.filesDir, fileName)

                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                }

                return@withContext file.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
        return@withContext null
    }

    fun getFileUri(path: String): Uri {
        return Uri.fromFile(File(path))
    }

    fun deleteImageFile(context: Context, path: String?) {
        if (path.isNullOrEmpty()) return

        try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}