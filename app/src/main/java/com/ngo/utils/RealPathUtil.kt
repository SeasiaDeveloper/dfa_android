package com.ngo.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.loader.content.CursorLoader
import java.io.ByteArrayOutputStream


object RealPathUtil {
    fun getRealPath(context: Context, fileUri: Uri): String? {
        val realPath: String?
        // SDK < API11
        realPath = if (Build.VERSION.SDK_INT < 11) {
            getRealPathFromURI_BelowAPI11(context, fileUri)
        } else if (Build.VERSION.SDK_INT < 19) {
            getRealPathFromURI_API11to18(context, fileUri)
        } else {
            getRealPathFromURI_API19(context, fileUri)
        }
        return realPath
    }

    fun getThumbnailFromVideo(videoPath: String): Bitmap {
        var videoPath = videoPath
        if (videoPath.contains("file://")) {
            videoPath = videoPath.replace("file://", "")
        }
        return ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND)
    }

    fun retriveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= 14) mediaMetadataRetriever.setDataSource(
                videoPath,
                HashMap()
            ) else mediaMetadataRetriever.setDataSource(videoPath)
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }

    @SuppressLint("NewApi")
    fun getRealPathFromURI_API11to18(
        context: Context?,
        contentUri: Uri?
    ): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        var result: String? = null
        val cursorLoader =
            CursorLoader(context!!, contentUri!!, proj, null, null, null)
        val cursor = cursorLoader.loadInBackground()
        if (cursor != null) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(column_index)
            cursor.close()
        }
        return result
    }

    fun getRealPathFromURI_BelowAPI11(
        context: Context,
        contentUri: Uri?
    ): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor =
            context.contentResolver.query(contentUri!!, proj, null, null, null)
        var column_index = 0
        var result = ""
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(column_index)
            cursor.close()
            return result
        }
        return result
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    fun getRealPathFromURI_API19(
        context: Context,
        uri: Uri
    ): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) { // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(
                uri.scheme,
                ignoreCase = true
            )
        ) { // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun imageRotateIfRequired(photoPath:String,bitmap: Bitmap):Bitmap
    {
        val ei = ExifInterface(photoPath)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        var rotatedBitmap: Bitmap? = null
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap =
                rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap =
                rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap =
                rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
            else -> rotatedBitmap = bitmap
        }
        return rotatedBitmap!!
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

/*    private fun getPickImageResultUri(data: Intent): Uri {
        var isCamera = true
        var uri: Uri
        if (data != null) {
            var action = data.getAction()
            isCamera = action != null *//*&& action.equals(MediaStore.ACTION_IMAGE_CAPTURE)*//*
        }
        if (isCamera) {
            uri = getCaptureImageOutputUri()
        } else {
            uri = intent?.getExtras()!!.get("data") as Uri
        }
        return uri
    }*/

    fun getImageUriWhenTakePhoto(inContext: Context, inImage: Bitmap): Uri {
        var OutImage = Bitmap.createScaledBitmap(inImage, 1200, 1200, true)
        var path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            OutImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getCapturedImage(selectedPhotoUri: Uri, context:Context): Bitmap {
        val bitmap = when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                selectedPhotoUri
            )
            else -> {
                val source = ImageDecoder.createSource(context.contentResolver, selectedPhotoUri)
                ImageDecoder.decodeBitmap(source)
            }
        }
        return bitmap
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(
                inContext.contentResolver,
                inImage,
                "Title",
                null
            )
        return Uri.parse(path)
    }

    fun decodeSampledBitmapFromResource(
        res: Resources, resId: Int,
        reqWidth: Int, reqHeight: Int
    ): Bitmap? { // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int { // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
// height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight
                && halfWidth / inSampleSize > reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}