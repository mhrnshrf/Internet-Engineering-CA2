import java.io.*;
import java.net.*;
import java.util.*;
import com.sun.net.httpserver.*;
import ir.ramtung.coolserver.*;


class Request{
	String symbolName;
	int quantity;
	int price;
	int customerID;
	String type;

	public Request(String symbolName, int quantity, int price, int customerID, String type){
		this.symbolName = symbolName;
		this.quantity = quantity;
		this.price = price;
		this.customerID = customerID;
		this.type = type;
	}
	public Request copyRequest(){
		return new Request(symbolName, quantity, price, customerID, type);
	}
	public int getPrice(){return price;}
	public int getQuant(){return quantity;}
	public int getID(){return customerID;}
	public String getType(){return type;}
	public String getSymbol(){return symbolName;}
	public void setQuant(int q){quantity = q;}
	public void setPrice(int p){price = p;}
}
