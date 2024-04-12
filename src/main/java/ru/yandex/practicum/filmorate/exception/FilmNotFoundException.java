package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends Exception {
    public FilmNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
