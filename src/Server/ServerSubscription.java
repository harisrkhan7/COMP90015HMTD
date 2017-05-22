package Server;

import java.net.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
public class ServerSubscription extends Thread {
	private ObjectServer server;	
	private int debug;
	Connection connection;
	InetAddress serverAddress ;
	Socket ClientSocket;
	DataInputStream in;
	DataOutputStream out;
	ResourceServer resourceTemplate;
	String id;
	ServerSubscription(ObjectServer server, ResourceServer resourceTemplate, Connection c,
			String id)
	{
		this.id = id;
		this.server = server;
		this.resourceTemplate = resourceTemplate;
		this.connection = c;
		try{
		ClientSocket = new Socket(server.getHostname(), server.getPort());
		in = new DataInputStream( ClientSocket.getInputStream());
		out = new DataOutputStream( ClientSocket.getOutputStream());
		}catch(IOException e)
		{
			System.out.println("Could not establish I/O");
		}
	}
	
	public void run() 
	{	
		try{
		JSONObject temp = new JSONObject();
		temp.put("resourceTemplate", resourceTemplate.toJSON());
		temp.put("id", this.id);
		temp.put("command", "SUBSCRIBE");
		out.writeUTF(temp.toJSONString());
		if(true)
		{
			System.out.println(temp.toString());
		}
		String data;
		JSONObject response = new JSONObject();
		JSONParser parser = new JSONParser();
		while(response.containsKey("resultSize") == false)
		{
			if(in.available()>0)
			{
				data = in.readUTF();
				// Attempt to convert read data to JSON
				response = (JSONObject) parser.parse(data);
				if(true)
				{
				System.out.println("RECEIVED:"+response.toString());
				}
				if(response.containsKey("response")==false &&
						response.containsKey("resultSize")==false)
				{
					connection.sendJSON(response);
					connection.addHit();
				}
    			}
		}
		}catch(IOException | ParseException e)
		{
			
		}
	}
	
	public void Unsubscribe()
	{
		JSONObject temp = new JSONObject();
		temp.put("id", this.id);
		temp.put("command", "UNSUBSCRIBE");
		try{
		out.writeUTF(temp.toJSONString());
		out.flush();
		}catch(IOException e)
		{
			
		}
		if(true)
		{
			System.out.println(temp.toString());
		}
	}
	/**
	 * @return the server
	 */
	public ObjectServer getServer() {
		return server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(ObjectServer server) {
		this.server = server;
	}
}
