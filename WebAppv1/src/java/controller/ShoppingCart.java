/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.bean.Product;
import com.bean.ProductBean;
import com.database.DataConnect;
import entity.Item;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;

/**
 * Class used to handle Shopping cart functionality
 * @author tom
 */
@ManagedBean(name = "sc") //"sc" used to refer to bean in xhtml file 
@SessionScoped // scope set to session
public class ShoppingCart implements Serializable 
{
    private List<Item> cart = new ArrayList<Item>();//stores the items in the cart
    private float total; //stores total price of items in cart
    private DataConnect dc;//used to obtain a connection to the database
    private String checkout = "success";//redirect user to checkout page
    final static Logger logger = Logger.getLogger(ShoppingCart.class);//used to write to the log when a user confirms or cancels and order

    /**
     * Used to add a product to the cart
     * @param p the product
     * @return redirect user to the cart.xtml
     */
    public String addtocart(Product p)
    {
        for (Item item: cart){
            if(item.getP().getId() == p.getId())//Increment quantity if Duplicate product
            {
                item.setQuantity(item.getQuantity()+1);
                return "cart";
            }
        }
        //creating a new item in cart
        Item i = new Item();
        i.setQuantity(1);
        i.setP(p);
        cart.add(i);
        return "cart";//redirect to cart.xtml page  
    }
    
    /**
     * used to update the user on total price of items in cart
     */
    public void update(){
        getTotal();
    }
    
    /**
     * used to remove an item from the cart
     * @param i the item to be removed
     */
    public void remove(Item i)
    {
        for(Item item : cart){
            if(item.equals(i))
            {
                cart.remove(item);
                break;
            }
        }
    }
    
    /**
     * used to checkout, verifies that there is enough items in stock
     * @return 
     */
    public String checkout()
    {
        Connection con = null;
        PreparedStatement ps = null;

        try 
        {
            con = DataConnect.getConnection();//obtain connection
            con.setAutoCommit(false);//set auto cmmit to false
            
            for(Item item : cart) //for each item in the cart
            {
                //A1: Injection prevention using prepared statement
                ps = con.prepareStatement("select * from app.products where ID = ?");//select products with matching id           
                ps.setInt(1, item.getP().getId()); //set param for query       
		ResultSet result =  ps.executeQuery();//execute query
                int inStoreQuantity = -1;
                
		if(result.next()) 
                    inStoreQuantity =  result.getInt("QUANTITY");//update in store quantity from database
                
                if(inStoreQuantity >= item.getQuantity()) //if there is enough quantity for given item
                {      
                    try
                    {
                        return "checkout";//redirect user to checkout.xhtml
                    }
                    catch (Exception ex)
                    {
                       ex.printStackTrace();
                    }
                }
                else // else tell user to select lower quantity
                {
                FacesContext.getCurrentInstance().addMessage(
                                    null,
                                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                    "Please select a lower quantity in order to checkout",
                                                    ""));
                return "";//no page redirection
                }
            }
        } 
        catch (SQLException ex)
        {
            System.out.println("checkout error -->" + ex.getMessage());
            try
            {
                if(con!=null)
                    con.rollback();
            }
            catch(SQLException se2)
            {
                se2.printStackTrace();
            }//end try
        } 
        finally 
        {
            DataConnect.close(con);//close connection to database
            try{
                con.setAutoCommit(true);
            }
            catch(Exception e){}              
        }         
 
