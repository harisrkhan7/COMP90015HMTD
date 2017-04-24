package Client;
import java.net.*;
import java.io.*;

import org.apache.commons.cli.CommandLine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Server.Response;
public class ClientTCP extends Thread {
	int serverPort;
	Socket ClientSocket;
	String clientArgument[];
	JSONParser parser;
	ArgumentParser commandParser;
	ClientTCP(int port, String[] inpmessage)
	{
		serverPort = port;
		this.clientArgument = inpmessage;
		parser = new JSONParser();
		commandParser = new ArgumentParser(clientArgument);
		
	}
	public void run()
	{
	  try{
	    Resource resource =  new Resource(null, null, "test");
		ClientSocket = new Socket("localhost", serverPort);
	    System.out.println("Connection Established");
	    DataInputStream in = new DataInputStream( ClientSocket.getInputStream());
	    DataOutputStream out =new DataOutputStream( ClientSocket.getOutputStream());
	    commandParser.parseInput();
	    JSONObject newCommand = commandParser.toJSON();
		//JSONObject newCommand = resource.toJSON();
		
	    System.out.println("before sending:"+newCommand.toString());
		// Send RMI to Server
		out.writeUTF(newCommand.toJSONString( ));
		out.flush();
		System.out.println("after sending:"+newCommand.toString());
		
		String data = in.readUTF();
    		// Attempt to convert read data to JSON
    		JSONObject response = (JSONObject) parser.parse(data);
    		System.out.println(response.toString());
	
	    ClientSocket.close();
	  }catch (ParseException e)
	     {
		  System.out.println("Parse:"+e.getMessage());
	     }
	  catch (UnknownHostException e) {
	     System.out.println("Socket:"+e.getMessage());
	  }catch (EOFException e){
	     System.out.println("EOF:"+e.getMessage());
	  }catch (IOException e){
	     System.out.println("readline:"+e.getMessage());
	  }finally {
	     if(ClientSocket!=null) try {
	       ClientSocket.close();
	     }catch (IOException e){
	       System.out.println("close:"+e.getMessage());
	     }
	  }
}
}
