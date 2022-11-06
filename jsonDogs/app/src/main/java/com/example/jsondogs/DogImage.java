package com.example.jsondogs;

import androidx.annotation.NonNull;

public class DogImage {
    private String message;
    private String status;

    public DogImage(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @NonNull
    @Override
    public String toString() {
        return "Dog image{" + message + " " + status + "}";
    }
}
