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
 * Class used to store and update user profiles
 * @author tom
 */
@ManagedBean(name="profileBean")
@RequestScoped
public class ProfileBean implements Serializable{

	@Resource(name="jdbc/users")//resource injection
	private DataSource ds; //datasource
        private DataConnect dc; //used to connect to database
        private String idParam = "-1"; //used to store id user enters
        private String nameParam = "-1";//used to store name user enters
        private Profile searchResult;//used to store the profile of the searched user
    
        /**
         * connect to DB and get customer list
         * @return list of products
         * @throws SQLException 
         */
	public List<Profile> getProfileList() throws SQLException{
		if(ds==null)
			throw new SQLException("Can't get data source");

		//get database connection
		Connection con = dc.getConnection();

		if(con==null)
			throw new SQLException("Can't get database connection");

		PreparedStatement ps= con.prepareStatement("select * from app.users where isadmin = ?");//select all profile who are not admins
                
                ps.setBoolean(1, false);//only shows non admin users

		ResultSet result =  ps.executeQuery();//execute query

		List<Profile> list = new ArrayList<Profile>();

		while(result.next())
                {
                    Profile profile = new Profile();//create profile object

                    profile.setId(result.getInt("ID"));//set attribute of object from field in db
                    profile.setMsg(result.getString("MSG"));//set attribute of object from field in db
                    profile.setName(result.getString("USERNAME"));//set attribute of object from field in db
                    
                    list.add(profile);//store all data into a List
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
                
		PreparedStatement ps= con.prepareStatement("select * from app.users where id = ? AND isadmin = ?");//select from users where id is the one given

                ps.setInt(1, Integer.parseInt(idParam));//set param for query
                ps.setBoolean(2, false);//only shows non admin users
                        
		ResultSet result =  ps.executeQuery();//execute query

		List<Profile> list = new ArrayList<Profile>();

		while(result.next())
                {
                    Profile profile = new Profile();//create profile object

                    profile.setId(result.getInt("ID"));//set attribute of object from field in db
                    profile.setMsg(result.getString("MSG"));//set attribute of object from field in db
                    profile.setName(result.getString("USERNAME"));//set attribute of object from field in db
                    
                    list.add(profile);//store all data into a List
		}

                if(list.size() > 0)
                    searchResult = list.get(0);//get 1 profile from the list
            }
            return "";
          //  return list;
	}
        
        //connect to DB and get customer list
	public String searchByName() throws SQLException
        {
            System.out.println("TESTING  searchByName() ");

            if(nameParam.equalsIgnoreCase("-1") == false)
            {
		if(ds==null)
			throw new SQLException("Can't get data source");

		//get database connection
		Connection con = dc.getConnection();

		if(con==null)
			throw new SQLException("Can't get database connection");

		PreparedStatement ps= con.prepareStatement("select * from app.users where username = ? AND isadmin = ?");//select users with the following username
                
                ps.setString(1, nameParam);//
                ps.setBoolean(2, false);//only shows non admin users
                
		ResultSet result =  ps.executeQuery();

		List<Profile> list = new ArrayList<Profile>();

		while(result.next())
                {
                    Profile profile = new Profile();//create profile object

                    profile.setId(result.getInt("ID"));//set attribute of object from field in db
                    profile.setMsg(result.getString("MSG"));//set attribute of object from field in db
                    profile.setName(result.getString("USERNAME"));//set attribute of object from field in db
                    
                    list.add(profile);//store all data into a List
		}

                if(list.size() > 0)
                    searchResult = list.get(0);//get 1 profile from the list
            }
        return "";
        //  return list;
    }
        
        
    /**
     * Getters and setters
     */
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
