package com.closetmixer.android.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.closetmixer.domain.usecase.GeneratedOutfit
import com.google.android.play.core.review.ReviewManagerFactory
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

fun copyFileToInternalStorage(context: Context, source: File): String {
    val dir = File(context.filesDir, "articles")
    dir.mkdirs()
    val dest = File(dir, "article_${System.currentTimeMillis()}.jpg")
    source.copyTo(dest, overwrite = true)
    source.delete()
    return dest.absolutePath
}

fun shareOutfit(context: Context, outfit: GeneratedOutfit) {
    val photoPaths = listOfNotNull(
        outfit.haut?.photoPath,
        outfit.bas?.photoPath,
        outfit.chaussure?.photoPath,
        outfit.bijou?.photoPath
    ).filter { it.isNotEmpty() }

    val description = buildOutfitDescription(outfit)

    val uris: List<Uri> = photoPaths.mapNotNull { path ->
        val file = File(path)
        if (!file.exists()) return@mapNotNull null
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val intent = when {
        uris.size > 1 -> Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            putExtra(Intent.EXTRA_TEXT, description)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        uris.size == 1 -> Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uris.first())
            putExtra(Intent.EXTRA_TEXT, description)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        else -> Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, description)
        }
    }

    context.startActivity(Intent.createChooser(intent, "Partager ma tenue"))
}

fun requestInAppReview(activity: Activity) {
    val manager = ReviewManagerFactory.create(activity)
    manager.requestReviewFlow().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            manager.launchReviewFlow(activity, task.result)
        } else {
            openPlayStorePage(activity)
        }
    }
}

fun openPlayStorePage(context: Context) {
    val uri = Uri.parse("market://details?id=${context.packageName}")
    val webUri = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    } catch (_: Exception) {
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}

private fun buildOutfitDescription(outfit: GeneratedOutfit): String {
    val lines = mutableListOf<String>()
    outfit.haut?.let {
        val color = it.couleur?.let { c -> " ($c)" } ?: ""
        lines += "Haut : ${it.sousCategorie.replaceFirstChar { c -> c.uppercase() }}$color"
    }
    outfit.bas?.let {
        val color = it.couleur?.let { c -> " ($c)" } ?: ""
        lines += "Bas : ${it.sousCategorie.replaceFirstChar { c -> c.uppercase() }}$color"
    }
    outfit.chaussure?.let {
        lines += "Chaussures : ${it.sousCategorie.replaceFirstChar { c -> c.uppercase() }}"
    }
    outfit.bijou?.let {
        val metal = it.metal?.takeIf { m -> m != "aucun" }?.let { m -> " $m" } ?: ""
        lines += "Bijou : ${it.sousCategorie.replaceFirstChar { c -> c.uppercase() }}$metal"
    }

    return buildString {
        appendLine("Ma tenue du jour — ClosetMixer")
        appendLine()
        lines.forEach { appendLine(it) }
        appendLine()
        append("#ClosetMixer #TenueDuJour #OOTD #Mode")
    }
}
