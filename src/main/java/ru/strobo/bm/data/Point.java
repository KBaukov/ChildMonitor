/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.strobo.bm.data;

import java.util.Date;


/**
 *
 * @author k.baukov
 */
public class Point {
    
    private Date timestamp;
    private String point;

    public Point() {
    }
    
    public Point(Date timestamp, String point) {
        this.timestamp = timestamp;
        this.point = point;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
    
    public String toJson() {
        return "{"
               + "timestamp:\"" + timestamp + "\""
               + ",point:\"" + point + "\""
        + "}"; 
    }

    
}
