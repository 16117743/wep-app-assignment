/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.database.DataConnect;

public class LoginDAO {

	public static int validate(String user, String password) {
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataConnect.getConnection();
                            
			ps = con.prepareStatement("SELECT USERNAME, PW, ISADMIN FROM APP.USERS WHERE USERNAME = ? AND PW = ?");
                        //ps = con.prepareStatement("SELECT * FROM APP.USERS FETCH FIRST 100 ROWS ONLY");
			ps.setString(1, user);
			ps.setString(2, password);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) 
                        {
                            boolean admin = rs.getBoolean("ISADMIN");
                            String name = rs.getString("USERNAME");
                            String pwd = rs.getString("PW");
                            
                            System.out.println("is admin = " + admin);
                            System.out.println("username = " + name);
                            System.out.println("pwd = " + pwd);
                            if(admin == true)
				return 2;
                            else if (admin == false)
                                return 1;
			}
		} catch (SQLException ex) {
			System.out.println("Login error -->" + ex.getMessage());
			return -1;
		} finally {
			DataConnect.close(con);
		}
		return -1;
	}
}
