import java.io.*;
import java.net.*;
import java.util.*;
import com.sun.net.httpserver.*;
import ir.ramtung.coolserver.*;



class Customer{
	int id;
	int cash;
	String name;
	String family;
	Map<String, Integer> stocks;

	ArrayList<Request> doneReq;
	ArrayList<Request> rejectedReq; 
	ArrayList<Request> processingReq;

	public Customer(int id, String name, String family){
		this.id = id;
		this.name = name;
		this.family = family;
		this.cash = 0;
	 	stocks = new HashMap<String, Integer>();
		doneReq = new ArrayList<Request>();
		rejectedReq = new ArrayList<Request>();
		processingReq = new ArrayList<Request>();

	}
	public static ArrayList<Customer> list = new ArrayList<Customer>();
	public static boolean exist(int id){
		for (int i = 0; i < list.size() ; i++) {
			if(list.get(i).id == id)
				return true;
			
		}
		return false;
	}
	public static Customer getCustomer(int id){
		for (int i = 0; i < list.size() ; i++) {
			if(list.get(i).id == id)
				return list.get(i);
			
		}
		return null;
	}
	public void deposit(int amount){
		cash = cash + amount;
	}
	public boolean withdraw(int amount){

		if ( cash < amount) 
			return false;

		cash = cash - amount;
		return true;

	}
	public void update(Request r, String req){
		System.err.println("b4 if");
		if (req.equals("buy")) {
			System.err.println("if1");
			cash -= r.getPrice()*r.getQuant();
			System.err.println("if2");
			System.err.println(r.getSymbol());
			System.err.println(stocks.get(r.getSymbol()));
			if(!stocks.containsKey(r.getSymbol()))
				stocks.put(r.getSymbol(), r.getQuant());
			else
				stocks.put(r.getSymbol(), stocks.get(r.getSymbol()) + r.getQuant());
			System.err.println("if3");
		}
		else{
			System.err.println("else");
			cash += r.getPrice()*r.getQuant();
			if(!stocks.containsKey(r.getSymbol()))
				stocks.put(r.getSymbol(), r.getQuant());
			else
				stocks.put(r.getSymbol(), stocks.get(r.getSymbol()) - r.getQuant());
		}
		System.err.println("after if");
		
		for (int i =0; i < processingReq.size() ; i++) {
		System.err.println("for ");
		System.err.println(i);
			if (processingReq.get(i).getQuant() == 0) 
				processingReq.remove(i);
		}
		doneReq.add(r);

	}
	public boolean canAfford(Request r){
		if (r.getQuant() * r.getPrice() > cash ) 
			return false;	
		return true;
	}
	public int getCash(){ return cash;}
	public void addRejected(Request r){
		rejectedReq.add(r);
	}
	public int getShare(String instrument){
		if(stocks.containsKey(instrument))
			return stocks.get(instrument);
		return 0;
	}
} 
