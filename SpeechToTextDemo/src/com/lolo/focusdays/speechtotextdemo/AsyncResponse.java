package com.lolo.focusdays.speechtotextdemo;

public interface AsyncResponse<T, S> {
    void processFinish(T output);
    void processProgress(S level);
    void processTime(long timeInMs);
}