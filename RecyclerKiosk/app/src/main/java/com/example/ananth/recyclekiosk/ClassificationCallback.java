package com.example.ananth.recyclekiosk;

public interface ClassificationCallback {
    void onError(Exception e);
    void onFinished(String classification);
}
