/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.strobo.bm.http;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.RequestParam;
import ru.strobo.bm.dao.DeviceDao;
import ru.strobo.bm.dao.TrackDao;
import ru.strobo.bm.dao.UserDao;
import ru.strobo.bm.data.Device;
import ru.strobo.bm.data.Point;
import ru.strobo.bm.data.User;
import ru.strobo.bm.ws.DeviceSessionsHandler;

/**
 *
 * @author k.baukov
 */
@RestController
@RequestMapping("/api")
public class ApiRestController {
    
    @Autowired
    UserDao uDao;
    
    @Autowired
    DeviceDao dDao;
    
    @Autowired
    TrackDao tDao;
    
    @Autowired
    DeviceSessionsHandler sh;
    
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ApiRestController.class);
    
    @RequestMapping(value = "/login", method = GET,  produces = "application/json;charset=UTF-8" )
    public String login(
            @RequestParam(value="login", required = true) String login,
            @RequestParam(value="pass",  required = true) String pass,
            HttpServletRequest   request
    ) {
        HttpSession sess =  request.getSession();
        Object Auth = sess.getAttribute("Auth");
        
        User user = uDao.auth(login, pass);
        if(user!=null) {
            String sessionId = user.getSessionId();
            Auth = "true";
            sess.setAttribute("Auth", Auth);
            sess.setAttribute("User", user);
            sess.setAttribute("SessID", sessionId);
            
            return "{success:true,sess:\"JSESSIONID=" + sess.getId() + "\"}";
        } else {
            return "{success:false,msg:\"Вдоступе отказано\"}";
        }
    }
    
    @RequestMapping(value = "/users", method = GET,  produces = "application/json;charset=UTF-8" )
    public String getUsers( ) {
        String data = "";        
        List<User> users = uDao.getUsers();
        
        for(User u : users)
            data += "," + u.toJson();
        
        return "{ success: true, data:[" + data.substring(1) + "]}";
    }
    
    @RequestMapping(value = "/devices", method = GET,  produces = "application/json;charset=UTF-8" )
    public String getDevices() {
        String data = "";
        List<Device> devices = dDao.getDevices();
        
        for(Device d : devices)
            data += "," + d.toJson();
        
        return "{ success: true, data:[" + (data.isEmpty() ? "" : data.substring(1) ) + "]}";
    }    
 
    @RequestMapping(value = "/gettrdata", method = GET,  produces = "application/json;charset=UTF-8" )
    public String getTrackByDevId(
            @RequestParam(value="deviceid", required = true) Integer deviceId
    ) {

        List<Point> trData = tDao.getTrackByDevId(deviceId);
        String data = "";
        
        for(Point p : trData) {
            data += ",[" + p.getPoint() +"]";
        }

        return "{success:true, data:[ " + (data.isEmpty() ? "" : data.substring(1) ) + "]}";
    }
   
    
}
