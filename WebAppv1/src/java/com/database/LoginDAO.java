/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.database;

import com.Login;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.database.DataConnect;


/**
 * Class used to verify username and password
 * @author tom
 */
public class LoginDAO {

    /**
     * Function used to validate username and password
     * @param user username
     * @param password password
     * @return error code 2= admin, 1= customer, -1 incorrect login
     */
    public static int validate(String user, String password) 
    {
        Connection con = null;
        PreparedStatement ps = null;

        try {
                con = DataConnect.getConnection();//obtain connection to database

                //prepared statement to defend against A1: Injection
                ps = con.prepareStatement("SELECT USERNAME, PW, ISADMIN FROM APP.USERS WHERE USERNAME = ? AND PW = ?");
                ps.setString(1, user);//prepared statement to defend against A1: Injection
                ps.setString(2, password);//prepared statement to defend against A1: Injection

                ResultSet rs = ps.executeQuery();//store values from sql query in rs

                while (rs.next())//while rs has next value
                {
                    boolean admin = rs.getBoolean("ISADMIN");//get from field
                    String name = rs.getString("USERNAME");//get from field
                    String pwd = rs.getString("PW");//get from field

                    System.out.println("is admin = " + admin);
                    System.out.println("username = " + name);
                    System.out.println("pwd = " + pwd);
                    if(admin == true)
                        return 2;// return 2 if admin
                    else if (admin == false)
                        return 1;//else return 1 for customer
                }
        } catch (SQLException ex) {
                System.out.println("Login error -->" + ex.getMessage());
                return -1; //default return value for false login attempt
        } finally {
                DataConnect.close(con);//close connection to database
        }
        return -1;//default return value for false login attempt
    }
        
    public static void changeProfile(Login user)
    {
        Connection con = null;
        PreparedStatement ps = null;

        try 
        {
            con = DataConnect.getConnection();//obtain connection to database

            ps = con.prepareStatement("UPDATE APP.USERS SET MSG = ? WHERE USERNAME = ? AND PW = ?");//update msg field of user with associated name and pw

            ps.setString(1, user.getMsg());//set param for query
            ps.setString(2, user.getUser());//set param for query
            ps.setString(3, user.getPwd());//set param for query

            ps.execute();//execute query

        } catch (SQLException ex) {
                System.out.println("Login error -->" + ex.getMessage());

        } finally {
                DataConnect.close(con);//close connection to db
        }         
    }
}
