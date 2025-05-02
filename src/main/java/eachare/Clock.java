package eachare;

public class Clock {
	protected int value = 0;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		System.out.println("=> Atualizando relogio para " + value);
	}

	public void increment() { setValue(value + 1); }

	public void onSendMessage(Message message) {
		increment();
	}

	public void onReceiveMessage(Message message) {
		setValue(Math.max(value, message.getClockValue()) + 1);
	}
}