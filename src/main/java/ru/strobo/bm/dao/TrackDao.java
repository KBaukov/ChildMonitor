/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.strobo.bm.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.strobo.bm.config.AppDataSource;
import ru.strobo.bm.data.Device;
import ru.strobo.bm.data.Point;
import ru.strobo.bm.data.TrackData;

/**
 *
 * @author k.baukov
 */
@Component
public class TrackDao {
    
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DeviceDao.class);
    
    public static final String GET_TRACK = "SELECT * FROM POINTS WHERE device_id = ?;";
    public static final String SET_TRACK_POINT = "INSERT INTO POINTS (id, device_id, timestamp, point) VALUES (?,?,?,?);";
    public static final String GET_LAST_TR_ID = "SELECT id FROM POINTS ORDER BY ID DESC LIMIT 1;";
    
//    public static final String GET_TRACK = "SELECT * FROM \"PUBLIC\".POINTS WHERE device_id = ?;";
//    public static final String SET_TRACK_POINT = "INSERT INTO \"PUBLIC\".POINTS (id, device_id, timestamp, point) VALUES (?,?,?,?);";
//    public static final String GET_LAST_TR_ID = "SELECT id FROM \"PUBLIC\".POINTS ORDER BY ID DESC LIMIT 1;";
//    
    
    @Autowired
    AppDataSource ds;
    
    @Autowired
    DeviceDao dDao;
    
    
    public List<Point> getTrackByDevId(Integer devId) {
        
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        List<Point> track = new ArrayList<Point>();
        
        try {
            conn = ds.getConnection();
 
            statement = conn.prepareStatement(GET_TRACK);
            statement.setInt(1, devId);
            
            rs = statement.executeQuery();
            
            while(rs.next()) {
                Point point = new Point(
                        rs.getDate("timestamp"),
                        rs.getString("point")
                );
                
                track.add(point);
            }
           
        } catch (SQLException ex) {
            Logger.error("Error while get track: "+ex.getMessage());
        } finally {
            try {
                if(rs!=null) rs.close();
                if(statement!=null) statement.close();
                if(conn!=null) conn.close();
            } catch (SQLException ex) {
                Logger.error("Error while connection resource release: "+ex.getMessage());
            }
        }
        
        return track;
    }
    
    public void setTrackPointByDevId(TrackData data) throws Exception {
        
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        Long lastId = null;
        
        
        Device dev = dDao.getDeviceByName(data.getDeviceId());
        
        if(dev==null) {
            throw new Exception("Device not found: " + data.getDeviceId());
        }
        
        Integer devId = dDao.getDeviceByName(data.getDeviceId()).getId();
        
        try {
            conn = ds.getConnection();
            
            statement = conn.prepareStatement(GET_LAST_TR_ID);
            rs = statement.executeQuery();
            while(rs.next()) {
                lastId = rs.getLong("id");
            }
            
            for(Point p: data.getPoints()) {
                statement = conn.prepareStatement(SET_TRACK_POINT);
                statement.setLong(1, ++lastId);
                statement.setInt(2, devId);
                statement.setTimestamp(3, new Timestamp(p.getTimestamp().getTime()));
                statement.setString(4, p.getPoint());

                statement.execute();
            }
           
            conn.commit();
           
        } catch (SQLException ex) {
            Logger.error("Error while set track: "+ex.getMessage());
        } finally {
            try {
                if(rs!=null) rs.close();
                if(statement!=null) statement.close();
                if(conn!=null) conn.close();
            } catch (SQLException ex) {
                Logger.error("Error while connection resource release: "+ex.getMessage());
            }
        }

    }
}
