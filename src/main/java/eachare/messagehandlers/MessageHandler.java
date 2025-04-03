package eachare.messagehandlers;

import eachare.Message;

public interface MessageHandler {
    void execute(Message message);
}
