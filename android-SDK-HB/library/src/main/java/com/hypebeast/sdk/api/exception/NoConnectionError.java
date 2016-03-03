package com.hypebeast.sdk.api.exception;

/**
 * Created by hesk on 3/3/16.
 */
public class NoConnectionError extends Exception {

    public NoConnectionError() {
    }

    public NoConnectionError(String detailMessage) {
        super(detailMessage);
    }

    public NoConnectionError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoConnectionError(Throwable throwable) {
        super(throwable);
    }

    /**
     * Returns the detail message which was provided when this
     * {@code Throwable} was created. Returns {@code null} if no message was
     * provided at creation time.
     */
    @Override
    public String getMessage() {
        return "There is no internet connection detected from the current device.";
    }
}
