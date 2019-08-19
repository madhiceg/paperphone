package com.withgoogle.experiments.unplugged.ui.pdf

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import com.withgoogle.experiments.unplugged.ui.PdfModule
import timber.log.Timber
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

private const val WIDTH_RESIZE = 117.5F

class PhotoModule(private val context: Context, private val uri: Uri): PdfModule {
    private fun loadBitmap(): Bitmap? {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")

        parcelFileDescriptor?.use {
            val image = BitmapFactory.decodeFileDescriptor(it.fileDescriptor, null, BitmapFactory.Options().apply {
                inSampleSize = 1
            })
            Timber.d("Width: ${image.width}, Height: ${image.height}")

            return image
        }

        return null
    }

    override fun draw(canvas: Canvas, resources: Resources) {
        loadBitmap()?.let {
            val ma = ColorMatrix().apply {
                setSaturation(0f)
            }

            val width = it.width
            val height = it.height

            val calculatedHeight = (height.toFloat() / width.toFloat()) * WIDTH_RESIZE

            val srcRect = Rect(0, 0, width, height)
            val destRect = RectF(0F, 0F, WIDTH_RESIZE, calculatedHeight)

            canvas.drawBitmap(it, srcRect, destRect, Paint().apply {
                isAntiAlias = true
                isFilterBitmap = true
                colorFilter = ColorMatrixColorFilter(ma)
            })
        }
    }

    override val isRotated: Boolean
        get() = false
}