package com.example.myapplication.tflite;

import android.graphics.Bitmap;

public interface BinaryClassifier {
    boolean recognizeImage(Bitmap bitmap);

    void setNumThreads(int num_threads);
}