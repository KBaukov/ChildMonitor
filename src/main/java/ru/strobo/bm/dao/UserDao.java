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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.codec.digest.DigestUtils;
import ru.strobo.bm.config.AppDataSource;
import ru.strobo.bm.data.User;

/**
 *
 * @author k.baukov
 */
@Component
public class UserDao {
    
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(UserDao.class);
    
    public static final String GET_USERS = "SELECT * FROM USERS;";
    public static final String GET_USER = "SELECT * FROM USERS WHERE login=? AND pass=?;";
    public static final String CHECK_SESS_BY_USER = "SELECT * FROM SESSIONS WHERE user_id=?;";
    public static final String CHECK_SESS_BY_ID = "SELECT * FROM SESSIONS WHERE session_id=?;";
    public static final String DEL_SESS_BY_USER = "DELETE FROM SESSIONS WHERE user_id=?;";
    public static final String DEL_SESS_BY_ID = "DELETE FROM SESSIONS WHERE session_id=?;";
    public static final String CREATE_SESS = "INSERT INTO SESSIONS (session_id, user_id, exp_date) VALUES (?,?,?);";
    public static final String UPDATE_SESS = "UPDATE SESSIONS  SET exp_date=? WHERE session_id=?;";
    public static final String UPDATE_USER_LAST_VISIT = "UPDATE USERS  SET last_visit=? WHERE id=?;";
//    public static final String GET_USERS = "SELECT * FROM \"PUBLIC\".USERS;";
//    public static final String GET_USER = "SELECT * FROM \"PUBLIC\".USERS WHERE login=? AND pass=?;";
//    public static final String CHECK_SESS_BY_USER = "SELECT * FROM \"PUBLIC\".SESSIONS WHERE user_id=?;";
//    public static final String CHECK_SESS_BY_ID = "SELECT * FROM \"PUBLIC\".SESSIONS WHERE session_id=?;";
//    public static final String DEL_SESS_BY_USER = "DELETE FROM \"PUBLIC\".SESSIONS WHERE user_id=?;";
//    public static final String DEL_SESS_BY_ID = "DELETE FROM \"PUBLIC\".SESSIONS WHERE session_id=?;";
//    public static final String CREATE_SESS = "INSERT INTO \"PUBLIC\".SESSIONS (session_id, user_id, exp_date) VALUES (?,?,?);";
//    public static final String UPDATE_SESS = "UPDATE \"PUBLIC\".SESSIONS  SET exp_date=? WHERE session_id=?;";
//    public static final String UPDATE_USER_LAST_VISIT = "UPDATE \"PUBLIC\".USERS  SET last_visit=? WHERE id=?;";
    
    
    @Autowired
    AppDataSource ds;
    
