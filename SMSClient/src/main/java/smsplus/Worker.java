package smsplus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Message Statuses ================
 *
 * T - Pending Q - Queued S - Sent E - Communication Error F - Data incorrect
 * errror (Fail)
 *
 * @author bogeyingbo
 */
public class Worker extends Thread implements DBConnectInterface {

    private Connection conn = null;

    private static final int messagCount = 0;
    private static boolean errorMet = false;
    private LinkedBlockingQueue bQueue;
    private DbAccess dbAccess;
    private static List<String> deniedlist = Arrays.asList(new String[]{"UK NOKIA", "Vodka", "NOKIA", "NOKIA-UK", "GLO", "MTN", "ETISALAT", "AIRTEL", "Nokia", "Nokia-UK", "Glo", "Mtn", "Etisalat", "Airtel"});

    public Worker(LinkedBlockingQueue<Message> messageQueue) {
        this.bQueue = messageQueue;
        //  this.dbAccess = new DbAccess();
    }

    public void retrieveConnection() {
        this.conn = null;
        boolean UNDONE = true;
        while (UNDONE) {
            try {

                try {
                    Thread.sleep(50);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Class.forName(classForName).newInstance();
                this.conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);

                errorMet = false;
                UNDONE = false;
//                System.out.println("Connection with remote database established.");
            } catch (Exception ex) {
                errorMet = true;
            }
        }
    }

    public void refreshConnection() {

        boolean UNDONE = true;
        while (UNDONE) {
            try {

                try {
                    Thread.sleep(50);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                System.out.println("Attempting to refresh connection. ");

                Class.forName(classForName).newInstance();
                this.conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);

                errorMet = false;
                UNDONE = false;
                System.out.println("Connection with remote database refreshed.");
            } catch (Exception ex) {
                errorMet = true;
            }
        }
    }

    @Override
    public void run() {

        while (true) {

            Message msg = null;
            try {
                msg = (Message) this.bQueue.take();

                System.out.println("Message Status: " + msg.getStatusId());

                if (msg.getErrorFlag() == false) {
                    msg.setDestAddress(msg.getDestAddress().trim());

                    System.out.println("formatting dest : " + msg.getDestAddress());
                    String dest;
                    dest = KannelS.formatDestination(msg.getDestAddress().trim());
                    System.out.println("formatted dest : " + dest);
                    //dest = msg.getDestAddress();

                    if (dest.equalsIgnoreCase("invalid")) {

                        msg.setErrorFlag(true);
                        System.out.println(" | Wrong  Phone Number " + msg.getDestAddress() + ",set StatusId to Failed for " + msg.getId());
                        int upd_result = this.updateRecord(msg, "F");

                        continue;
                    }
                    msg.setDestAddress(dest);

                    int sendstatus = 0;
                    boolean isHeaderAllowed = true;

                    //        if (deniedlist.contains(msg.getSourceAddress())) {
                    //          isHeaderAllowed = false;
                    //        }
                    System.out.println("Header allowed " + isHeaderAllowed);
                    if (isHeaderAllowed) {

                        sendstatus = KannelS.sendSms(msg);
                        /**
                         * *
                         * if(msg.getErrorFlag() == false){ sendstatus =
                         * KannelS.sendSms(msg); }
                        **
                         */

                    } else {
                        sendstatus = 300;
                    }

                    System.out.println("Send result: " + sendstatus);
                    System.out.println("SEND RESULT " + sendstatus);

                    /**
                     * synchronized (Worker.class) {
                     *
                     * System.out.println("Sending Message :" + messagCount);
                     * messagCount = messagCount + 1;
                     *
                     * }
                     ***
                     */
                    if ((sendstatus == 200) || (sendstatus == 202)) {
                        int mov_result = this.updateRecord(msg, "S");
                        System.out.println("Sent updating");
                    } else if (sendstatus == 300) {

                        System.out.println("Invalid header, failing message " + msg.getSourceAddress());
                        int upd_res = this.updateRecord(msg, "F");
                        System.out.println(" message  failed " + msg.getDestAddress());

                    } else {
                        System.out.println(" message not sent Reversing Status back to pending status");

                        int upd_res = this.updateRecord(msg, "E");

                    }

                } else {

                    System.out.println(" message  failed " + msg.getDestAddress());
                    int upd_res = this.updateRecord(msg, "F");

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                if (msg != null) {
                    int upd_res = this.updateRecord(msg, "E");
                }

            }

            msg = null;
        }
    }

    private int updateRecord(Message msg, String i) {
        boolean record_updated = false;
        int result = -2;
        PreparedStatement ps = null;

        while (record_updated == false) {

            try {
                // checkConnection();
                // this.conn.setAutoCommit(false);
                String sql = "UPDATE  SMSQueue_dml  SET Status = ?,  TryCount  =  TryCount + 1   WHERE msgID = ? ";
                ps = this.conn.prepareStatement(sql);
                ps.setString(1, i);
                ps.setLong(2, msg.getId());

                ps.setQueryTimeout(5);
                result = ps.executeUpdate();

                result = 1;
                record_updated = true;
                System.out.println(ps.toString());
            } catch (Exception cE) {
                errorMet = true;
                record_updated = false;
                System.out.println("Communication error to remote database encountered. ");
                refreshConnection();
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

        return result;

    }

}

/* Location:           C:\Users\akin\Desktop\MessageScheduler1.36.jar
 * Qualified Name:     cyclops.Worker
 * JD-Core Version:    0.6.2
 */
