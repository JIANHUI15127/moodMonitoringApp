package com.example.moodmonitoringapp.recognizer.tensorflow

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.media.FaceDetector
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.moodmonitoringapp.util.extensions.pixelColor
import com.example.moodmonitoringapp.util.extensions.resetConfig
import com.example.moodmonitoringapp.util.extensions.toBlackWhite
import com.example.moodmonitoringapp.util.getEmotion

import org.tensorflow.lite.Interpreter
import java.io.File

// jeez!
// input data type to the model [n, 48, 48, 1]
// So this is what happens here:
typealias InputType = Array<Pixels>
typealias OutputType = Array<FloatArray?>

typealias Pixels = Array<Array<FloatArray>>

class EmotionClassifier(context: Context) {

    private val width = 48
    private val height = 48
    private val modelPath = "model.tflite"
    private val model = Interpreter(loadModelFile(context))

    /**
     * Классифицирует эмоции лица на изображении
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun classify(bitmap: Bitmap): Map<String, Int>? {
        val faces = getFaces(bitmap = bitmap)
        if (faces.isNotEmpty()) {
            val startFacePoint = getStartPoint(face = faces[0])
            val croppedBitmap = Bitmap.createBitmap(bitmap,
                startFacePoint.x.toInt(), startFacePoint.y.toInt(), width, height)

            val input = prepareData(croppedBitmap)
            val output: OutputType = Array(1) {
                return@Array FloatArray(7) {
                    return@FloatArray 0f
                }
            }
            model.run(input, output)

            // Преобразуем вывод из нейросети в нормальный вид
            if (output.isNotEmpty()) {
                return output[0]?.mapIndexed { index, it ->
                    getEmotion(index) to (it * 100).toInt()
                }?.filter { it.second != 0 }?.toMap()
            }
        }

        return null
    }

    /**
     * Loads model from application assets, saves and returns model file
     */
    private fun loadModelFile(context: Context): File {
        val inputStream = context.assets.open(modelPath)
        val readBytes = inputStream.readBytes()
        val file = File(context.filesDir, modelPath)
        if (!file.exists()) {
            file.writeBytes(readBytes)
        }
        return file
    }

    /**
     *
    Recognizes a face in an image
     */
    private fun getFaces(bitmap: Bitmap): Array<FaceDetector.Face> {
        // Распознаем одно лицо
        val image = bitmap.resetConfig(Bitmap.Config.RGB_565)
        val faceDetector = FaceDetector(image.width, image.height, 1)
        val faces: Array<FaceDetector.Face?> = Array(1) {
            return@Array null
        }
        faceDetector.findFaces(image, faces) // faces - out параметр, в нем результат
        return faces.filterNotNull().toTypedArray()
    }

    /**
     * Найти точку, от которой начинается прямоугольник с лицом (левый верхний угол)
     */
    private fun getStartPoint(face: FaceDetector.Face): PointF {
        val midFacePoint = PointF(0f ,0f)
        face.getMidPoint(midFacePoint)

        // Приблизительно начало прямоугольника с лицом
        return PointF(midFacePoint.x - 24, midFacePoint.y - 24)
    }

    /**
     * Преобразование изображения в понятный для модели вид
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun prepareData(bitmap: Bitmap): InputType {
        val image: MutableList<Array<FloatArray>> = mutableListOf()
        for (y in 0 until height) {
            val row = mutableListOf<FloatArray>()
            for (x in 0 until width) {
                val value = bitmap.pixelColor(x, y).toBlackWhite().red()
                row.add(FloatArray(1) {
                    return@FloatArray value
                })
            }
            image.add(row.toTypedArray())
        }
        return arrayOf(image.toTypedArray())
    }
}