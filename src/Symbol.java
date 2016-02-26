import java.io.*;
import java.net.*;
import java.util.*;
import com.sun.net.httpserver.*;
import ir.ramtung.coolserver.*;

class Symbol{
	String name;
	ArrayList<Request> buy; 
	ArrayList<Request> sell;
	public static ArrayList<Symbol> list = new ArrayList<Symbol>();
	public Symbol(String name){
		this.name = name;
		buy = new ArrayList<Request>();
		sell = new ArrayList<Request>();
	}
	public static boolean exist(String name){
		for (int i = 0; i < list.size() ; i++) {
			if(list.get(i).name.equals(name))
				return true;
			
		}
		return false;
	}
	public static Symbol getSymbol(String name){
		for (int i = 0; i < list.size() ; i++) {
			if(list.get(i).name.equals(name))
				return list.get(i);
			
		}
		return null;
	}
	// public boolean reqSell(int id, String instrument, int price, int quantity){
	// 	if(id!=1 && quantity > Customer.getCustomer(id).stocks.get(instrument))
	// 		return false;
	// 	Request r = new Request(instrument, quantity, price, id);
	// 	int i;
	// 	for (i = 0; i < sell.size() ; i++) {
	// 		if(price < sell.get(i).getPrice())
	// 			break;
			
	// 	}
	// 	sell.add(i,r);
	// 	return true;
	// }
	// public boolean reqBuy(int id, String instrument, int price, int quantity){
	// 	if(quantity*price > Customer.getCustomer(id).cash)
	// 		return false;
	// 	Request r = new Request(instrument, quantity, price, id);
	// 	int i;
	// 	for (i = 0; i < buy.size() ; i++) {
	// 		if(price > buy.get(i).getPrice())
	// 			break;
			
	// 	}
	// 	buy.add(i,r);
	// 	return true;
	// }

