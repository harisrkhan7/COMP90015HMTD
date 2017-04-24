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
		    while(true){
		     if(in.available() > 0){
		    		// Attempt to convert read data to JSON
		    	 System.out.println("server reading data from client ");
		    	 	JSONObject command = (JSONObject) parser.parse(in.readUTF());
		    		System.out.println("COMMAND RECEIVED: "+command.toJSONString());	
		    		String commandText;
		    	 	commandText = command.get("command").toString(); 
		    	 	if(commandText == null)
		    	 	{
		    	 		commandText = " ";
		    	 	}
		    	 	switch(commandText)
		    		{
		    		case "FETCH": 
		    			System.out.println("GET COMMAND RECEIVED");
		    			Response responseget = new Response("success","get command received");
		    			JSONObject replyget = responseget.toJSON();
		    			out.writeUTF(replyget.toJSONString());
		    			break;
		    		case "QUERY":
		    			System.out.println("QUERY COMMAND RECEIVED");
		    			Response reply1 = availableServices.query(false, new Resource(null, null, "test"));
		    			JSONObject reply12 = reply1.toJSON();
;		    			out.writeUTF(reply12.toJSONString());
		    			break;
		    		default:
		    			Response response = new Response("error","invalid command");
		    			JSONObject reply = response.toJSON();
		    			out.writeUTF(reply.toJSONString());
		    			System.out.println("default error");
		    			break;
		    		}   	
		    	}
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


