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
	  VerifyRequestObject verify;
		
	  public Connection (Socket aClientSocket, Services aS) {
	    try {
	    	  availableServices = aS;
	      clientSocket = aClientSocket;
	      in = new DataInputStream( clientSocket.getInputStream());
	      out = new DataOutputStream( clientSocket.getOutputStream());
	      parser = new JSONParser();
	      verify = new VerifyRequestObject();
	      System.out.println(clientSocket.getInetAddress());
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
		    		JSONObject reply = null;
		    		if(verify.checkCommand(command))
		  		{
		    			String commandText = command.get("command").toString();
		    			
		  			switch(commandText)
		    		{
		    		case "FETCH": 
		    			System.out.println("FETCH COMMAND RECEIVED");
		    			reply = availableServices.fetch(new Resource(command,commandText), out)
		    					.toJSON();
		    			break;
		    		case "QUERY":
		    			System.out.println("QUERY COMMAND RECEIVED");
		    			reply = availableServices.
		    					query(false, new Resource(command,commandText))
		    					.toJSON();
		    			break;
		    		case "PUBLISH":
		    			System.out.println("PUBLISH COMMAND RECEIVED");
		    			reply = availableServices.publish(new Resource(command,commandText))
		    					.toJSON();
		    			break;	
		    		case "SHARE":
		    			System.out.println("SHARE COMMAND RECEIVED");
		    			reply = availableServices.share(command.get("secret").toString(),
		    					new Resource(command,commandText))
		    					.toJSON();
		    			break;	
		    		case "REMOVE":
		    			System.out.println("REMOVE COMMAND RECEIVED");
		    			reply = availableServices.remove(new Resource(command,commandText))
		    					.toJSON();
		    			break;	
		    		
		    		default:
		    			reply =  new Response(false,"invalid command").toJSON();
		    			break;
		    		}	
		  		}
		  		else
		  		{
		  			reply = new Response(false,"Command missing").toJSON();
		  			break;
		  		}
		  		out.writeUTF(reply.toJSONString());
		    	}
		    }

		  }catch (EOFException e){
		     System.out.println("EOF:"+e.getMessage());
		  } catch(IOException e) {
		     System.out.println("readline:"+e.getMessage());
		  } catch(ParseException e){
			  System.out.println("Parse: " + e.getMessage());
		  } catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
		    try {
		      clientSocket.close();
		    }catch (IOException e){/*close failed*/}
	
		}
	}
	}


