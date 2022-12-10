package com.example.moodmonitoringapp.util.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi

fun Bitmap.resetConfig(config: Bitmap.Config): Bitmap {
    val convertedBitmap = Bitmap.createBitmap(width, height, config)
    val canvas = Canvas(convertedBitmap)
    val paint = Paint()
    paint.color = Color.BLACK
    canvas.drawBitmap(this, 0f, 0f, paint)
    return convertedBitmap
}

@RequiresApi(Build.VERSION_CODES.O)
fun Color.toBlackWhite(): Color {
    val c = (red() + green() + blue()) / 3
    return Color.valueOf(c,c,c)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Bitmap.pixelColor(x: Int, y: Int): Color {
    val pixel = getPixel(x, y)
    val redValue = Color.red(pixel)
    val blueValue = Color.blue(pixel)
    val greenValue = Color.green(pixel)
    return Color.valueOf(redValue.toFloat(), greenValue.toFloat(), blueValue.toFloat())
}