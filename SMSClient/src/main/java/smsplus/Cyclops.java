package smsplus;

import java.util.concurrent.LinkedBlockingQueue;

public class Cyclops extends Thread implements DBConnectInterface {

    private LinkedBlockingQueue<Message> messageQueue;
    Worker[] workers = null;

    MessagePoller poll = null;

    public void begin() {
        this.messageQueue = new LinkedBlockingQueue(POLLSIZE);

        System.out.println("Creating the worker threads");

        workers = new Worker[15];

        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(this.messageQueue);
            workers[i].start();
//            new Thread(new Worker(this.messageQueue)).start();
        }

        System.out.println("Creating the view daemon...");

        new Thread(new MessagePoller(this.messageQueue)).start();

        //  poll  =  new MessagePoller(this.messageQueue);
        //  new Thread(poll).start();
    }

    @Override
    public void run() {
        begin();
    }

}

/* Location:           C:\Users\akin\Desktop\MessageScheduler1.36.jar
 * Qualified Name:     cyclops.Cyclops
 * JD-Core Version:    0.6.2
 */
