/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.strobo.bm.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.strobo.bm.dao.UserDao;
import ru.strobo.bm.data.User;

/**
 *
 * @author k.baukov
 */
@Controller
public class HomeController {
    
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(HomeController.class);
    
    @Autowired
    UserDao uDao;
       
    @RequestMapping("/home")
    public String home(Model model, HttpServletRequest  request, HttpServletResponse  response) {
        
        HttpSession sess =  request.getSession();
        Object Auth = sess.getAttribute("Auth");
        User user = (User) sess.getAttribute("User");
        
        Logger.info("Incomming http request in home.");
        
        if( Auth == null || user == null) {
            Logger.info("Redirect to login.");
            response.setHeader("Location", "/home/login");
            response.setStatus(302);
            return "login";
        } else {
            model.addAttribute("uName", "{login:'" + user.getLogin() + "', userType:'" + user.getUserType() + "'}");
            return "main";
        }
    }
    
    @RequestMapping("/home/login")
    public String login(Model model, HttpServletRequest  request, HttpServletResponse  response) {
        HttpSession sess =  request.getSession();
        Object Auth = sess.getAttribute("Auth");
        String errorMess = ""; String uName = "";
        String sessId = "";
        
        Logger.info("Incomming http request in login.");
        
        if( request.getParameter("username") != null ) {
            String userName =  request.getParameter("username");
            String userPass =  request.getParameter("password");

            if( userName == "" || userPass == "" ) {
                errorMess = "Пароль и Имя пользователя не могут быть пустыми.";
            } else {
                
                User user = uDao.auth(userName, userPass);
                if(user!=null)
                    sessId = user.getSessionId();
                else 
                    errorMess = "В доступе отказано.";
                
                if( sessId !=null && !sessId.isEmpty() ) {
                    Auth = "true";
                    sess.setAttribute("Auth", Auth);
                    sess.setAttribute("User", user);
                    sess.setAttribute("SessID", sessId);
                    uName = user.getLogin();
                } else {
                    errorMess = "В доступе отказано.";
                }
            }
        }
        
        if( Auth == null ) {
            model.addAttribute("errorMsg", errorMess);
            return "login";
        } else {
            Logger.info("Redirect to home.");
            response.setHeader("Location", "/home");
            response.setStatus(302);
            return "main";
        }

    }
    
    @RequestMapping("/home/logout")
    public String logout(Model model, HttpServletRequest  request, HttpServletResponse  response) {
        HttpSession sess =  request.getSession();
        sess.removeAttribute("Auth");
        sess.removeAttribute("User");
        sess.removeAttribute("SessID");
        response.setHeader("Location", "/home/login");
        response.setStatus(302);
        return "login";
    }
   
}
