package com;

import net.sf.json.JSONArray;

/**
 * @ authour Gongsheng Yuan
 */
public class ElementJsonClass {

    public String orderId;
    public String personId;
    public String orderDate;
    public String totalPrice;

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String asin;

    //public String orderLine;
    public JSONArray orderLine;

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public String getPersonId() {
        return personId;
    }
    public void setPersonId(String personId) {
        this.personId = personId;
    }


    public String getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }


    public String getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }



//
//    public String getOrderLine() {
//        return orderLine;
//    }
//    public void setOrderLine(String orderLine) {
//        this.orderLine = orderLine;
//    }

    public JSONArray getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(JSONArray orderLine) {
        this.orderLine = orderLine;
    }


}
