package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
