/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.strobo.bm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.strobo.bm.config.AppDataSource;
import ru.strobo.bm.data.Device;

/**
 *
 * @author k.baukov
 */
@Component
public class DeviceDao {
    
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DeviceDao.class);
    
    private Map<Integer, Device> devMap = new HashMap<Integer, Device>();
    
    public static final String GET_DEVICES = "SELECT * FROM DEVICES;";
    //public static final String GET_USER = "SELECT * FROM USERS WHERE login=? AND pass=?;";
//    public static final String GET_DEVICES = "SELECT * FROM \"PUBLIC\".DEVICES;";
//    //public static final String GET_USER = "SELECT * FROM \"PUBLIC\".USERS WHERE login=? AND pass=?;";
    
    @Autowired
    AppDataSource ds;
    
    @PostConstruct
    private void init() {
        getDevices();
        for (Map.Entry<Integer, Device> d : devMap.entrySet()) {
            Logger.info("deviceId: "+d.getKey());
            Logger.info("deviceName: "+d.getValue().getName());
            Logger.info("----------------------------------");
        }
    }
    
    public List<Device> getDevices() {
        
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        devMap.clear();
        
        List<Device> list = new ArrayList<Device>();
        
        try {
            conn = ds.getConnection();
 
            statement = conn.prepareStatement(GET_DEVICES);
            
            rs = statement.executeQuery();
            
            while(rs.next()) {
                Device device = new Device(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("name"),
                        rs.getString("active_flag"),
                        rs.getString("description")
                );
                
                list.add(device);
                devMap.put(device.getId(), device);
            }
           
        } catch (SQLException ex) {
            Logger.error("Error while get users: "+ex.getMessage());
        } finally {
            try {
                if(rs!=null) rs.close();
                if(statement!=null) statement.close();
                if(conn!=null) conn.close();
            } catch (SQLException ex) {
                Logger.error("Error while connection resource release: "+ex.getMessage());
            }
        }
        
        return list;
    }
    
    public Device getDeviceByName(String name) {
        
        Device dev = null;
        
        for (Map.Entry<Integer, Device> d : devMap.entrySet()) {
            if(name.equals( d.getValue().getName()) ) {
                dev = d.getValue();
            }
        }
        
        return dev;
    }
    public Device getDeviceById(Integer id) {
        
        Device dev = null;
        
        for (Map.Entry<Integer, Device> d : devMap.entrySet()) {
            if(id == d.getValue().getId() ) {
                dev = d.getValue();
            }
        }
        
        return dev;
    }
    
    public int getMapCount() {
        return devMap.size();
    }
}
