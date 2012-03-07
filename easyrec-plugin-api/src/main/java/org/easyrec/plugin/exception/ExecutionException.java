package org.easyrec.plugin.exception;

public class ExecutionException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -2621147639926809922L;

    public ExecutionException() {
        super();
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(Throwable cause) {
        super(cause);
    }

}
