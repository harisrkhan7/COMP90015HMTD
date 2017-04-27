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
	DataInputStream in;
	DataOutputStream out;
	JSONObject newCommand;
	ClientTCP(JSONObject cmd)
	{
		try{
		this.serverPort = 3345;
		this.newCommand = cmd;
		this.parser = new JSONParser();
		ClientSocket = new Socket("localhost", serverPort);
	    System.out.println("Connection Established");
	    this.in = new DataInputStream( ClientSocket.getInputStream());
	    this.out =new DataOutputStream( ClientSocket.getOutputStream());
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
	  }catch(NullPointerException e){
		  System.out.println("Server not found");
	  }
	  finally {
	     if(ClientSocket!=null) try {
	       ClientSocket.close();
	     }catch (IOException e){
	       System.out.println("close:"+e.getMessage());
	     }
	  }
	  
}
	boolean checkCommand(String command, String response)
	  {
		return (response.equals("success") && (command.equals("QUERY")||command.equals("FETCH")));
	  }
	JSONObject getFirstResponse() throws NullPointerException
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
		
	   
	    String commandText = newCommand.get("command").toString();
	    String data;
		try {
			out.writeUTF(newCommand.toJSONString( ));
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) 
		{
			System.out.println("Server not found");
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
