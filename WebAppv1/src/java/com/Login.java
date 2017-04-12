/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.database.LoginDAO;
import com.SessionUtils;


/**
 * Class used to store login credentials of either the customer or the admin
 * @author tom
 */
@ManagedBean
@SessionScoped
public class Login implements Serializable { 
    /**
     * The serialization runtime associates with each serializable class a version number, called a serialVersionUID,
     * which is used during deserialization to verify that the sender and receiver of a serialized object have loaded classes
     * for that object that are compatible with respect to serialization. If the receiver has loaded a class for 
     * the object that has a different serialVersionUID than that of the corresponding sender's class, then 
     * deserialization will result in an InvalidClassException
     */
    private static final long serialVersionUID = 1094801825228386363L;//serialization runtime
    private String pwd; //field for password
    private String msg; //field for user message
    private String user; //field for username
    private boolean isAdmin = false; //field for setting adminstrative priviledges 
    
    /**
     * Validates the username and password
     * @return the page to be redirected to, admin, shop or login.xtml
     */
    public String validateUsernamePassword() 
    {
        try
        {
            int valid = LoginDAO.validate(user, pwd); //LoginDAO.validate returns an error code depending on name and pw
            
            if (valid == 2) //the logged in user is an admin
            {
                HttpSession session = SessionUtils.getSession();//get the http session from sessionUtils           
                session.setAttribute("username", user);//AuthorizationFilter checks -> if ((ses != null && ses.getAttribute("username") != null)
                isAdmin = true;//set admin privileges
                return "admin";//redirect user to admin.xtml
            }
            else if (valid == 1) //the logged in user is a customer
            {
                HttpSession session = SessionUtils.getSession();//get the http session from sessionUtils
                session.setAttribute("username", user);//AuthorizationFilter checks -> if ((ses != null && ses.getAttribute("username") != null)
                return "shop";//redirect user to shop.xtml
            }
            else 
            {
                //notify user of incorrect login attempt
                FacesContext.getCurrentInstance().addMessage(
                                null,
                                new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                "Incorrect Username and Password ",
                                                "Please enter correct username and Password"));
                return "login";//redirect user tologin.xtml
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String updateProfile(){
        LoginDAO.changeProfile(this); //pass this login bean to the update profile method found in LoginDAO
        return "";
    }

    //logout event, invalidate session
    public String logout() {
            HttpSession session = SessionUtils.getSession(); //get the http session from sessionUtils
            session.invalidate(); //invalidate the seesion now that the user has logged out
            return "login";
    }

    /** Getters and setters */
    public String getPwd() {
            return pwd;
    }

    public void setPwd(String pwd) {
            this.pwd = pwd;
    }

    public String getMsg() {
            return msg;
    }

    public void setMsg(String msg) {
            this.msg = msg;
    }

    public String getUser() {
            return user;
    }

    public void setUser(String user) {
            this.user = user;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }
               
}
