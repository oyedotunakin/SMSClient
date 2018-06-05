/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smsplus;


 
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import    java.util.HashMap;

/**
 *
 * @author bogeyingbo
 */
public class HTTPSendInterface  implements   HTTPInterface  {
    
    

    
    
    
    
    
    
  static String  urlComposer2(Message msg)
    throws UnsupportedEncodingException
  {
      
      
      StringBuilder param = new StringBuilder(); 
        System.out.println((new StringBuilder()).append("THE MESSAGE ID IS").append(msg.getId()).toString());  
        System.out.println("Inside compose Message URL");
        param.append("http://").append(serverIP).append(":");
        param.append("8080").append("/BulkSms/SendQuickMessageAPI");
        param.append("?header=").append(URLEncoder.encode(msg.getSourceAddress(), "UTF-8"));
        param.append("&message=").append(URLEncoder.encode(msg.getMessageContent(), "UTF-8"));
        param.append("&phone=").append(URLEncoder.encode(msg.getDestAddress(), "UTF-8"));
        param.append("&username=").append(URLEncoder.encode(username, "UTF-8"));
        param.append("&password=").append(URLEncoder.encode(password, "UTF-8"));
        System.out.println((new StringBuilder()).append("The parameters composed URL is ").append(param.toString()).toString());
         

    System.out.println(param.toString());
    return param.toString();
  }
     
     
     
     
     
     
     
     
    
}
