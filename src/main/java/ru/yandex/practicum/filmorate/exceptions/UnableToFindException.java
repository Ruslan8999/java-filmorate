package ru.yandex.practicum.filmorate.exceptions;

public class UnableToFindException extends RuntimeException{
    public UnableToFindException() {
        super("Не верно указан ID");
    }
}