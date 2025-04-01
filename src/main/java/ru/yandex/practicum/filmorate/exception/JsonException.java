package ru.yandex.practicum.filmorate.exception;

import java.io.IOException;

public class JsonException extends IOException {
    public JsonException(String message) {
        super(message);
    }
}
