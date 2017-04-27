package Server;
import java.net.*;
import java.util.ArrayList;

import org.json.simple.JSONArray;
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
	  Response reply;
	  boolean debug;
	  public Connection (Socket aClientSocket, Services aS,boolean debug) {
	    try {
	    	  this.debug = debug;
	    	  availableServices = aS;
	      clientSocket = aClientSocket;
	      in = new DataInputStream( clientSocket.getInputStream());
	      out = new DataOutputStream( clientSocket.getOutputStream());
	      parser = new JSONParser();
	      verify = new VerifyRequestObject();
	      reply = null;
	    } catch(IOException e) {
	       System.out.println("Connection:"+e.getMessage());
	} 
	    }
	public void run(){
		     boolean waitForMessage = false;     
		     try {
		     do
			{	    	
					waitForMessage = serveClient();
				
			}while(waitForMessage == true);
			send(reply);
			
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
	}
	boolean serveClient() throws IOException, ParseException
	{
		boolean result = true;
		 if(in.available() > 0){
	    		// Attempt to convert read data to JSON
			 System.out.println("server reading data from client ");
	    	 	JSONObject command = (JSONObject) parser.parse(in.readUTF());
	    	 	if(debug)
	    		System.out.println("COMMAND RECEIVED: "+command.toJSONString());		
	    		if(verify.existsCommand(command))
	  		{
	    			String cmdText = command.get("command").toString();
	    			if(verify.checkResource(command, cmdText))
	    			{
	    				performOperation(command);
	    			}
	    			else
	    			{
	    				reply = verify.getMissingResponse(cmdText);
	    			}
	  		}
	  		else
	  		{
	  			reply = new Response(false,"Command missing");
	  		}
	    		result = false;
	  	}
		return result;
	}
	
	void performOperation(JSONObject command)
	{
		try {
		String commandText = command.get("command").toString();
		commandText = commandText.toUpperCase();
		synchronized(availableServices)
		{
		switch(commandText)
		{
		case "FETCH": 
			System.out.println("FETCH COMMAND RECEIVED");
			reply = availableServices.fetch(new Resource(command,commandText), out);
			break;
		case "QUERY":
			System.out.println("QUERY COMMAND RECEIVED");
			Resource temp = new Resource(command,commandText);
			reply = availableServices.
					query(false, temp);
			break;
		case "PUBLISH":
			System.out.println("PUBLISH COMMAND RECEIVED");
			reply = availableServices.publish(new Resource(command,commandText));
			break;	
		case "SHARE":
			System.out.println("SHARE COMMAND RECEIVED");
			reply = availableServices.share(command.get("secret").toString(),
					new Resource(command,commandText));
			break;	
		case "REMOVE":
			System.out.println("REMOVE COMMAND RECEIVED");
			reply = availableServices.remove(new Resource(command,commandText));
			break;	
		case "EXHCHANGE":
			System.out.println("EXCHANGE COMMAND RECEIVED");
			reply = initiateExchange(command);
			break;
		default:
			reply =  new Response(false,"invalid command");
			break;
		}
		}
		  }catch (URISyntaxException | ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	Response initiateExchange(JSONObject command) throws ParseException, IOException
	{
		Response reply = null;
		JSONArray list = waitForNextMessage(true);
		//reply = availableServices.exchange(list);
		return reply;
	}
	void send(Response toSend) throws IOException
	{
		
		if(toSend != null)
		{
		JSONObject reply = toSend.toJSON();
		out.writeUTF(reply.toJSONString());
		if(debug)
		System.out.println(reply.toString());
		
		if(toSend.responseListIsEmpty() == false)
		{
			out.writeUTF(reply.toJSONString());
			JSONObject temp = null;
			int iterator = 0;
			ArrayList<JSONObject> replyList = toSend.getResponseListToJSON();
			for(JSONObject j:replyList)
			{
				iterator++;
				out.writeUTF(j.toJSONString());
				if(debug)
					System.out.println(j.toString());
			}
			
			temp = new JSONObject();
			temp.put("resultSize", iterator);
			out.writeUTF(temp.toJSONString());
			out.flush();
		}
		else {
			out.writeUTF(reply.toJSONString());
			if(debug)
				reply.toJSONString();
			out.flush();
		}
		}
		}
		
	JSONArray waitForNextMessage(boolean wait) throws ParseException, IOException{
		JSONObject response = new JSONObject();
		String data;
		if(wait)
		{
			JSONArray list = new JSONArray();
			do
		{
		data = in.readUTF();
    		// Attempt to convert read data to JSON
    		response = (JSONObject) parser.parse(data);
    		list.add(response);
		}while(response.containsKey("resultSize") == false);
			return list;
		}
		return null;
	}
		}


