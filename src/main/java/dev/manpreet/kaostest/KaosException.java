package dev.manpreet.kaostest;

public class KaosException extends RuntimeException {

    public KaosException(String message) {
        super(message);
    }

    public KaosException(String message, Throwable exception) {
        super(message, exception);
    }
}
