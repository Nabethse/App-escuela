package com.myapplication.core.util

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun bitmapToBase64DataUrl(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val bytes = outputStream.toByteArray()
        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        return "data:image/jpeg;base64,$base64"
    }

    fun buildPhotoUrl(baseUrl: String, photoPath: String?): String? {
        if (photoPath.isNullOrBlank()) return null
        // Si ya es una URL completa (S3 o Base64), retornarla tal cual
        if (photoPath.startsWith("http") || photoPath.startsWith("data:image")) return photoPath
        
        // Si es una ruta relativa del servidor (/uploads/...)
        return baseUrl.trimEnd('/') + if (photoPath.startsWith("/")) photoPath else "/$photoPath"
    }
}
