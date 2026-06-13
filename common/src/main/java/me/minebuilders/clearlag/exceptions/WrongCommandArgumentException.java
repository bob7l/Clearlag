package me.minebuilders.clearlag.exceptions;

import me.minebuilders.clearlag.language.messages.Message;

/**
 * @author bob7l
 */
public class WrongCommandArgumentException extends Throwable {

    private Message message;

    private final Object[] objs;

    public WrongCommandArgumentException(Message message, Object... objs) {
        this.message = message;
        this.objs = objs;
    }

    public Message getError() {
        return message;
    }

    public Object[] getReplacables() {
        return objs;
    }

    public void setError(Message message) {
        this.message = message;
    }
}
