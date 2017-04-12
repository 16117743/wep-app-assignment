/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import com.Login;
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
import java.math.BigDecimal;
import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;

@ManagedBean(name="product")
@RequestScoped
public class ProductBean implements Serializable{

    
    @Resource(name="jdbc/users")//resource injection
    private DataSource ds; //Used as a DataseSource object
    private DataConnect dc; //Used to obtain a connection to the database
    private String idParam = "-1";//used to store product id user entered
    private String nameParam = "-1";//used to store name of product user entered
    private String quantityParam = "-1";//used to store product quantity user entered
    private String priceParam = "-1";//used to store product price user entered
    private Product searchResult; // used to store attributes of the found product
    private Product newProduct; // used  to store attributes of the newly added product
    final static Logger logger = Logger.getLogger(ProductBean.class); //used to write to the log found in the DIR: /home//logfile

    /**
     * connect to DB and get customer list
     * @return list of products
     * @throws SQLException 
     */
    public List<Product> getProductList() throws SQLException
    {    
        if(ds==null)
            throw new SQLException("Can't get data source");

        //get database connection
        Connection con = dc.getConnection();//obtain connection to db

        if(con==null)
            throw new SQLException("Can't get database connection");//throw SQL connection if connection is null

        PreparedStatement ps = con.prepareStatement("select * from app.products");//prepared statement used to prevent SQL injection  
        ResultSet result =  ps.executeQuery();//get customer data from database

        List<Product> list = new ArrayList<Product>(); //list to hold products

        while(result.next()) //while results set has a next result
        {
            Product product = new Product(); //create product bean

            product.setId(result.getInt("ID"));//set id attribute
            product.setPrice(result.getBigDecimal("PRICE"));//set price attribute
            product.setProduct(result.getString("PRODUCT"));//set productname attribute
            product.setQuantity(result.getInt("QUANTITY"));//set quantity attribute

            list.add(product);//add bean to list
        }

        return list;//return the list of products
    }
    
    /**
     * Finds a product based on id
     * @return
     * @throws SQLException 
     */
    public String searchByID() throws SQLException
    {

        if(idParam.equalsIgnoreCase("-1") == false)
        {
            if(ds==null)
                    throw new SQLException("Can't get data source");//throw SQL connection if ds is null

            //get database connection
            Connection con = dc.getConnection();//obtain connection to db

            if(con==null)
                    throw new SQLException("Can't get database connection");//throw SQL connection if connection is null
  
            PreparedStatement ps = con.prepareStatement("select * from app.products where id = ?");//A1: Injection prevention using prepared statement

            ps.setInt(1, Integer.parseInt(idParam));//set parameter for query

            ResultSet result =  ps.executeQuery();//store result in resultset

            List<Product> list = new ArrayList<Product>(); //list to hold products

            while(result.next()) //while results set has a next result
            {
                Product product = new Product(); //create product bean

                product.setId(result.getInt("ID"));//set id attribute
                product.setPrice(result.getBigDecimal("PRICE"));//set price attribute
                product.setProduct(result.getString("PRODUCT"));//set productname attribute
                product.setQuantity(result.getInt("QUANTITY"));//set quantity attribute

                list.add(product);//add bean to list
            }

            searchResult = list.get(0);//get 1 product from the list
        }
        return "";//no page redirection
      //  return list;
    }

    /**
     * Finds a product based on product name
     * @return
     * @throws SQLException 
     */
    public String searchByName() throws SQLException
    {
        System.out.println("TESTING  searchByName() ");

        if(idParam.equalsIgnoreCase("-1") == false)//if user has entered a parameter
        {
            if(ds==null)
                    throw new SQLException("Can't get data source");//throw SQL connection if ds is null

            //get database connection
            Connection con = dc.getConnection();//obtain connection to db

            if(con==null)
                    throw new SQLException("Can't get database connection");//throw SQL connection if connection is null

            //A1: Injection prevention using prepared statement
            PreparedStatement ps= con.prepareStatement("select * from app.products where product = ?");//select all product where name is ?

            ps.setString(1, nameParam);//set parameter for query

            ResultSet result =  ps.executeQuery();//execute query

            List<Product> list = new ArrayList<Product>();

            while(result.next()) //while results set has a next result
            {
                Product product = new Product(); //create product bean

                product.setId(result.getInt("ID"));//set id attribute
                product.setPrice(result.getBigDecimal("PRICE"));//set price attribute
                product.setProduct(result.getString("PRODUCT"));//set productname attribute
                product.setQuantity(result.getInt("QUANTITY"));//set quantity attribute

                list.add(product);//add bean to list
            }

            searchResult = list.get(0);//get 1 product from the list
        }
        return "";//no page redirection
      //  return list;
    }
    
