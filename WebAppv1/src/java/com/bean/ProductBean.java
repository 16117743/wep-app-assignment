/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.bean.Product;
import com.database.DataConnect;

@ManagedBean(name="product")
@SessionScoped
public class ProductBean implements Serializable{

	//resource injection
	@Resource(name="jdbc/users")
	private DataSource ds;
        private DataConnect dc;

	//if resource injection is not support, you still can get it manually.
	/*public CustomerBean(){
		try {
			Context ctx = new InitialContext();
			ds = (DataSource)ctx.lookup("java:comp/env/jdbc/mkyongdb");
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}*/
        
        public void test(){
            System.out.println("TESTING product bean");
        }

	//connect to DB and get customer list
	public List<Product> getProductList() throws SQLException{
            
                

		if(ds==null)
			throw new SQLException("Can't get data source");

		//get database connection
		Connection con = dc.getConnection();

		if(con==null)
			throw new SQLException("Can't get database connection");

		PreparedStatement ps
			= con.prepareStatement(
			   "select * from app.products");

		//get customer data from database
		ResultSet result =  ps.executeQuery();

		List<Product> list = new ArrayList<Product>();

		while(result.next()){
			Product product = new Product();
                        
                        product.setId(result.getInt("ID"));
                        product.setPrice(result.getBigDecimal("PRICE"));
                        product.setProduct(result.getString("PRODUCT"));
                        product.setQuantity(result.getInt("QUANTITY"));

			//store all data into a List
			list.add(product);
		}

		return list;
	}
}