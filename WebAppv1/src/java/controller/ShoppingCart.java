/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.bean.Product;
import com.database.DataConnect;
import entity.Item;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author tom
 */
@ManagedBean(name = "sc")
@SessionScoped
public class ShoppingCart implements Serializable {
    private List<Item> cart = new ArrayList<Item>();
    private float total;
    private DataConnect dc;
    private String checkout = "success";

    public List<Item> getCart() {
        return cart;
    }

    public void setCart(List<Item> cart) {
        System.out.println("TESTING cart!!!");
        this.cart = cart;
    }
    
    public void test() {
        System.out.println("TESTING cart!!!");
    }

    public float getTotal() {
        total =0;
        for(Item item:cart){
            System.out.println("Item ID in cart = " + item.getP().getId());
            System.out.println("Item price in cart = " + item.getP().getPrice());
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

    

    public String addtocart(Product p)
    {
        //Increment quantity if Duplicate product
        for (Item item: cart){
            if(item.getP().getId() == p.getId())
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
        return "cart";       
    }
    
    public void update(){
        getTotal();
    }
    
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
    
    public String checkout(){
        Connection con = null;
        PreparedStatement ps = null;

        try 
        {
            con = DataConnect.getConnection();
            con.setAutoCommit(false);
            //ps = con.prepareStatement("UPDATE APP.PRODUCTS SET QUANTITY = ? WHERE ID = ?");
            
            for(Item item : cart)
            {
                ps = con.prepareStatement("select * from app.products where ID = ?");           
                ps.setInt(1, item.getP().getId());        
		ResultSet result =  ps.executeQuery();
                int inStoreQuantity = -1;
                
                System.out.println("item.getP().getId() = " + item.getP().getId());
                System.out.println("item.getQuantity() = " + item.getQuantity());
                System.out.println("inStoreQuantity = " + inStoreQuantity);
                
		if(result.next()) 
                    inStoreQuantity =  result.getInt("QUANTITY");
   
                System.out.println("inStoreQuantity = " + inStoreQuantity);
                
                if(inStoreQuantity >= item.getQuantity())
                {
                    System.out.println("TRUE!");
                    
                    try
                    {
                        ps = con.prepareStatement("UPDATE APP.PRODUCTS SET QUANTITY = ? WHERE ID = ?");
                        int updateQuantity = inStoreQuantity - item.getQuantity();
                        ps.setInt(1, updateQuantity);
                        ps.setInt(2, item.getP().getId());
                        ps.execute();
                        con.commit();
                        return "checkout";    
                    }
                    catch (SQLException ex)
                    {
                       ex.printStackTrace();
                    }
                }
                else{
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
 
        return "checkout";
    }
    
    public Boolean validateQuantity(Item item){
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
    
    public String cancelOrder(){
        return "";
    }
}