    //connect to DB and get customer list
    public List<Product> getProductListAdmin(Login user) throws SQLException
    {    
        if(user.getIsAdmin() ==true)// protect against A7 Missing Function Level Access Control
        {
            if(ds==null)
                    throw new SQLException("Can't get data source");//throw SQL connection if ds is null

            //get database connection
            Connection con = dc.getConnection();//obtain connection to db

            if(con==null)
                    throw new SQLException("Can't get database connection");//throw SQL connection if connection is null

            //A1: Injection prevention using prepared statement
            PreparedStatement ps = con.prepareStatement("select * from app.products"); //select all products in table

            ResultSet result =  ps.executeQuery();//execute query

            List<Product> list = new ArrayList<Product>();

            while(result.next()) //while results set has a next result
            {
                Product product = new Product(); //create product bean

                product.setId(result.getInt("ID"));//set id attribute
                product.setPrice(result.getBigDecimal("PRICE"));//set price attribute
                product.setProduct(result.getString("PRODUCT"));//set productname attribute
                product.setQuantity(result.getInt("QUANTITY"));//set quantity attribute

                list.add(product);//add bean to list
            }
            return list;
        }
        else //else user is not an admin
        {
            FacesContext.getCurrentInstance().addMessage(
                                    null,
                                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                    "Missing Function Level Access Control Prevention!",
                                                    ""));
        }
        return null;
    }

    
    /**
     * Allows admin to update quantity of product
     * @param user used to verify if admin
     * @return 
     */
    public String updateQuantity(Login user)
    {
        Connection con = null;
        PreparedStatement ps = null;

        if(user.getIsAdmin() ==true)// protect against A7 Missing Function Level Access Control
        {
            try
            {
                List<Product> productList = getProductList();

                for (Product p: productList)
                {
                    con = DataConnect.getConnection();//get connection to db
                    con.setAutoCommit(true);//set auto commit true
                    //A1: Injection prevention using prepared statement
                    ps = con.prepareStatement("UPDATE APP.PRODUCTS SET QUANTITY = ? WHERE ID = ?");//update the quantity where id = ?

                    if(p.getId().equals(Integer.parseInt(idParam)))//if the id of product in the list is the same as the one provided by user
                    {
                        int updateQuantity = Integer.parseInt(quantityParam);//convert string to int
                        ps.setInt(1, updateQuantity);//set param for query
                        ps.setInt(2, p.getId());//set param for query
                        ps.execute();//execute statement
                        con.commit();//commit changes to db
                    }
                }  
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else //else user is not an admin
        {
            FacesContext.getCurrentInstance().addMessage(
                                    null,
                                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                    "Missing Function Level Access Control Prevention!",
                                                    ""));
        }
        return "";//no page redirection
    }
       
    /**
     * Allows admin to add new product to database
     * @param user used to determine if admin
     * @return 
     */
    public String addNewProduct(Login user)
    {   
        Connection con = null;
        PreparedStatement ps = null;

        if(user.getIsAdmin() ==true)// protect against A7 Missing Function Level Access Control
        {
            try
            {
                List<Product> productList = getProductList();

                for (Product p: productList)
                {              
                    con = DataConnect.getConnection();//get connection to db
                    con.setAutoCommit(true);//set auto commit true
                   
                    ps = con.prepareStatement("INSERT INTO APP.PRODUCTS (ID, PRODUCT, QUANTITY, PRICE) VALUES (?,?,?,?)"); //A1: Injection prevention using prepared statement

                    int id = Integer.parseInt(idParam);
                    ps.setInt(1, id);//set id param
                    ps.setString(2, nameParam);//set name param
                    ps.setInt(3, Integer.parseInt(quantityParam));//set quantity param
                    double val = Double.parseDouble(priceParam);
                    ps.setDouble(4, val);//set price param
                    ps.execute();
                    con.commit();   

                    logger.info("Product : " + nameParam + " added to inventory");//update log of added item event
                }  
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            FacesContext.getCurrentInstance().addMessage(
                                    null,
                                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                    "A7: Missing Function Level Access Control Prevention!",
                                                    ""));
        }
        return "";//no page redirection
    }
    
    /**
     * Allow admin to remove products from database
     * @param removed product to be removed
     * @param user used to verify if is admin
     * @return 
     */
    public String removeProduct(Product removed, Login user)
    {
        Connection con = null;
        PreparedStatement ps = null;
        
        if(user.getIsAdmin() ==true)// protect against A7 Missing Function Level Access Control
        {
            try
            {
                con = DataConnect.getConnection();//get connection to db
                con.setAutoCommit(true);//set auto commit true
                //A1: Injection prevention using prepared statement
                ps = con.prepareStatement("DELETE FROM APP.PRODUCTS WHERE ID =?");//delete product with matching id user provided

                int id = removed.getId();//get id from selected product
                ps.setInt(1, id);//set param
                ps.execute();//execute statement
                con.commit();//commit changes to database

                logger.info("Product : " + removed.getProduct() + " removed from inventory");//update log of removed item event
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            FacesContext.getCurrentInstance().addMessage(
                                    null,
                                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                    "A7: Missing Function Level Access Control Prevention!",
                                                    ""));
        }
        
        return "";//no page redirection
    }
    
    
    /************************
     * Getters and Setters 
     ************************/
    public Product getNewProduct() {
        return newProduct;
    }

    public void setNewProduct(Product newProduct) {
        this.newProduct = newProduct;
    }

    public String getIdParam() {
        return idParam;
    }

    public void setIdParam(String idParam) {
        this.idParam = idParam;
    }

    public String getNameParam() {
        return nameParam;
    }

    public void setNameParam(String nameParam) {
        this.nameParam = nameParam;
    }

    public Product getResult() {
        return searchResult;
    }

    public void setResult(Product result) {
        this.searchResult = result;
    }    

    public String getQuantityParam() {
        return quantityParam;
    }

    public void setQuantityParam(String quantityParam) {
        this.quantityParam = quantityParam;
    }

    public String getPriceParam() {
        return priceParam;
    }

    public void setPriceParam(String priceParam) {
        this.priceParam = priceParam;
    } 
    
     public void test(){
        System.out.println("TESTING product bean");
    }

    
}
