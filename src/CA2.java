import java.io.*;
import java.net.*;
import java.util.*;
import com.sun.net.httpserver.*;
import ir.ramtung.coolserver.*;


class Add extends ServiceHandler{
	public Add(){
		Customer c = new Customer(1,"admin","admin");
		Customer.list.add(c);
		// ServiceHandler();
	}
	public void execute(PrintWriter out) {
        int id = Integer.parseInt(params.get("id"));
        String name = params.get("name");
        String family = params.get("family");
        String response = new String();

        if(!Customer.exist(id) && id != 1){
        	Customer c = new Customer(id, name, family);
        	Customer.list.add(c);
        	response = "New user is added";
        }
        else
        	response = "Repeated id";


        Page responsePage = new Page("./res.html")
            .subst("response", response);

        responsePage.writeTo(out);
    }
}

class Deposit extends ServiceHandler{
	public void execute(PrintWriter out) {
        int id = Integer.parseInt(params.get("id"));
        int amount = Integer.parseInt(params.get("amount"));
        String response = new String();

        if(Customer.exist(id)){
        	Customer.getCustomer(id).deposit(amount);
        	response = "Successful";
        }
        else
        	response = "Unknown user id";


        Page responsePage = new Page("./res.html")
            .subst("response", response);
        responsePage.writeTo(out);
	} 
}

class Withdraw extends ServiceHandler{
	public void execute(PrintWriter out) {
        int id = Integer.parseInt(params.get("id"));
        int amount = Integer.parseInt(params.get("amount"));
        String response = new String();

        if(Customer.exist(id)){
        	if (Customer.getCustomer(id).withdraw(amount)) 
        		response = "Successful";
        	else	
        		response = "Not enough money";
        	
        }
        else
        	response = "Unknown user id";

        Page responsePage = new Page("./res.html")
            .subst("response", response);
        responsePage.writeTo(out);
	} 
}

class Sell extends ServiceHandler{
	public void execute(PrintWriter out) {
        int id = Integer.parseInt(params.get("id"));
        String instrument = params.get("instrument");
        int  price = Integer.parseInt(params.get("price"));
        int  quantity = Integer.parseInt(params.get("quantity"));
        String type = params.get("type");

        String response = new String();
        Symbol s;

        if (id == 1) {
        	if (Symbol.exist(instrument)) {
        		s = Symbol.getSymbol(instrument);
        	}
        	else{
        		s = new Symbol(instrument);
        		Symbol.list.add(s);
        	}

        	response = s.updateList(id, instrument, price, quantity, type, "sell");
        	
        }
		else if (!Symbol.exist(instrument)) 
			response = "Invalid symbol id";
        else if(!Customer.exist(id))
        	response = "Unknown user id";
        else if (Customer.getCustomer(id).getShare(instrument) < quantity)	
        	response = "Not enough share";
        else{
        	s = Symbol.getSymbol(instrument);
        	response = s.updateList(id, instrument, price, quantity, type, "sell");
        }

        Page responsePage = new Page("./res.html")
            .subst("response", response);
        responsePage.writeTo(out);
	} 
}


class Buy extends ServiceHandler{
	public void execute(PrintWriter out) {
        int id = Integer.parseInt(params.get("id"));
        String instrument = params.get("instrument");
        int  price = Integer.parseInt(params.get("price"));
        int  quantity = Integer.parseInt(params.get("quantity"));
        String type = params.get("type");

        String response = new String();
        Symbol s;

		if (!Symbol.exist(instrument)) 
			response = "Invalid symbol id";
        else if(!Customer.exist(id))
        	response = "Unknown user id";
        else if (Customer.getCustomer(id).getCash() < quantity*price)	
        	response = "Not enough money";
        else{
        	s = Symbol.getSymbol(instrument);
        	response = s.updateList(id, instrument, price, quantity, type, "buy");
        }
        Page responsePage = new Page("./res.html")
            .subst("response", response);
        responsePage.writeTo(out);
	} 
}

public class CA2 {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(9091), 0);
        server.createContext("/customer/add", new Add());
        server.createContext("/customer/deposit", new Deposit());
        server.createContext("/customer/withdraw", new Withdraw());
        server.createContext("/order/sell", new Sell());
        server.createContext("/order/buy", new Buy());
        server.start();
    }
}
