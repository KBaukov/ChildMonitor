/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.strobo.bm.data;

import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author k.baukov
 */
public class User {
    
    private Integer id;
    private String userType;
    private String login;
    private String pass;
    private String activeFlag;
    private String sessionId;
    private Timestamp lastVisit;

    public User(Integer id, String userType, String login, String pass, String activeFlag, Timestamp lastVisit) {
        this.id = id;
        this.userType = userType;
        this.login = login;
        this.pass = pass;
        this.activeFlag = activeFlag;
        this.lastVisit = lastVisit;
    }

    public User(String userType, String login, String pass) {
        this.userType = userType;
        this.login = login;
        this.pass = pass;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(String activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Timestamp getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(Timestamp lastVisit) {
        this.lastVisit = lastVisit;
    }
    
    public String toJson() {
        return "{"
               + "id:" + id 
               + ",user_type:\"" + userType + "\""
               + ",login:\"" + login  + "\""
               + ",active_flag:\"" + activeFlag  + "\""
               + ",last_visit:\"" + lastVisit.toString()  + "\""
        + "}"; 
    }
    
}
