package Server;
import java.net.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
public class Connection extends Thread {
	  DataInputStream in;
	  DataOutputStream out;
	  Socket clientSocket;
	  Services availableServices;
	  JSONParser parser;
		
	  public Connection (Socket aClientSocket, Services aS) {
	    try {
	    	  availableServices = aS;
	      clientSocket = aClientSocket;
	      in = new DataInputStream( clientSocket.getInputStream());
	      out = new DataOutputStream( clientSocket.getOutputStream());
	      parser = new JSONParser();
	    } catch(IOException e) {
	       System.out.println("Connection:"+e.getMessage());
	} 
	    }
	public void run(){
		try {           
			// an echo server
		     System.out.println("server reading data from client ");
		     if(in.available() > 0){
		    		// Attempt to convert read data to JSON
		    		JSONObject command = (JSONObject) parser.parse(in.readUTF());
		    		System.out.println("COMMAND RECEIVED: "+command.toJSONString());
		    	}

//		     System.out.println("server writing data");
//		     out.writeUTF(data);
		     synchronized(availableServices){
		    	 
		     }
		  }catch (EOFException e){
		     System.out.println("EOF:"+e.getMessage());
		  } catch(IOException e) {
		     System.out.println("readline:"+e.getMessage());
		  } catch(ParseException e){
			  System.out.println("Parse: " + e.getMessage());
		  }
		finally{
		    try {
		      clientSocket.close();
		    }catch (IOException e){/*close failed*/}
		}
		}
	}


