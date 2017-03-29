/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import com.database.DataConnect;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.sql.DataSource;

/**
 *
 * @author tom
 */
@ManagedBean(name="profileBean")
@RequestScoped
public class ProfileBean implements Serializable{

	//resource injection
	@Resource(name="jdbc/users")
	private DataSource ds;
        private DataConnect dc;
        private String idParam = "-1";
        private String nameParam = "-1";
        private Profile searchResult;
    

	//connect to DB and get customer list
	public List<Profile> getProfileList() throws SQLException{
		if(ds==null)
			throw new SQLException("Can't get data source");

		//get database connection
		Connection con = dc.getConnection();

		if(con==null)
			throw new SQLException("Can't get database connection");

		PreparedStatement ps
			= con.prepareStatement(
			   "select * from app.users where isadmin = ?");
                
                ps.setBoolean(1, false);

		//get customer data from database
		ResultSet result =  ps.executeQuery();

		List<Profile> list = new ArrayList<Profile>();

		while(result.next())
                {
                    Profile profile = new Profile();

                    profile.setId(result.getInt("ID"));
                    profile.setMsg(result.getString("MSG"));
                    profile.setName(result.getString("USERNAME"));

                    //store all data into a List
                    list.add(profile);
		}

		return list;
	}
        
        //connect to DB and get customer list
	public String searchByID() throws SQLException{
            System.out.println("TESTING  searchByName() ");

            if(idParam.equalsIgnoreCase("-1") == false)
            {
		if(ds==null)
			throw new SQLException("Can't get data source");

		//get database connection
		Connection con = dc.getConnection();

		if(con==null)
			throw new SQLException("Can't get database connection");
                
		PreparedStatement ps
			= con.prepareStatement(
			   "select * from app.users where id = ?");

                ps.setInt(1, Integer.parseInt(idParam));
                        
		ResultSet result =  ps.executeQuery();

		List<Profile> list = new ArrayList<Profile>();

		while(result.next())
                {
                    Profile profile = new Profile();

                    profile.setId(result.getInt("ID"));
                    profile.setMsg(result.getString("MSG"));
                    profile.setName(result.getString("USERNAME"));

                    //store all data into a List
                    list.add(profile);
		}

                searchResult = list.get(0);
            }
            return "";
          //  return list;
	}
        
        //connect to DB and get customer list
	public String searchByName() throws SQLException{
            System.out.println("TESTING  searchByName() ");

            if(nameParam.equalsIgnoreCase("-1") == false)
            {
		if(ds==null)
			throw new SQLException("Can't get data source");

		//get database connection
		Connection con = dc.getConnection();

		if(con==null)
			throw new SQLException("Can't get database connection");

		PreparedStatement ps
			= con.prepareStatement(
			   "select * from app.users where username = ?");

		//get customer data from database 
                //ps.setInt(1, Integer.parseInt(idParam));
                ps.setString(1, nameParam);
                        
		ResultSet result =  ps.executeQuery();

		List<Profile> list = new ArrayList<Profile>();

		while(result.next())
                {
                    Profile profile = new Profile();

                    profile.setId(result.getInt("ID"));
                    profile.setMsg(result.getString("MSG"));
                    profile.setName(result.getString("USERNAME"));

                    //store all data into a List
                    list.add(profile);
		}

                searchResult = list.get(0);
            }
        return "";
        //  return list;
    }
        
    public String getIdParam() {
        return idParam;
    }

    public void setIdParam(String idParam) {
        System.out.println("TESTING  setIdParam ");
        this.idParam = idParam;
    }

    public String getNameParam() {
        return nameParam;
    }

    public void setNameParam(String nameParam) {
        System.out.println("TESTING setNameParam ");
        this.nameParam = nameParam;
    }

    public Profile getResult() {
        return searchResult;
    }

    public void setResult(Profile result) {
        this.searchResult = result;
    }      
        
}
