package smsplus;

import java.util.Date;

public class Smsschedules
{
  private long Id;
  private String taskId;
  private String addressBookId;
  private Date DateCreated;
  private String DeliveryTime;
  private String header;
  private String MessageContent;
  private String MessageSize;
  private String username;
  private String accountNumber;
  private String status;

  public long getId()
  {
    return this.Id;
  }

  public void setId(long Id)
  {
    this.Id = Id;
  }

  public String getTaskId()
  {
    return this.taskId;
  }

  public void setTaskId(String taskId)
  {
    this.taskId = taskId;
  }

  public String getAddressBookId()
  {
    return this.addressBookId;
  }

  public void setAddressBookId(String addressBookId)
  {
    this.addressBookId = addressBookId;
  }

  public Date getDateCreated()
  {
    return this.DateCreated;
  }

  public void setDateCreated(Date DateCreated)
  {
    this.DateCreated = DateCreated;
  }

  public String getDeliveryTime()
  {
    return this.DeliveryTime;
  }

  public void setDeliveryTime(String DeliveryTime)
  {
    this.DeliveryTime = DeliveryTime;
  }

  public String getHeader()
  {
    return this.header;
  }

  public void setHeader(String header)
  {
    this.header = header;
  }

  public String getMessageContent()
  {
    return this.MessageContent;
  }

  public void setMessageContent(String MessageContent)
  {
    this.MessageContent = MessageContent;
  }

  public String getMessageSize()
  {
    return this.MessageSize;
  }

  public void setMessageSize(String MessageSize)
  {
    this.MessageSize = MessageSize;
  }

  public String getUsername()
  {
    return this.username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getAccountNumber()
  {
    return this.accountNumber;
  }

  public void setAccountNumber(String accountNumber)
  {
    this.accountNumber = accountNumber;
  }

  public String getStatus()
  {
    return this.status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }
}

/* Location:           C:\Users\akin\Desktop\MessageScheduler1.36.jar
 * Qualified Name:     cyclops.Smsschedules
 * JD-Core Version:    0.6.2
 */