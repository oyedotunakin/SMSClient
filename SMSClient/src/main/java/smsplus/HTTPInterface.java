/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smsplus;

import java.util.HashMap;

/**
 *
 * @author bogeyingbo
 */
public interface HTTPInterface {
    
  public final   static  String header = "A&P Foods";
  public final  static  String username = "AP Foods";
  public final  static   String password = "12345";
        
        
    public  final   static   HashMap<String, String>   configMapping     =   new    HashMap<String, String>();
    public  final  static  String   fileConfigDirectory  =   "E:/SmsIntegrationSystemFiles/mandrakeproperties.prop";
     //  "C:/Cellulant/mtnurlproperties.prop";
     
  public final  String serverIP = "78.110.169.251"; 
    
    
}
