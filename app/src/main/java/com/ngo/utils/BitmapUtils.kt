package com.ngo.utils

import android.graphics.*
import android.os.Environment
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class BitmapUtils {
        companion object
    }

    fun BitmapUtils.Companion.getCompressedImage(pathName: String, scalingLogic: ImageView.ScaleType): String {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, options)
        options.inJustDecodeBounds = false
        val dstWidth: Double = ((options.outWidth.toDouble() / (options.outWidth.toDouble() * options.outHeight.toDouble())) * 1000) * options.outWidth.toDouble()
        val dstHeight: Double = ((options.outHeight.toDouble() / (options.outWidth.toDouble() * options.outHeight.toDouble())) * 1000) * options.outHeight.toDouble()
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth.toInt(), dstHeight.toInt(), scalingLogic)
        val unscaledBitmap = BitmapFactory.decodeFile(pathName, options)

        return createScaledBitmap(unscaledBitmap, dstWidth.toInt(), dstHeight.toInt(), scalingLogic)
    }

    private fun createScaledBitmap(unscaledBitmap: Bitmap, dstWidth: Int, dstHeight: Int, scalingLogic: ImageView.ScaleType): String {
        val srcRect = calculateSrcRect(unscaledBitmap.width, unscaledBitmap.height, dstWidth, dstHeight, scalingLogic)
        val dstRect = calculateDstRect(unscaledBitmap.width, unscaledBitmap.height, dstWidth, dstHeight, scalingLogic)
        val scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(scaledBitmap)
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, Paint(Paint.FILTER_BITMAP_FLAG))

        return writeToFile(scaledBitmap)
    }

    private fun writeToFile(scaledBitmap: Bitmap): String {
        val f: File = File(Environment.getExternalStorageDirectory(), "${/*Utilities.dateTime()*/123}.png")
        f.createNewFile();
        //Convert bitmap to byte array
        val bos: ByteArrayOutputStream = ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);

        //write the bytes in file
        val fos: FileOutputStream = FileOutputStream(f)
        fos.write(bos.toByteArray());
        fos.flush();
        fos.close();

        return f.absolutePath
    }

    private fun calculateSampleSize(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, scalingLogic: ImageView.ScaleType): Int {
        if (scalingLogic == ImageView.ScaleType.FIT_CENTER) {
            val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
            val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

            if (srcAspect > dstAspect) {
                return srcWidth / dstWidth
            } else {
                return srcHeight / dstHeight
            }
        } else {
            val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
            val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight
            } else {
                return srcWidth / dstWidth
            }
        }
    }

    private fun calculateSrcRect(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, scalingLogic: ImageView.ScaleType): Rect {
        if (scalingLogic == ImageView.ScaleType.CENTER_CROP) {
            val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
            val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

            if (srcAspect > dstAspect) {
                val srcRectWidth = (srcHeight * dstAspect).toInt()
                val srcRectLeft = (srcWidth - srcRectWidth) / 2
                return Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight)
            } else {
                val srcRectHeight = (srcWidth / dstAspect).toInt()
                val scrRectTop = (srcHeight - srcRectHeight).toInt() / 2
                return Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight)
            }
        } else {
            return Rect(0, 0, srcWidth, srcHeight)
        }
    }

    private fun calculateDstRect(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, scalingLogic: ImageView.ScaleType): Rect {
        if (scalingLogic == ImageView.ScaleType.FIT_CENTER) {
            val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
            val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

            if (srcAspect > dstAspect) {
                return Rect(0, 0, dstWidth, (dstWidth / srcAspect).toInt())
            } else {
                return Rect(0, 0, (dstHeight * srcAspect).toInt(), dstHeight)
            }
        } else {
            return Rect(0, 0, dstWidth, dstHeight)
        }

}