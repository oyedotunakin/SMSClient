package smsplus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageRetryPoller extends Thread implements DBConnectInterface {

    private long control = 0L;
    private DbAccess dbAccess;
    // final int POLLSIZE = 1000;
    final int POLLSIZE = 200;
    private Connection conn = null;
    private static boolean errorMet = false;
    private LinkedBlockingQueue<Message> messageQueueRetry;

    public MessageRetryPoller(LinkedBlockingQueue<Message> messageQ) {
        this.messageQueueRetry = messageQ;
        this.refreshConnection();
        //  this.dbAccess = new DbAccess();
    }

    private void refreshConnection() {

        boolean UNDONE = true;
        while (UNDONE) {
            try {

                try {
                    Thread.sleep(50);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                 //System.out.println("Attempting to refresh connection. ");
                Class.forName(classForName).newInstance();
                this.conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);

                errorMet = false;
                UNDONE = false;
                //System.out.println("Connection with remote database refreshed.");
            } catch (Exception ex) {
                errorMet = true;
            }
        }
    }

    public void run() {
        //System.out.println("Application starting...");

        int rel_res = this.updatePreviousQs("E", "T");
        doPolling();
    }

    private void doPolling() {
        while (true) {

            LinkedList messages = this.fetchMessages(RETRY_POLLSIZE, this.control);
           //System.out.println("Fetched " + messages.size() + " pending messages into sms queue");

            if (messages.size() < 1) {
                try {
                    Thread.sleep(RETRY_SLEEP_MILL_TIME);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            Iterator itr = messages.iterator();
            while (itr.hasNext()) {
                Message msg = (Message) itr.next();
                try {
                    int res = this.setAsQueued(msg, "Q");
                    if (res > 0) {
                        this.messageQueueRetry.put(msg);
                    }
                    //System.out.println("Queue size before inserting " + this.messageQueueRetry.size());
                    //System.out.println("Message with destination addr: " + msg.getDestAddress() + " buffered in the queue");
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(MessageRetryPoller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    private int setAsQueued(Message msg, String i) {
        boolean record_updated = false;
        int result = -2;
        PreparedStatement ps = null;

        while (record_updated == false) {

            try {
                // checkConnection();
                // this.conn.setAutoCommit(false);
                String sql = "UPDATE  SMSQueue_dml  SET  Status = ?    WHERE msgID = ? ";
                ps = this.conn.prepareStatement(sql);
                ps.setString(1, i);
                ps.setLong(2, msg.getId());

                ps.setQueryTimeout(5);
                result = ps.executeUpdate();

                result = 1;
                record_updated = true;
                //System.out.println(ps.toString());
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

    private synchronized LinkedList fetchMessages(int POLLSIZE, long control) {
        boolean record_updated = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        LinkedList rec = new LinkedList();

       // Connection con = getConnection();
        while (record_updated == false) {
            try {

                //   String GETMESSAGES = "SELECT   *  FROM  SMSQueue_dml   WHERE   (Status = 'P')    LIMIT " + POLLSIZE + " ";
                String GETMESSAGES = "SELECT  TOP " + POLLSIZE + " * FROM  SMSQueue_dml   WHERE      "
                        + "  (Status  =  'E')  OR  ((Status  =  'F')   and  (TryCount < 3))  OR  "
                        + "  ((Status  =  'U')   AND   (TryCount < 3)) ";

                ps = conn.prepareStatement(GETMESSAGES);
                //System.out.println(ps.toString());
                rs = ps.executeQuery();

                while (rs.next()) {
                    //System.out.println("Record found");
                    Message msg = new Message();
                    msg.setId(rs.getLong("msgID"));
                    msg.setMessageContent(rs.getString("Message"));
                    msg.setDestAddress(rs.getString("mobileID"));
                    msg.setSourceAddress(rs.getString("SenderID"));
                    msg.setStatusId(rs.getString("Status"));

                    rec.add(msg);
                }

                //System.out.println("Retry messages retrieved = "+rec.size());
                record_updated = true;

            } catch (Exception cE) {
                errorMet = true;
                record_updated = false;
                System.out.println("Communication error to remote database encountered. ");
                refreshConnection();
                //  cE.printStackTrace();
                System.out.println(cE.getMessage());
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                        rs = null;
                    }
                    if (ps != null) {
                        ps.close();
                        ps = null;
                    }
                    /**
                     * if (con != null) { con.close(); con = null; }
                     * *
                     */
                } catch (Exception ex) {
                    // Log.l.infoLog.info("Error encountered when attempting to close prepared statement");
                    ex.printStackTrace();
                }
            }
        }
        return rec;
    }

    private int updatePreviousQs(String qd, String pending) {
        boolean record_updated = false;
        PreparedStatement ps = null;
        int result = -2;

        while (record_updated == false) {
            try {

                String sql = "UPDATE SMSQueue_dml    SET Status = ? WHERE Status = ?";
                ps = this.conn.prepareStatement(sql);
                ps.setString(1, pending);
                ps.setString(2, qd);

                result = ps.executeUpdate();
                result = 1;

                record_updated = true;

                //System.out.println(ps.toString());
            } catch (Exception cE) {
                errorMet = true;
                record_updated = false;
                System.out.println("Communication error to remote database encountered. ");
                refreshConnection();
                // cE.printStackTrace();
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
 * Qualified Name:     cyclops.MessagePoller
 * JD-Core Version:    0.6.2
 */
