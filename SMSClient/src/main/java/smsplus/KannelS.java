package smsplus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class KannelS {
//  private final String dKey = "w3aF1Bank";

    private static final String port = "2775";
    private static final String host = "77.75.122.3";

    static String urlComposer(Message msg)
            throws UnsupportedEncodingException {
        StringBuilder param = new StringBuilder();

        param.append("http://").append(host).append(":");

        param.append(port).append("/cgi-bin/sendsms");
   // param.append("").append("");

        param.append("?from=").append(URLEncoder.encode(msg.getSourceAddress(), "UTF-8"));
//    param.append("?from=").append(msg.getSourceAddress().trim());
        param.append("&to=").append(msg.getDestAddress());
        param.append("&username=").append("bulksender");
        param.append("&password=").append("foobar");
        param.append("&text=").append(URLEncoder.encode(msg.getMessageContent(), "UTF-8"));
//     param.append("&text=").append(msg.getMessageContent().trim());
        //   param.append("&dlr-mask=").append("31");
        //String dlrurl = "http://localhost/dlrpriority.php?dlr=%d&username=fcmbpriority&uid=12345&smsid=34565&fid=%F&oa=%p&da=%P&smsc=%i&dlr-type=%d&message=%a&time=%t&internalId=%I%22&FCMBID=" + msg.getId();
        //  String dlrurl = "http://localhost:8080/dlr/DoUpdate?dlr=%d&table=SMSQueue_dml&username=fcmbpriority&uid=12345&smsid=34565&fid=%F&oa=%p&da=%P&smsc=%i&dlr-type=%d&message=%a&time=%t&internalId=%I%22&FCMBID=" + msg.getId();
        //   param.append("&dlr-url=").append(URLEncoder.encode(dlrurl, "UTF-8"));
        //  param.append("&dlr-url=").append(dlrurl);

        System.out.println(param.toString());
        return param.toString();
    }

  //  http://127.0.0.1:13013/cgi-bin/sendsms?from=FCMB&to=2348083064718&username=tester&password=foobar&text=testing this
    static int sendSms(Message msg) {
        int responseCode = 0;
        String responseMessage;
        try {
            URL myUrl = new URL(urlComposer(msg));
            System.out.println("Opening connection " + myUrl.toString());
            HttpURLConnection response = (HttpURLConnection) myUrl.openConnection();
            responseCode = response.getResponseCode();
            System.out.println("HTTP result " + response.getResponseMessage() + " code " + response.getResponseCode());
            responseMessage = response.getResponseMessage();
            System.out.println("  Response  for " + msg.getMessageId() + " --- HTTP_STATUS_CODE - " + responseCode + "--- HTTP_STATUS_REASON - " + responseMessage);

        } catch (IOException ex) {
//      Log.l.fatalLog.fatal(KannelS.class.getName() + ex);
            responseCode = 111;
        } catch (Exception ex) {
            System.out.println(KannelS.class.getName() + ex);
            System.out.println(ex.getMessage());
            responseCode = 111;
        }
        return responseCode;
    }

    public static String formatDestination(String phone) {
        String destinationMsisdn = phone.replaceAll("[^+0-9]", "");
        String formattedDest = "invalid";
        try {
            if (destinationMsisdn.length() == 14) {
                if ((destinationMsisdn.startsWith("+234"))
                        && (Pattern.matches("[+][2][3][4][0-9]*", destinationMsisdn))) {
                    formattedDest = destinationMsisdn;
                } else {
                    System.out.println("DESTINATION ERROR: " + destinationMsisdn);
                }

            } else if (destinationMsisdn.length() == 13) {
                if ((destinationMsisdn.startsWith("234"))
                        && (Pattern.matches("[2][3][4][0-9]*", destinationMsisdn))) {
                    formattedDest = "+" + destinationMsisdn;
                } else {
                    System.out.println("DESTINATION ERROR: " + destinationMsisdn);
                }

            } else if ((destinationMsisdn.length() == 11)
                    && (destinationMsisdn.startsWith("0"))) {
                if (Pattern.matches("[0-9]*", destinationMsisdn));
                formattedDest = destinationMsisdn.replaceFirst("0", "+234");
            } else {
                System.out.println("DESTINATION ERROR: " + destinationMsisdn);
            }
        } catch (NullPointerException e) {
            System.out.println(KannelS.class.getName() + e);
            System.out.println("DESTINATION ERROR: " + destinationMsisdn);
        }
        return formattedDest;
    }

 
}