    public List<User> getUsers() {
        
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        List<User> list = new ArrayList<User>();
        
        try {
            conn = ds.getConnection();
 
            statement = conn.prepareStatement(GET_USERS);
            
            rs = statement.executeQuery();
            
            while(rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("user_type"),
                        rs.getString("login"),
                        rs.getString("pass"),
                        rs.getString("active_flag"),
                        rs.getTimestamp("last_visit")
                );
                
                list.add(user);
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
    
    public User auth(String login, String pass) {
        
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        User user = null;
        String sessionId = "";
        
        try {
            conn = ds.getConnection();
 
            statement = conn.prepareStatement(GET_USER);            
            statement.setString(1, login.toLowerCase());
            statement.setString(2, pass);
            
            rs = statement.executeQuery();
            
            if(rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("user_type"),
                        rs.getString("login"),
                        rs.getString("pass"),
                        rs.getString("active_flag"),
                        rs.getTimestamp("last_visit")
                );
            }
            
            if(user!=null) {
                sessionId = createSession(conn, user.getId(), user.getLogin());
                user.setSessionId(sessionId);
                updateLastUserVisit(conn, user.getId());
            }
            
            conn.commit();
           
        } catch (SQLException ex) {
            try {
                Logger.error("Error while auth user: "+ex.getMessage());
                conn.rollback();
            } catch (SQLException ex1) {
                Logger.error("Error while rollback: "+ex.getMessage());
            }
        } finally {
            try {
                if(rs!=null) rs.close();
                if(statement!=null) statement.close();
                if(conn!=null) conn.close();
            } catch (SQLException ex) {
                Logger.error("Error while connection resource release: "+ex.getMessage());
            }
        }
        
        return user;
    }
    
    public boolean checkSession(String sessionId) {
        
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        boolean sessStatus = false;
        
        User user = null; Timestamp expDate;
        
        try {
            conn = ds.getConnection();
 
            statement = conn.prepareStatement(CHECK_SESS_BY_ID);
            statement.setString(1, sessionId);
            
            rs = statement.executeQuery();
            
            if(rs.next()) {
                expDate = rs.getTimestamp("exp_date");
                if( new Date().getTime() >= expDate.getTime()) {
                    //delete sessObj
                    deleteSessionByID(conn, sessionId);
                    sessStatus = false;
                } else {
                    //update exp_date in  sessObj
                    updateSession(conn, sessionId);
                    sessStatus = true;
                }
            } else {
                sessStatus = false;
            }

            conn.commit();
            
        } catch (SQLException ex) {
             try {
                Logger.error("Error while check user session: "+ex.getMessage());
                conn.rollback();
            } catch (SQLException ex1) {
                Logger.error("Error while rollback: "+ex.getMessage());
            }
        } finally {
            try {
                if(rs!=null) rs.close();
                if(statement!=null) statement.close();
                if(conn!=null) conn.close();
            } catch (SQLException ex) {
                Logger.error("Error while connection resource release: "+ex.getMessage());
            }
        }
        
        return sessStatus;

    }
    
    public String createSession(Connection conn, int userId, String userName) {

        PreparedStatement statement = null;
        
        deleteSessionByUser(conn, userId);

        Date cDate = new Date();
        String sessionId = sha256(userName, cDate);        
        Timestamp expDate = new Timestamp(cDate.getTime() + 600000L);
        
        try {
 
            statement = conn.prepareStatement(CREATE_SESS);            
            statement.setString(1, sessionId);
            statement.setInt(2, userId);
            statement.setTimestamp(3, expDate);
            
            int res = statement.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.error("Error while create user session: "+ex.getMessage());
        } finally {
            try {
                if(statement!=null) statement.close();
            } catch (SQLException ex) {
                Logger.error("Error while connection resource release: "+ex.getMessage());
            }
        }
        
        return sessionId;
    }
    
    public void deleteSessionByUser(Connection conn, int userId) {

        PreparedStatement statement = null;
        
        try {
 
            statement = conn.prepareStatement(DEL_SESS_BY_USER);            
            statement.setInt(1, userId);
            int res = statement.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.error("Error while delete user session: "+ex.getMessage());
        } finally {
            try {
                if(statement!=null) statement.close();
            } catch (SQLException ex) {
                Logger.error("Error while connection resource release: "+ex.getMessage());
            }
        }

    }
    
    public void deleteSessionByID(Connection conn, String sessId) {

        PreparedStatement statement = null;
        
        try {
 
            statement = conn.prepareStatement(DEL_SESS_BY_ID);            
            statement.setString(1, sessId);
            int res = statement.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.error("Error while delete user session: "+ex.getMessage());
        } finally {
            try {
                if(statement!=null) statement.close();
            } catch (SQLException ex) {
                Logger.error("Error while connection resource release: "+ex.getMessage());
            }
        }

    }
    
    public void updateSession(Connection conn, String sessId) {

        PreparedStatement statement = null;
        Date cDate = new Date();       
        Timestamp expDate = new Timestamp(cDate.getTime() + 600000L);
        
        try {
 
            statement = conn.prepareStatement(UPDATE_SESS);            
            statement.setTimestamp(1, expDate);
            statement.setString(2, sessId);
            int res = statement.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.error("Error while get user: "+ex.getMessage());
        } finally {
            try {
                if(statement!=null) statement.close();
            } catch (SQLException ex) {
                Logger.error("Error while connection resource release: "+ex.getMessage());
            }
        }

    }
    
    public void updateLastUserVisit(Connection conn, int uId) {

        PreparedStatement statement = null;
        Date cDate = new Date();       
        Timestamp tcDate = new Timestamp(cDate.getTime());
        
        try {
 
            statement = conn.prepareStatement(UPDATE_USER_LAST_VISIT);            
            statement.setTimestamp(1, tcDate);
            statement.setInt(2, uId);
            int res = statement.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.error("Error while get user: "+ex.getMessage());
        } finally {
            try {
                if(statement!=null) statement.close();
            } catch (SQLException ex) {
                Logger.error("Error while connection resource release: "+ex.getMessage());
            }
        }

    }
    
    
    private String sha256(String uName, Date dd) {
        String hash = DigestUtils.sha256Hex( uName + "_" + dd.toString());
        return hash;
    }
    
}