	public String updateList(int id, String instrument, int price, int quantity, String type, String req){

		if (req.equals("sell")) {
			if(id!=1 && quantity > Customer.getCustomer(id).stocks.get(instrument))
				return "Not enough share";
			Request r = new Request(instrument, quantity, price, id, type);
			if (type.equals("MPO") || type.equals("IOC")) {
				sell.add(0,r);
			}
			else{
				int i;
				for (i = 0; i < sell.size() ; i++) {
					if(price < sell.get(i).getPrice())
						break;
					
				}
				sell.add(i,r);
			}

		}
		if (req.equals("buy")) {
			if(quantity*price > Customer.getCustomer(id).getCash())
				return "Not enough money";
			Request r = new Request(instrument, quantity, price, id, type);
			if (type.equals("MPO") || type.equals("IOC")) {
				buy.add(0,r);
			}
			else{
				int i;
				for (i = 0; i < buy.size() ; i++) {
					if(price < buy.get(i).getPrice())
						break;
					
				}
				buy.add(i,r);
			}
		}
		return bargain();
	}
	public String bargain(){
		if (buy.size() == 0 && (sell.get(0).getType().equals("MPO") ||sell.get(0).getType().equals("IOC")) ) {
			System.err.println("if1");
			Customer.getCustomer(sell.get(0).getID()).addRejected(sell.get(0));
			sell.remove(0);
			return "Order is declined";			
		}
		else if (sell.size() == 0 && (buy.get(0).getType().equals("MPO") ||buy.get(0).getType().equals("IOC")) ) {
			System.err.println("if2");
			Customer.getCustomer(buy.get(0).getID()).addRejected(buy.get(0));
			buy.remove(0);
			return "Order is declined";			
		}
		else if (buy.size() == 0 || sell.size() == 0)
			return "Order is queued";

		if (sell.get(0).getType().equals("MPO")) {
			int sumStock = 0;
			for (int i =0; i < buy.size(); i++){ 
				Customer c = Customer.getCustomer(buy.get(i).getID());
				if (c.getCash() < buy.get(i).getPrice() *buy.get(i).getQuant()) {
					buy.remove(i);
					i--;
					continue;
				}
				sumStock += buy.get(i).getQuant();
			}

			if (sumStock < sell.get(0).getQuant()) {
				Customer.getCustomer(sell.get(0).getID()).addRejected(sell.get(0));
				sell.remove(0);
				return "Order is declined";
			}
			sell.get(0).setPrice(buy.get(0).getPrice());
		}
		else if (buy.get(0).getType().equals("MPO")) {
			int sumStock = 0;
			int sumPrice = 0;
			for (int i =0; i < sell.size(); i++){ 
				int oldSum = sumStock;
				sumStock += sell.get(i).getQuant();
				if (sumStock >= buy.get(0).getQuant()) {
					sumPrice += (buy.get(0).getQuant() - oldSum)*sell.get(i).getPrice();
					break;
				}
				sumPrice += sell.get(i).getQuant()*sell.get(i).getPrice();
			}
			if (sumStock < buy.get(0).getQuant() || sumPrice > Customer.getCustomer(buy.get(0).getID()).getCash()) {
				Customer.getCustomer(buy.get(0).getID()).addRejected(buy.get(0));
				buy.remove(0);
				return "Order is declined";
			}
			buy.get(0).setPrice(sell.get(0).getPrice());
		}
		else if (sell.get(0).getType().equals("IOC")) {
			int sumStock = 0;
			for (int i =0; i < buy.size() && buy.get(i).getPrice() >= sell.get(0).getPrice(); i++){ 
				Customer c = Customer.getCustomer(buy.get(i).getID());
				if (c.getCash() < buy.get(i).getPrice() *buy.get(i).getQuant()) {
					buy.remove(i);
					i--;
					continue;
				}
				sumStock += buy.get(i).getQuant();
			}

			if (sumStock < sell.get(0).getQuant()) {
				Customer.getCustomer(sell.get(0).getID()).addRejected(sell.get(0));
				sell.remove(0);
				return "Order is declined";
			}
		}
		else if (buy.get(0).getType().equals("IOC")) {
			int sumStock = 0;
			int sumPrice = 0;
			for (int i =0; i < sell.size() && sell.get(i).getPrice() <= buy.get(0).getPrice(); i++){ 
				int oldSum = sumStock;
				sumStock += sell.get(i).getQuant();
				if (sumStock >= buy.get(0).getQuant()) {
					sumPrice += (buy.get(0).getQuant() - oldSum)*buy.get(0).getPrice();
					break;
				}
				sumPrice += sell.get(i).getQuant()*buy.get(0).getPrice();
			}
			if (sumStock < buy.get(0).getQuant() || sumPrice > Customer.getCustomer(buy.get(0).getID()).getCash()) {
				Customer.getCustomer(buy.get(0).getID()).addRejected(buy.get(0));
				buy.remove(0);
				return "Order is declined";
			}
		}
		StringBuilder response = new StringBuilder();

		while(sell.get(0).getPrice() <= buy.get(0).getPrice()){
			System.err.println("up 1");
			int q = buy.get(0).getQuant() - sell.get(0).getQuant();
			Request b = buy.get(0).copyRequest(); 
			Request s = sell.get(0).copyRequest(); 
			int stockNum;
			System.err.println("up 2");
			int sellerID = sell.get(0).getID();
			int buyerID = buy.get(0).getID();
			int p = buy.get(0).getPrice();
			if(q < 0){

				stockNum = buy.get(0).getQuant();
				s.setQuant(buy.get(0).getQuant());
				System.err.println("up 6");
				System.err.println("up 7");
				System.err.println("up 8");
				buy.get(0).setQuant(0);
				sell.get(0).setQuant((-1)*q);
				Customer.getCustomer(buy.get(0).getID()).update(b, "buy");
				Customer.getCustomer(sell.get(0).getID()).update(s, "sell");
				buy.remove(0);
			}
			else{

				b.setQuant(sell.get(0).getQuant());
				stockNum = sell.get(0).getQuant();
				buy.get(0).setQuant(q);
				sell.get(0).setQuant(0);
				Customer.getCustomer(buy.get(0).getID()).update(b, "buy");
				Customer.getCustomer(sell.get(0).getID()).update(s, "sell");
				sell.remove(0);
				if (q == 0)
					buy.remove(0);
			}
			System.err.println("up 3");
			// ‫‪123‬‬ ‫‪sold‬‬ ‫‪40‬‬ ‫‪shares‬‬ ‫‪o‬‬ ‫‪f‬‬ ‫‪RANA1‬‬ ‫‪@130‬‬ ‫‪to‬‬ ‫‪345‬‬
			if(response.length()!=0)
				response.append("<br>");			
			response.append(sellerID);
			response.append(" sold ");
			response.append(stockNum);
			response.append(" shares of ");
			response.append(name);
			response.append(" @");
			response.append(p);
			response.append(" to ");
			response.append(buyerID);
			System.err.println(response);


			if (buy.size() != 0 && buy.get(0).getType().equals("MPO")) 
				buy.get(0).setPrice(sell.get(0).getPrice());
			else if (buy.size() != 0 && sell.get(0).getType().equals("MPO")) 
				sell.get(0).setPrice(buy.get(0).getPrice());
			if (buy.size() == 0 || sell.size() == 0) 
				break;
			
		}
		if (response.length() == 0) 
			return "Order is queued";
		return response.toString();		
	}
}
