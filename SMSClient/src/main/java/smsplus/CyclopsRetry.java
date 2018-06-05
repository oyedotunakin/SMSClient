package smsplus;

import java.util.concurrent.LinkedBlockingQueue;



public class CyclopsRetry   extends  Thread    implements  DBConnectInterface
{
  private LinkedBlockingQueue<Message>   messageQueueRetry;
  WorkerRetry[] workersRetry =  null;
  
  MessageRetryPoller   pollRetry  =  null;
  

  public void begin()
  {
    this.messageQueueRetry = new LinkedBlockingQueue(RETRY_POLLSIZE);

    System.out.println("Creating the worker threads");

     workersRetry = new WorkerRetry[2];
    
    
      for (int i = 0; i < workersRetry.length; i++) { 
      workersRetry[i] = new WorkerRetry(this.messageQueueRetry);
      // workers[i].start();
      new Thread(new Worker(this.messageQueueRetry)).start();
    }
      

    System.out.println("Creating the view daemon...");
    
    
    new Thread(new MessageRetryPoller(this.messageQueueRetry)).start();
    
   //  poll  =  new MessagePoller(this.messageQueue);
   //  new Thread(poll).start();
     
  }
  
  
  
  
  
 
    
 
      
      
      
      
      
          
    
    public   void  run(){
        begin(); 
    }
    
      
      
}

/* Location:           C:\Users\akin\Desktop\MessageScheduler1.36.jar
 * Qualified Name:     cyclops.Cyclops
 * JD-Core Version:    0.6.2
 */