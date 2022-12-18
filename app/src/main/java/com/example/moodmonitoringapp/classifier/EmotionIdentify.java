package com.example.moodmonitoringapp.classifier;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class EmotionIdentify {
    private  final  Interpreter interpreter;

    private EmotionIdentify(Interpreter interpreter){
        this.interpreter = interpreter;
    }

    public static EmotionIdentify classifier(AssetManager assetManager, String modelPath) throws IOException {
        ByteBuffer byteBuffer = loadModelFile(assetManager,modelPath);
        Interpreter interpreter = new Interpreter(byteBuffer);
        return  new EmotionIdentify(interpreter);
    }
    private static ByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public int recognizeImage(Bitmap bitmap){
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(bitmap);
        float[][]  result = new float[1][7];
        Log.d("run+++","in" );
        interpreter.run(byteBuffer, result);
        Log.d("run+++","out" );
        int id = 0;
        float max = 0.f;
        for (int i = 0;  i < 7; ++i){
            if (result[0][i] > max){
                max = result[0][i];
                id = i;
            }
        }
        return id;
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect( 48 * 48 * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] pixels = new int[48 * 48];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int pixel : pixels) {
            float rChannel = (pixel >> 16) & 0xFF;
            float gChannel = (pixel >> 8) & 0xFF;
            float bChannel = (pixel) & 0xFF;
            float pixelValue = (rChannel + gChannel + bChannel) / 3 / 255.f;
            byteBuffer.putFloat(pixelValue);
        }
        return byteBuffer;
    }

}
