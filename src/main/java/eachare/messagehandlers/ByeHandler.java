package eachare.messagehandlers;

public class ByeHandler implements MessageHandler{
    private final eachare.MessageSender messageSender;

    public ByeHandler(eachare.MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void execute(eachare.Message message) {
        messageSender.closeConnection(message.getOriginAddress(), message.getOriginPort());
    }

}
