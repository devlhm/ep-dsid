package eachare;

public class Clock {
    private int value = 0;

    public int getValue() {
        return value;
    }

    public int increment() {
        value++;
        System.out.println("=> Atualizando relogio para " + value);
        return value;
    }
}
