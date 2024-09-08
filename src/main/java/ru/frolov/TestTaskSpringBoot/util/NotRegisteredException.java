package ru.frolov.TestTaskSpringBoot.util;

public class NotRegisteredException extends RuntimeException {
    public NotRegisteredException(String msg){
        super(msg);
    }
}
