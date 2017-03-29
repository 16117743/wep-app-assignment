/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.bean.Product;
import entity.Item;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author tom
 */
@ManagedBean(name = "sc")
@SessionScoped
public class ShoppingCart implements Serializable {
    private List<Item> cart = new ArrayList<Item>();
    private float total;

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
            total = total = (item.getQuantity()*item.getP().getPrice().floatValue());
        }
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
    
    public String addtocart(Product p)
    {
        System.out.println("TESTING add to cart!!!");
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
        //reload page
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
}
