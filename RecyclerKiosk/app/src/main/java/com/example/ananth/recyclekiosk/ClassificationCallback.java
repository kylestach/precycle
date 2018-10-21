package com.example.ananth.recyclekiosk;

import java.util.Map;

public interface ClassificationCallback {
    void onError(Exception e);
    void onFinished(Classification classification);
}