        return "checkout";
    }
    
    
    /**
     * Used to validate the quantity of items in db against shopping cart
     * @param item
     * @return 
     */
    public Boolean validateQuantity(Item item)
    {
        Connection con = null;
        PreparedStatement ps = null;
        try
        {
            if(ps==null)
                    throw new SQLException("Can't get data source");

            //get database connection
            con = dc.getConnection();

            if(con==null)
                    throw new SQLException("Can't get database connection");

            ps = con.prepareStatement("select * from app.products where ID = ?");

            //get customer data from database 
            ps.setInt(1, item.getP().getId());

            ResultSet result =  ps.executeQuery();

            List<Product> list = new ArrayList<Product>();

            int quantity = -1;
            while(result.next()){
                quantity =  result.getInt("QUANTITY");
            }

            if(quantity < item.getQuantity())
                return true;
            else
                return false;
        }
        catch (SQLException ex) {
                    System.out.println("validateQuantity error -->" + ex.getMessage());
              
            } finally {
                    DataConnect.close(con);
            }  
            
        return true;
    }
    
    
    /**
     * used to cancel an order
     * @param customer
     * @return 
     */
    public String cancelOrder(String customer){
        logger.info("Customer : " + customer + " cancelled their order");//update log that user has cancelled an order   
        
        return "cancelorder";//redirect to cancelorder.xhtml
    }
    
    
    /**
     * used to confirm the order, entry added to order entry table
     * @param customer
     * @return 
     */
    public String confirmOrder(String customer)
    {    
        Connection con = null;
        PreparedStatement ps = null;

        try
        {
            con = DataConnect.getConnection();//obtain connection
            con.setAutoCommit(true);//set to true
            //A1: Injection prevention using prepared statement
            ps = con.prepareStatement("INSERT INTO APP.ORDERS (CUSTOMER, COST, ORDERID) VALUES (?,?,?)");//insert into table an order entry

            ps.setString(1, customer);//set param for statement
            double totalPrice = getTotal();
            ps.setDouble(2, totalPrice);//set param for statement
            int orderID = ThreadLocalRandom.current().nextInt(1, 10000 + 1);//generate a random id in range 1 - 10000
            ps.setInt(3, orderID);//set param for statement
            ps.execute();//execute statemet
            con.commit();//commit to db

            logger.info("Customer : " + customer + " confirmed their order"); // update log that user confirmed order
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        /***********************************/

        try 
        {
            con = DataConnect.getConnection();
            con.setAutoCommit(false);
            
            for(Item item : cart)
            {
                ps = con.prepareStatement("select * from app.products where ID = ?");//get all products that have matching id         
                ps.setInt(1, item.getP().getId());//set param for statemnet
		ResultSet result =  ps.executeQuery();//execute Query
                int inStoreQuantity = -1;
                
		if(result.next()) 
                    inStoreQuantity =  result.getInt("QUANTITY");//get instore quantity from db
                
                if(inStoreQuantity >= item.getQuantity())//verify quantity of item before commiting to database
                {      
                    try
                    {
                        ps = con.prepareStatement("UPDATE APP.PRODUCTS SET QUANTITY = ? WHERE ID = ?");//update products quantity where id matches
                        int updateQuantity = inStoreQuantity - item.getQuantity(); //update quantity is the difference between inStoreQuantity - selected item quantity
                        ps.setInt(1, updateQuantity);//set param
                        ps.setInt(2, item.getP().getId());//set param
                        ps.execute();//execute
                        con.commit();//commit to db
                        return "final"; //redirect to final.xhtml   
                    }
                    catch (SQLException ex)
                    {
                       ex.printStackTrace();
                    }
                }
                else
                {
                FacesContext.getCurrentInstance().addMessage(
                                    null,
                                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                    "Please select a lower quantity in order to checkout",
                                                    ""));
                return "";
                }
            }
        } 
        catch (SQLException ex)
        {
            System.out.println("checkout error -->" + ex.getMessage());
            try
            {
                if(con!=null)
                    con.rollback();
            }
            catch(SQLException se2)
            {
                se2.printStackTrace();
            }//end try
        } 
        finally 
        {
            DataConnect.close(con);
            try{
                con.setAutoCommit(true);
            }
            catch(Exception e){}              
        }         
        
        
        return ""; //no page redirect
    }
    
    public void setCart(List<Item> cart) {
    this.cart = cart;
    }
        
         public List<Item> getCart() {
        return cart;
    }



    public float getTotal() 
    {
        total =0;
        for(Item item:cart){
            total = total + (item.getQuantity()*item.getP().getPrice().floatValue());
        }
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    public void test() {
        System.out.println("TESTING cart!!!");
    }
}
