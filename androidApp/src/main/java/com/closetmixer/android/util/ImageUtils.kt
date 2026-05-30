package com.closetmixer.android.util

import android.content.Context
import android.net.Uri
import java.io.File

fun copyImageToInternalStorage(context: Context, uri: Uri): String {
    val dir = File(context.filesDir, "articles")
    dir.mkdirs()
    val file = File(dir, "article_${System.currentTimeMillis()}.jpg")
    context.contentResolver.openInputStream(uri)?.use { input ->
        file.outputStream().use { output -> input.copyTo(output) }
    }
    return file.absolutePath
}
