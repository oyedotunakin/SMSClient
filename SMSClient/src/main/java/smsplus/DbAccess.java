package smsplus;

//import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
//import cyclops.Log;
//import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Message Statuses ================
 *
 * T - Pending Q - Queued S - Sent E - Communication Error F - Data incorrect
 * errror (Fail)
 *
 * @author bogeyingbo
 */
public final class DbAccess {

    private Connection conn = null;
    private static boolean errorMet = false;

    public DbAccess() {
        retrieveConnection();
    }

    public final boolean getErrorStatus() {
        return errorMet;
    }

    public void retrieveConnection() {
        this.conn = null;
        boolean UNDONE = true;
        while (UNDONE) {
            try {

               // Class.forName("com.mysql.jdbc.Driver");
                // conn = DriverManager.getConnection("jdbc:mysql://192.168.0.98:3306/sms_app", "bogeyingbo", "adadad");
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
                this.conn = DriverManager.getConnection("jdbc:sqlserver://172.27.11.236:1433;databaseName=BroadCast", "smsusr", "smsusr");
              //    this.conn =  DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=BroadCast", "sa", "nimda");

//                System.out.println("Connection with local database established.");
                errorMet = false;
                UNDONE = false;
//                System.out.println("Connection with remote database established.");
            } catch (Exception ex) {
                errorMet = true;
                //java.util.logging.Logger.getLogger(DbAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void refreshConnection() {
        try {
            if (!this.conn.isClosed()) {
                this.conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            // Log.l.fatalLog.fatal(ex.toString());
            //java.util.logging.Logger.getLogger(DbAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean UNDONE = true;
        while (UNDONE) {
            try {
                System.out.println("Attempting to refresh connection. ");
              //  Class.forName("com.mysql.jdbc.Driver");
                //  conn = DriverManager.getConnection("jdbc:mysql://192.168.0.98:3306/sms_app", "bogeyingbo", "addaeq");

                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
                this.conn = DriverManager.getConnection("jdbc:sqlserver://172.27.11.236:1433;databaseName=BroadCast", "smsusr", "smsusr");
                  //   this.conn =  DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=BroadCast", "sa", "nimda");
//                / this.conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;instance=BOGEYINGBO-PC\\SQLEXPRESS;databaseName=sms_app", "sa", "nimda");

                // Class.forName("com.mysql.jdbc.Driver").newInstance();
                // this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bulksms", "messagescheduler", "c3lluL4ntSch3dul3R");
                errorMet = false;
                UNDONE = false;
                System.out.println("Connection with remote database refreshed.");
            } catch (Exception ex) {
                errorMet = true;
                // java.util.logging.Logger.getLogger(DbAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int updateSentRecord(Message msg, String i) {
        boolean record_updated = false;
        PreparedStatement ps = null;
        int result = -2;

        while (record_updated == false) {
            try {
                //  checkConnection();

                String sql = "UPDATE   SMSQueue_dml   SET smsStatus = ? WHERE   (smsID = ?)   AND   (datesent = GETDATE()) ";
                ps = this.conn.prepareStatement(sql);
                ps.setString(1, i);
                ps.setLong(2, msg.getId());

                ps.setQueryTimeout(5);
                result = ps.executeUpdate();

                record_updated = true;
                System.out.println(ps.toString());
            } catch (Exception cE) {
                errorMet = true;
                record_updated = false;
                System.out.println("Communication error to remote database encountered. ");
                refreshConnection();
                result = -1;
                // Log.l.infoLog.info("Error while updating record status");
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

    public int setAsQueued(Message msg, String i) {
        boolean record_updated = false;
        int result = -2;
        PreparedStatement ps = null;

        while (record_updated == false) {

            try {
                // checkConnection();
                // this.conn.setAutoCommit(false);
                String sql = "UPDATE  SMSQueue_dml  SET   Status = ?    WHERE   msgID = ?    ";
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

    public int updateRecord(Message msg, String i) {
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

    public LinkedList fetchMessages(int POLLSIZE, long control) {
        boolean record_updated = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        LinkedList rec = new LinkedList();

       // Connection con = getConnection();
        while (record_updated == false) {
            try {

                //  String GETMESSAGES = "SELECT   *  FROM  SMSQueue_dml   WHERE  Status = 'P'    LIMIT " + POLLSIZE + " ";
                String GETMESSAGES = "SELECT TOP " + POLLSIZE + " * FROM SMSQueue_dml   WHERE   (Status  =  'P')  or   "
                        + "  (Status  =  'E')  or  ((Status  =  'F')   and  (TryCount < 3))  or  ((Status  =  'U')   and  (TryCount < 3)) ";

                ps = conn.prepareStatement(GETMESSAGES);
                System.out.println(ps.toString());
                rs = ps.executeQuery();

                while (rs.next()) {
                    System.out.println("Record found");
                    Message msg = new Message();
                    msg.setId(rs.getLong("msgID"));
                    msg.setMessageContent(rs.getString("Message"));
                    msg.setDestAddress(rs.getString("mobileID"));
                    msg.setSourceAddress(rs.getString("SenderID"));
                    msg.setStatusId(rs.getString("Status"));

                    rec.add(msg);
                }

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

    public LinkedList<Smsschedules> checkSMSStatusUpdate() {
        PreparedStatement ps = null;
        ResultSet result = null;
        Smsschedules schedule = null;
        LinkedList rec = new LinkedList();
        try {
            // checkConnection();
            String SMSMSQUERY = "select TaskID,Status from smsschedules where Status in (7,4) AND deliveryTime <= now()";
            ps = this.conn.prepareStatement(SMSMSQUERY);
            result = ps.executeQuery();
            // Log.l.infoLog.info("QUERY " + ps.toString());
            if (result.next()) {
                schedule = new Smsschedules();
                schedule.setTaskId(result.getString("TaskID"));
                schedule.setStatus(result.getString("Status"));
                rec.add(schedule);
            }
            result.close();
        } catch (Exception ex) {
            errorMet = true;
            ex.printStackTrace();
            // Log.l.fatalLog.fatal("Error could not check status update");
            // Log.l.fatalLog.fatal(ex.toString());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (result != null) {
                    result.close();
                    result = null;
                }
            } catch (Exception ex) {
                // Log.l.infoLog.info("Error encountered when attempting to close prepared statement");
                ex.printStackTrace();
            }
        }

        return rec;
    }

    public int updatePreviousQs(String qd, String pending) {
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

                System.out.println(ps.toString());
            } catch (Exception cE) {
                errorMet = true;
                record_updated = false;
                System.out.println("Communication error to remote database encountered. ");
                refreshConnection();
                // cE.printStackTrace();
                result = -1;
                // System.out.println(cE.getMessage());

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

    public boolean isHeaderAllowed(String sourceAddress) {
        PreparedStatement ps = null;
        ResultSet result = null;
        boolean isAllowed = true;
        try {
            String SMSMSQUERY = "select header from blockedheaders where header = ? ";
            ps = this.conn.prepareStatement(SMSMSQUERY);
            ps.setString(1, sourceAddress);
            result = ps.executeQuery();

            if (result.next()) {
                System.out.println("header " + sourceAddress);
                isAllowed = false;
            }
            result.close();
        } catch (Exception ex) {
            errorMet = true;
            ex.printStackTrace();
            //Log.l.fatalLog.fatal(ex.toString());
            System.out.println(ex.getMessage());
        } finally {
        }

        return isAllowed;
    }

    private final void checkConnection() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean upate_ok = false;
       // Connection con = getConnection();

        while (upate_ok == false) {
            try {

                //  String GETMESSAGES = "SELECT   *  FROM  SMSQueue_dml_priority   WHERE  Status = 'P'    LIMIT " + POLLSIZE + " ";
                String GETMESSAGES = " SELECT  1 ";
                ps = conn.prepareStatement(GETMESSAGES);
                System.out.println(ps.toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    System.out.println("Record found");
                }

                //   errorConnect  =  false;
                upate_ok = true;

            } catch (Exception cE) {
                // errorConnect  =   true;
                upate_ok = false;
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
    }

}

/* Location:           C:\Users\akin\Desktop\MessageScheduler1.36.jar
 * Qualified Name:     database.DbAccess
 * JD-Core Version:    0.6.2
 */
