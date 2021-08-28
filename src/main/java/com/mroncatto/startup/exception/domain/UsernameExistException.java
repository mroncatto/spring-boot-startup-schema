package com.mroncatto.startup.exception.domain;

public class UsernameExistException extends Exception{
    public UsernameExistException(String message) {
        super(message);
    }
}
