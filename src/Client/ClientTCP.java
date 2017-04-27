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
	DataInputStream in;
	DataOutputStream out;
	ClientTCP(int port, String[] inpmessage)
	{
		try{
		serverPort = port;
		this.clientArgument = inpmessage;
		parser = new JSONParser();
		commandParser = new ArgumentParser(clientArgument);
		ClientSocket = new Socket("localhost", serverPort);
	    System.out.println("Connection Established");
	    in = new DataInputStream( ClientSocket.getInputStream());
	    out =new DataOutputStream( ClientSocket.getOutputStream());
		}catch(IOException e){}
	}
	public void run()
	{
	  try{
		  
	    JSONObject response;
		boolean wait = false;
		String replyResponse = null;
		String commandText = parseAndSend();
		response = getFirstResponse();
		replyResponse = response.get("response").toString();
    		wait = (checkCommand(commandText, replyResponse));
    		waitForNextMessage(wait);
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
	boolean checkCommand(String command, String response)
	  {
		return (response.equals("success") && command.equals("QUERY"));
	  }
	JSONObject getFirstResponse() 
	{
		JSONObject response = null;
		try{
		String data = in.readUTF();
		// Attempt to convert read data to JSON
		response = (JSONObject) parser.parse(data);
		System.out.println(response.toString());
		}catch(IOException e){}
		catch(ParseException e){}
		return response;
		
	}
	String parseAndSend()
	{
		commandParser.parseInput();
	    JSONObject newCommand = commandParser.toJSON();
	    String commandText = newCommand.get("command").toString();
	    String data;
		try {
			out.writeUTF(newCommand.toJSONString( ));
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commandText;
		
	}
	void waitForNextMessage(boolean wait) throws ParseException, IOException{
		JSONObject response = new JSONObject();
		String data;
		if(wait)
		{
			do
		{
			data = in.readUTF();
    		// Attempt to convert read data to JSON
    		response = (JSONObject) parser.parse(data);
    		System.out.println(response.toString());
    		
		}while(response.containsKey("resultSize") == false);
		}
	}
}
