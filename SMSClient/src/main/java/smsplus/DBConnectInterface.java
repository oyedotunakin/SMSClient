/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smsplus;

/**
 *
 * @author bogeyingbo
 */
public interface DBConnectInterface {

    /**
     * * LIVE PARAMETERS ***
     */
    public final int POLLSIZE = 50;
    public final int RETRY_POLLSIZE = 20;
    public final String classForName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public final String dbUrl = "jdbc:sqlserver://172.27.11.236:1433;databaseName=BroadCast";
    public final String dbUserName = "dmlusr";
    public final String dbPassword = "dmlusr";
    public final long SLEEP_MILL_TIME = 10;
    public final long RETRY_SLEEP_MILL_TIME = 50;

    /**
     * * TEST PARAMETERS ON SQLSERVER public final String classForName =
     * "com.microsoft.sqlserver.jdbc.SQLServerDriver"; public final String dbUrl
     * = "jdbc:sqlserver://localhost:1433;databaseName=BroadCast"; public final
     * String dbUserName = "sa"; public final String dbPassword = "nimda"; *
     */
    /**
     * * TEST PARAMETERS ON MYSQL SERVER public final String classForName =
     * "com.mysql.jdbc.Driver"; public final String dbUrl =
     * "jdbc:mysql://192.168.0.98:3306/sms_app"; public final String dbUserName
     * = "sa"; public final String dbPassword = "nimda"; *
     */
}
