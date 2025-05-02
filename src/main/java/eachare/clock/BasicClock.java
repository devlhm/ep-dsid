package eachare.clock;

import eachare.Message;

public class BasicClock extends Clock {
    @Override
    public void onSendMessage(Message message) {
        increment();
    }

    @Override
    public void onReceiveMessage(Message message) {
        increment();
    }
}
