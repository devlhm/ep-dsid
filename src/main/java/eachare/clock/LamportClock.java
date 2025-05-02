package eachare.clock;

import eachare.Message;

public class LamportClock extends Clock {

	@Override
	public void onSendMessage(Message message) {
		increment();
	}

	@Override
	public void onReceiveMessage(Message message) {
		setValue(Math.max(value, message.getClockValue()) + 1);
	}
}
