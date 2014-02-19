/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import Logger.FileLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author matej_000
 */
public class DBTrackCreator {
    
    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private String system = System.getProperty("os.name");

    public DBTrackCreator() throws Exception {
        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
      connect = DriverManager
          .getConnection("jdbc:mysql://localhost:3306/GPSWebApp?useUnicode=true&characterEncoding=UTF-8","root","Www4dm1n#");
        } catch (Exception e) {
            FileLogger.getInstance().createNewLog("ERROR: Cannot connect to database in DBTrackCreator!!!");
            throw e;
        }
    }
    
    public void createNewTrack(String trackName, String trackDescr, String trackActivity, String trackPath, int userID, String startDate, String endDate, String access, String startAddress, String endAddress, String length, String minElevation, String maxElevation, String heightDiff, String duration){
        try {
            statement =  connect.createStatement();
            //statement.executeQuery();
            
            //System.out.println("asdasdasddaadasada" + access);
            
            //System.out.println(trackActivity);
            Locale localeObject=new Locale("en"); 
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",localeObject);
      
            String modifiedDate = df.format(date);
  
            String stat = "INSERT INTO TRACKS (TRACK_NAME, TRACK_DESCRIPTION, TRACK_ACTIVITY, TRACK_FILE, TRACK_USER_ID, TRACK_STARTDATE, TRACK_ENDDATE, TRACK_ACCESS, TRACK_START_ADDRESS, TRACK_END_ADDRESS, TRACK_LENGTH_KM, TRACK_MIN_ELEVATION, TRACK_MAX_ELEVATION, TRACK_HEIGHT_DIFF, TRACK_DURATION, TRACK_DATE_CREATED) VALUES ('"+ trackName +"' , '"+ trackDescr + 
                                                                "' , '" + trackActivity + "' , '" + trackPath +"' ," + userID + ", '"+ startDate +"' , '"+ endDate + "', '"+access+"', '"+startAddress+"', '"+endAddress+"', '"+length+"', '"+minElevation+"', '"+maxElevation+"', '"+heightDiff+"', '"+duration+"', '"+ df.format(date) +"')";
            if (system.startsWith("Windows")) {
                stat = stat.replaceAll("\\\\", "/");
            }
            System.out.println(stat);
            statement.executeUpdate(stat);
            FileLogger.getInstance().createNewLog("Track " + trackName + " was successfuly created in DB. The userID is " + userID + " .");
            close();
        } catch (SQLException ex) {
            System.out.println("Nezapisal som do DB!!!");
            FileLogger.getInstance().createNewLog("ERROR: Cannot create track " + trackName + " !!! The user ID is " + userID + " !!!");
            close();
        }
    }
    
    private void close() {
    try {
      if (resultSet != null) {
        resultSet.close();
      }
      if (statement != null) {
        statement.close();
      }
      if (connect != null) {
        connect.close();
      }
    } catch (Exception e) {

    }
  }
}
