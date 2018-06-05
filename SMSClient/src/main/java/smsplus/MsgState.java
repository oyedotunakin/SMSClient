/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smsplus;

/**
 *
 * @author bogeyingbo
 */
public interface MsgState {
    
      
    /**  
     T  -  Pending
 Q  -  Queued
 S  -  Sent
 E  -  Communication Error
 F  -  Data incorrect errror (Fail)
  **/
    
 static final String  SENT = "S";
 static final String  PENDING = "T";
 static final String  COMMUNICATION_ERROR = "E";
 static final String  FAILED = "F";
    
}
