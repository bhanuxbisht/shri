package com.seva.scripture.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareUtils {

    fun shareText(context: Context, payload: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, payload)
        }
        context.startActivity(Intent.createChooser(intent, "Share Shloka"))
    }

    fun shareAsImage(context: Context, title: String, sanskrit: String, meaning: String) {
        val bitmap = renderShlokaCard(title, sanskrit, meaning)
        val file = File(context.cacheDir, "shared_shloka.png")
        FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Shloka"))
    }

    private fun renderShlokaCard(title: String, sanskrit: String, meaning: String): Bitmap {
        val width = 1080
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.parseColor("#F7F1E5"))

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#C9782A")
            textSize = 54f
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.SERIF
        }
        val sanskritPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#43372F")
            textSize = 44f
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.SERIF
        }
        val meaningPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#43372F")
            textSize = 34f
            textAlign = Paint.Align.LEFT
        }

        canvas.drawText(title, width / 2f, 120f, titlePaint)

        val sanskritLines = wrapCentered(sanskrit, sanskritPaint, width - 140)
        var y = 240f
        sanskritLines.forEach {
            canvas.drawText(it, width / 2f, y, sanskritPaint)
            y += 64f
        }

        y += 60f
        val meaningLines = wrapLeft(meaning, meaningPaint, width - 140)
        meaningLines.forEach {
            canvas.drawText(it, 70f, y, meaningPaint)
            y += 52f
        }

        return bitmap
    }

    private fun wrapCentered(text: String, paint: TextPaint, maxWidth: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var line = ""
        for (word in words) {
            val next = if (line.isBlank()) word else "$line $word"
            if (paint.measureText(next) <= maxWidth) {
                line = next
            } else {
                lines += line
                line = word
            }
        }
        if (line.isNotBlank()) lines += line
        return lines
    }

    private fun wrapLeft(text: String, paint: TextPaint, maxWidth: Int): List<String> {
        return wrapCentered(text, paint, maxWidth)
    }
}
