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

@ManagedBean
@SessionScoped
public class Login implements Serializable {

	private static final long serialVersionUID = 1094801825228386363L;
	
	private String pwd;
	private String msg;
	private String user;

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

	//validate login
	public String validateUsernamePassword() 
        {
            try
            {
		int valid = LoginDAO.validate(user, pwd);
		if (valid == 2) {
                    HttpSession session = SessionUtils.getSession();
                    session.setAttribute("username", user);
                    return "admin";
		}
                else if (valid == 1) {
                    HttpSession session = SessionUtils.getSession();
                    session.setAttribute("username", user);
                    return "shop";
                }
                else {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Incorrect Username and Password ",
							"Please enter correct username and Password"));
			return "login";
		}
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
	}
        
        public String updateProfile(){
            LoginDAO.changeProfile(this);
            
            return "";
        }

	//logout event, invalidate session
	public String logout() {
		HttpSession session = SessionUtils.getSession();
		session.invalidate();
		return "login";
	}
}
