package com.lolo.focusdays.speechtotextdemo;

public interface AsyncResponse<T> {
    void processFinish(T output);
    void processTime(long timeInMs);
}