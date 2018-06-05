package smsplus;

public class Main {

    public static void main(String[] args) {
        Cyclops cyclops = new Cyclops();
        CyclopsRetry cyclopsRetry = new CyclopsRetry();

        new Thread(cyclops).start();
        new Thread(cyclopsRetry).start();

    }

}

/* Location:           C:\Users\akin\Desktop\MessageScheduler1.36.jar
 * Qualified Name:     cyclops.Main
 * JD-Core Version:    0.6.2
 */
