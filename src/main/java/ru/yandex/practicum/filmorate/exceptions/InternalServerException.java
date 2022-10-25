package ru.yandex.practicum.filmorate.exceptions;

public class InternalServerException extends RuntimeException{
    public InternalServerException() {
        super("Неверное значение id");
    }

    public InternalServerException(String message) {
        super(message);
    }
}
