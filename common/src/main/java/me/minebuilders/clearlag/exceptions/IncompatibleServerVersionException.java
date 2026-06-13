package me.minebuilders.clearlag.exceptions;

/**
 * @author bob7l
 */
public class IncompatibleServerVersionException extends Exception {

    public IncompatibleServerVersionException(String message) {
        super(message);
    }

    public IncompatibleServerVersionException() {
        this("Incompatible server version");
    }
}
