package smsplus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.regex.Pattern;



public class Message  implements  MsgState
{
  private long id;
  private int messageId;
  private String statusId;
  private int no_of_Retry;
  private int maxSend;
  private int bucketId;
  private String messageContent;
  private String destAddress;
  private String sourceAddress;
  private Date dateInserted;
  private Date dateModified;
  private String accountNumber;
  private String messageSize;
  
   private  boolean  errorFlag  =  false;  
  private   String   errorMsg;
  
   private   String  msgStatus   =  MsgState.PENDING;
  
  
  
  
   public  boolean  getErrorFlag(){
      return   errorFlag;
  }
    
    
  public  String  getErrorMessage(){
      return   errorMsg;
  }
  

  public int getMessageId()
  {
    return this.messageId;
  }

  public void setMessageId(int messageId)
  {
    this.messageId = messageId;
  }

  public String getStatusId()
  {
    return this.statusId;
  }

  public void setStatusId(String statusId)
  {
    this.statusId = statusId;
  }

  public int getNo_of_Retry()
  {
    return this.no_of_Retry;
  }

  public void setNo_of_Retry(int no_of_Retry)
  {
    this.no_of_Retry = no_of_Retry;
  }

  public int getMaxSend()
  {
    return this.maxSend;
  }

  public void setMaxSend(int maxSend)
  {
    this.maxSend = maxSend;
  }

  public int getBucketId()
  {
    return this.bucketId;
  }

  public void setBucketId(int bucketId)
  {
    this.bucketId = bucketId;
  }

  public String getMessageContent()
  {
    return this.messageContent;
  }

  
  
  
  
  public void setMessageContent(String   newMessageContent)
  {
      if((newMessageContent != null) &&  (newMessageContent.equals("") == false)){
          this.messageContent =   newMessageContent.trim();
      }else{
              errorFlag  =  true;  
              msgStatus  =   MsgState.FAILED;
            if(errorMsg == null){
                errorMsg  =   "Message "+id+" has invalid short message.";
            }else{    errorMsg  =   errorMsg+"Message "+id+" has invalid short message.";   }
      }
       
  }

  
  
  
  public String getDestAddress()
  {
    return this.destAddress;
  }

  
  
  public void setDestAddress(String  newDestAddress)
  {
      if((newDestAddress != null) &&  (newDestAddress.equals("") == false)){
        //  this.destAddress =  formatDestination(newDestAddress.trim());
          this.destAddress =   newDestAddress.trim();
      }else {
          errorFlag  =  true; 
          statusId   =   "F";
          msgStatus  =   MsgState.FAILED;
                if(errorMsg == null){
                    errorMsg  =   "Message "+id+" has invalid  destination address.";
                }else{    errorMsg  =   errorMsg+"Message "+id+" has invalid destination address.";   }
      }
       
  }
  
  

  
  public void  setErrorFlag(boolean  newErrorFlagStatus)
  {
     this.errorFlag =  newErrorFlagStatus;
  }
  
  
  
  public String getSourceAddress()
  {
    return this.sourceAddress;
  }
  
  

  public void setSourceAddress(String  newSourceAddress)
  {
       if((newSourceAddress != null) && (newSourceAddress.equals("") == false)){
           this.sourceAddress = newSourceAddress.trim();
       }
  }
  
  

  public Date getDateInserted()
  {
    return this.dateInserted;
  }

  public void setDateInserted(Date dateInserted)
  {
    this.dateInserted = dateInserted;
  }

  public Date getDateModified()
  {
    return this.dateModified;
  }

  public void setDateModified(Date dateModified)
  {
    this.dateModified = dateModified;
  }

  public String getAccountNumber()
  {
    return this.accountNumber;
  }

  public void setAccountNumber(String accountNumber)
  {
    this.accountNumber = accountNumber;
  }

  public String getMessageSize()
  {
    return this.messageSize;
  }

  public void setMessageSize(String messageSize)
  {
    this.messageSize = messageSize;
  }

  public long getId()
  {
    return this.id;
  }

  public void setId(long id)
  {
    this.id = id;
  }
  
  
  
  
  
  
  
 private       String    formatDestination(String phone)
  {
    String destinationMsisdn = phone.replaceAll("[^+0-9]", ""); 
    String formattedDest = "invalid";
    try {
      if (destinationMsisdn.length() == 14) {
        if ((destinationMsisdn.startsWith("+234")) && 
          (Pattern.matches("[+][2][3][4][0-9]*", destinationMsisdn))) {
          formattedDest = destinationMsisdn;
        }else{
            System.out.println("DESTINATION ERROR: "+destinationMsisdn);
        }

      } else if (destinationMsisdn.length() == 13) {
        if ((destinationMsisdn.startsWith("234")) && 
          (Pattern.matches("[2][3][4][0-9]*", destinationMsisdn))) {
             formattedDest = "+"+destinationMsisdn;
          }else{
            System.out.println("DESTINATION ERROR: "+destinationMsisdn);
        }

      } else if ((destinationMsisdn.length() == 11) && 
        (destinationMsisdn.startsWith("0"))) {
        if (Pattern.matches("[0-9]*", destinationMsisdn)){
             formattedDest = destinationMsisdn.replaceFirst("0", "+234");
         }else{
            System.out.println("DESTINATION ERROR: "+destinationMsisdn);
        }
      } else {
          
             statusId   =   "F";
             errorFlag  =  true; 
             msgStatus  =   MsgState.FAILED;
             System.out.println("DESTINATION ERROR: "+destinationMsisdn);
      }
    }
    catch (NullPointerException e)
    {
         statusId  =  "F";
         errorFlag  =  true; 
         msgStatus  =   MsgState.FAILED;
         System.out.println("DESTINATION ERROR: "+destinationMsisdn);
        System.out.println(KannelS.class.getName() + e);
    }
    return formattedDest;
  }
    
    
    
    
    
    
    
    
   public   boolean     updateRecord(Connection   cron) {
        boolean record_updated = false;
        int result = -2;
        PreparedStatement ps = null;
        
        while (record_updated == false) {            
            
            try {
                // checkConnection();
                // this.conn.setAutoCommit(false); 
                String sql = "UPDATE     SMSQueue_dml  SET Status = ?,  TryCount  =  TryCount + 1   WHERE msgID = ? ";
                ps =  cron.prepareStatement(sql);
                ps.setString(1, msgStatus);
                ps.setLong(2, this.id);

                ps.setQueryTimeout(5);
                result = ps.executeUpdate();

                result = 1;
                record_updated = true;
                System.out.println(ps.toString());
            } catch (Exception cE) {
              //  errorMet = true;
                record_updated = false;
                System.out.println("Communication error to remote database encountered. "); 
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                   ex.printStackTrace();
                }
             //   refreshConnection();
                result = -1;           
                System.out.println(cE.getMessage());
                
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                        ps = null;
                    }
                } catch (Exception ex) {
                    // Log.l.infoLog.info("Error encountered when attempting to close prepared statement");    
                    ex.printStackTrace();
                }
            }
        }

   return   record_updated;


    }
  
   
   
   
   
   
   
   
    
    
    
}

/* Location:           C:\Users\akin\Desktop\MessageScheduler1.36.jar
 * Qualified Name:     cyclops.Message
 * JD-Core Version:    0.6.2
 */