/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.strobo.bm.data;

import java.util.List;


/**
 *
 * @author k.baukov
 */
public class TrackData {
    private String type;
    private String deviceId;
    private List<Point> points;

    public TrackData() {
    }
    
    public TrackData(String type, String deviceId, List<Point> points) {
        this.type = type;
        this.deviceId = deviceId;
        this.points = points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
    
    public String toJson() {
        return "{"
               + "type:\"" + type + "\""
               + ", device_id:\"" + deviceId + "\""
               + ", points:\"" + points + "\""
        + "}"; 
    }

}
